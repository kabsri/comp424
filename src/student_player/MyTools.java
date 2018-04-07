package student_player;

import coordinates.*;
import tablut.*;

import java.util.*;

public class MyTools {

    //rating of how good each tile is for the king to be on
    public static int[][] kingPosWeights =
            {
            {1000,5   ,100 ,100 ,100 ,100 ,100 ,5  ,1000},
            {5   ,5   ,40  ,40  ,40  ,40  ,40  ,5  ,5   },
            {100 ,40  ,20  ,20  ,20  ,20  ,20  ,40 ,100 },
            {100 ,40  ,20  ,10  ,10  ,10  ,20  ,40 ,100 },
            {100 ,40  ,20  ,10  ,0   ,10  ,20  ,40 ,100 },
            {100 ,40  ,20  ,10  ,10  ,10  ,20  ,40 ,100 },
            {100 ,40  ,20  ,20  ,20  ,20  ,20  ,40 ,100 },
            {5   ,5   ,40  ,40  ,40  ,40  ,40  ,5  ,5   },
            {1000,5   ,100 ,100 ,100 ,100 ,100 ,5  ,1000}
            };

    //time threshold to cut off search
    public static long threshold = 1500000000;

    public static boolean timeOut = false;

    public static double getSomething() {
        return Math.random();
    }

    // Evaluate the state of the board, Swedes want to minimize and muscovites want to maximize
    public static int evaluate(TablutBoardState bs){
        int winner = bs.getWinner();
        if (winner == TablutBoardState.SWEDE) {
            return -100000;
        }else if (winner == TablutBoardState.MUSCOVITE){
            return 100000;
        }
        int musc = bs.getNumberPlayerPieces(TablutBoardState.MUSCOVITE);
        int swed = bs.getNumberPlayerPieces(TablutBoardState.SWEDE);
        Coord king = bs.getKingPosition();
        return 5*musc + 60*kingDanger(bs) - 8*swed - 5*kingMoves(bs) - 5*kingPosWeights[king.x][king.y] + ((int)(Math.random()*10)-5);
    }

