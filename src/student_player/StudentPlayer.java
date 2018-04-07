package student_player;

import coordinates.*;
import tablut.*;

import java.util.*;

/** A player file submitted by a student. */
public class StudentPlayer extends TablutPlayer {

    /**
     * You must modify this constructor to return your student number. This is
     * important, because this is what the code that runs the competition uses to
     * associate you with your agent. The constructor should do nothing else.
     */
    public StudentPlayer() {
        super("260671847");
    }

    /**
     * This is the primary method that you need to implement. The ``boardState``
     * object contains the current state of the game, which your agent must use to
     * make decisions.
     */
    public TablutMove chooseMove(TablutBoardState boardState) {
        // You probably will make separate functions in MyTools.
        // For example, maybe you'll need to load some pre-processed best opening
        // strategies...

        long start = System.nanoTime();
        ArrayList<TablutMove> moves = boardState.getAllLegalMoves();
        int numMoves = moves.size();
        int numMovesOpp = 0;
        //First loop through all possible moves to see if there exists a winning one
        for (TablutMove move : moves){
            TablutBoardState cbs = (TablutBoardState) boardState.clone();
            cbs.processMove(move);
            if (numMovesOpp == 0){
                numMovesOpp = cbs.getAllLegalMoves().size();
            }
            if (cbs.getWinner()==cbs.getOpponent()){
                return move;
            }
        }
        //Want to search 3 moves ahead, but if we suspect branching factor to be too high then only do two moves
        int branchEstimate = numMoves + numMoves*numMovesOpp + numMoves*numMovesOpp+numMoves;
        int depth = 3;
        if (branchEstimate>7400) {
            depth = 2;
        }
        //alpha-beta pruning
        //TablutMove myMove = MyTools.abPrune(boardState, depth);
        if (boardState.getTurnNumber()<=1){
            TablutMove myMove = MyTools.abPrune(boardState, depth);
            return myMove;
        }
        MyTools.timeOut = false;
        TablutMove myMove = MyTools.abPruneTimeCut(boardState, depth, start);
        // Return your move to be processed by the server.
        return myMove;
    }
}