    ///////////////////////////
    //alpha-beta pruning
    ///////////////////////////
    public static TablutMove abPrune(TablutBoardState state, int maxDepth){
        StateTree tree = new StateTree(state);
        if (state.getTurnPlayer()==TablutBoardState.SWEDE){
            int bestValue = abMin(tree, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
            ArrayList<StateTree> children = tree.getChildren();
            for (StateTree child : children){
                if (child.getValue()==bestValue){
                    return child.getPrevMove();
                }
            }
        }
        int bestValue = abMax(tree, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        ArrayList<StateTree> children = tree.getChildren();
        for (StateTree child : children){
            if (child.getValue()==bestValue){
                return child.getPrevMove();
            }
        }
        return state.getAllLegalMoves().get(0);

    }

    public static int abMin(StateTree s, int maxDepth, int alpha, int beta){
        if (s.getDepth() >= maxDepth || s.getState().gameOver()){
            return evaluate(s.getState());
        }
        s.expand();
        ArrayList<StateTree> children = s.getChildren();
        for (StateTree child: children){
            beta = Math.min(beta, abMax(child, maxDepth, alpha, beta));
            if (alpha >= beta){
                s.setValue(alpha);
                return alpha;
            }
        }
        s.setValue(beta);
        return beta;
    }

    public static int abMax(StateTree s, int maxDepth, int alpha, int beta){
        if (s.getDepth() >= maxDepth || s.getState().gameOver()){
            return evaluate(s.getState());
        }
        s.expand();
        ArrayList<StateTree> children = s.getChildren();
        for (StateTree child: children){
            alpha = Math.max(alpha, abMin(child, maxDepth, alpha, beta));
            if (alpha >= beta){
                s.setValue(beta);
                return beta;
            }
        }
        s.setValue(alpha);
        return alpha;
    }

    ///////////////////////////////////////////
    //alpha beta pruning with a time cutoff
    //////////////////////////////////////////
    public static TablutMove abPruneTimeCut(TablutBoardState state, int maxDepth, long start){
        StateTree tree = new StateTree(state);
        if (state.getTurnPlayer()==TablutBoardState.SWEDE){
            int bestValue = abMinTimeCut(tree, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, start);
            ArrayList<StateTree> children = tree.getChildren();
            for (StateTree child : children){
                if (child.getValue()==bestValue){
                    return child.getPrevMove();
                }
            }
        }
        int bestValue = abMaxTimeCut(tree, maxDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, start);
        ArrayList<StateTree> children = tree.getChildren();
        for (StateTree child : children){
            if (child.getValue()==bestValue){
                return child.getPrevMove();
            }
        }
        return state.getAllLegalMoves().get(0);
    }

    public static int abMinTimeCut(StateTree s, int maxDepth, int alpha, int beta, long start){
        if (s.getDepth() >= maxDepth || s.getState().gameOver()){
            return evaluate(s.getState());
        }
        if (timeOut || System.nanoTime()-start>threshold){
            System.out.println("Time!");
            timeOut = true;
            s.setValue(beta);
            return beta;
        }
        s.expand();
        ArrayList<StateTree> children = s.getChildren();
        for (StateTree child: children){
            beta = Math.min(beta, abMaxTimeCut(child, maxDepth, alpha, beta, start));
            if (timeOut){
                break;
            }
            if (alpha >= beta){
                s.setValue(alpha);
                return alpha;
            }
        }
        s.setValue(beta);
        return beta;
    }

    public static int abMaxTimeCut(StateTree s, int maxDepth, int alpha, int beta, long start){
        if (s.getDepth() >= maxDepth || s.getState().gameOver()){
            return evaluate(s.getState());
        }
        if (timeOut || System.nanoTime()-start>threshold){
            System.out.println("Time!");
            timeOut = true;
            s.setValue(alpha);
            return alpha;
        }
        s.expand();
        ArrayList<StateTree> children = s.getChildren();
        for (StateTree child: children){
            alpha = Math.max(alpha, abMinTimeCut(child, maxDepth, alpha, beta, start));
            if (timeOut){
                break;
            }
            if (alpha >= beta){
                s.setValue(beta);
                return beta;
            }
        }
        s.setValue(alpha);
        return alpha;
    }

    /////////////////////////
    // minimax
    /////////////////////////
    public static TablutMove minimaxDecision(TablutBoardState state, int maxDepth){
        StateTree tree = new StateTree(state);
        tree.expand();
        ArrayList<StateTree> children = tree.getChildren();
        int r = (int)(Math.random()*children.size());
        int bestValue = evaluate(children.get(r).getState());
        TablutMove minimaxMove = children.get(r).getPrevMove();
        //Take minimal move if Swedes
        if (state.getTurnPlayer() == TablutBoardState.SWEDE) {
            for (StateTree child : children) {
                int val = minimaxValue(child, maxDepth);
                if (val < bestValue) {
                    bestValue = val;
                    minimaxMove = child.getPrevMove();
                }
            }
            System.out.println(bestValue);
            return minimaxMove;
        }
        //Take maximal move if Muscovites
        for (StateTree child : children) {
            int val = minimaxValue(child, maxDepth);
            if (val > bestValue) {
                bestValue = val;
                minimaxMove = child.getPrevMove();
            }
        }
        //System.out.println(bestValue);
        return minimaxMove;
    }

    public static int minimaxValue(StateTree s, int maxDepth){
        if (s.getDepth() >= maxDepth){
            return evaluate(s.getState());
        }
        s.expand();
        ArrayList<StateTree> children = s.getChildren();
        int bestValue = evaluate(children.get(0).getState());
        //If Swede's turn then take minimum value
        if (s.getState().getTurnPlayer() == TablutBoardState.SWEDE) {
            for (StateTree child : children) {
                int m = minimaxValue(child, maxDepth);
                if (m < bestValue) {
                    bestValue = m;
                }
            }
            return bestValue;
        }
        //If Muscovite's turn then take maximum value
        for (StateTree child : children) {
            int m = minimaxValue(child, maxDepth);
            if (m > bestValue) {
                bestValue = m;
            }
        }
        return bestValue;
    }

    //forward pruning implementation that doesn't work
    public static TablutMove forwardPruneMin(TablutBoardState state, int maxDepth, int k){
        ArrayList<TablutMove> moves = state.getAllLegalMoves();
        TablutMove[] bestKMoves = new TablutMove[k];
        StateTree[] bestKStates = new StateTree[k];
        int[] bestKes = new int[k];
        int bestKThreshold=Integer.MAX_VALUE;
        int bestKIndex=0;
        for (int i=0; i<moves.size(); i++){
            TablutBoardState cbs = (TablutBoardState) state.clone();
            TablutMove move = moves.get(i);
            cbs.processMove(move);
            int e = evaluate(cbs);
            if (i==0){
                bestKMoves[i] = move;
                bestKStates[i] = new StateTree(cbs);
                bestKes[i] = e;
                bestKThreshold = e;
                bestKIndex = i;
            }else if (i<k){
                bestKMoves[i] = move;
                if (e>bestKThreshold){
                    bestKMoves[i] = move;
                    bestKStates[i] = new StateTree(cbs);
                    bestKes[i] = e;
                    bestKThreshold = e;
                    bestKIndex = i;
                }
            } else {
                if (e<bestKThreshold){
                    bestKMoves[bestKIndex] = move;
                    bestKStates[bestKIndex] = new StateTree(cbs);
                    bestKes[bestKIndex] = e;
                    bestKThreshold = bestKes[0];
                    bestKIndex = 0;
                    for (int j=0; j<k; j++){
                        if (bestKes[j]<bestKThreshold){
                            bestKThreshold = bestKes[j];
                            bestKIndex=j;
                        }
                    }
                }
            }
        }
        TablutMove bestMove = bestKMoves[0];
        int bestVal = Integer.MAX_VALUE;
        for (int i=0; i<k; i++){
            int val = abMin(bestKStates[i], maxDepth-1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if (val<bestVal){
                bestMove = bestKMoves[i];
            }
        }
        return bestMove;

    }

    // return whether the king is on the edge of the board or not
    public static int kingOnEdge(TablutBoardState bs){
        Coord king = bs.getKingPosition();
        if(king.x==0 || king.y==0 || king.x==8 || king.y==8){
            return 1;
        }
        return 0;
    }

    // return the number of moves the king has in the board state
    public static int kingMoves(TablutBoardState bs){
        TablutBoardState s = (TablutBoardState) bs.clone();
        Coord king = bs.getKingPosition();
        int up=0,right=0,down=0,left=0;
        for (int i=king.x-1; i>0; i--){
            if (bs.coordIsEmpty(Coordinates.get(i, king.y))){
                up++;
            } else {
                break;
            }
        }
        for (int i=king.x+1; i<9; i++){
            if (bs.coordIsEmpty(Coordinates.get(i, king.y))){
                down++;
            } else {
                break;
            }
        }
        for (int j=king.x-1; j>0; j--){
            if (bs.coordIsEmpty(Coordinates.get(king.x, j))){
                left++;
            } else {
                break;
            }
        }
        for (int j=king.x+1; j<9; j++){
            if (bs.coordIsEmpty(Coordinates.get(king.x, j))){
                up++;
            } else {
                break;
            }
        }
        return up+down+left+right;

    }

    //return number of muscovites that neighbour king
    public static int kingDanger(TablutBoardState bs){
        int n = 0;
        Coord king = bs.getKingPosition();
        List<Coord> neighbours = Coordinates.getNeighbors(king);
        for (Coord c: neighbours){
            if (bs.getPieceAt(c)== TablutBoardState.Piece.BLACK){
                n++;
            }
        }
        return n;
    }

}