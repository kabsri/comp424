package student_player;

import coordinates.*;
import tablut.*;

import java.util.*;

public class MyTools {

    public static double getSomething() {
        return Math.random();
    }

    public static TablutMove minimaxDecision(TablutBoardState state, int maxDepth){
        StateTree tree = new StateTree(state);
        tree.expand();
        ArrayList<StateTree> children = tree.getChildren();
        double bestValue = objectiveValueSwedes(children.get(0).getState());
        TablutMove minimaxMove = children.get(0).getPrevMove();
        //Take minimal move if Swedes
        if (state.getTurnPlayer() == TablutBoardState.SWEDE) {
            for (StateTree child : children) {
                double val = minimaxValue(child, maxDepth);
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
            double val = minimaxValue(child, maxDepth);
            if (val > bestValue) {
                bestValue = val;
                minimaxMove = child.getPrevMove();
            }
        }
        System.out.println(bestValue);
        return minimaxMove;
    }

    public static double minimaxValue(StateTree s, int maxDepth){
        if (s.getDepth() >= maxDepth){
            return objectiveValueSwedes(s.getState());
        }
        s.expand();
        ArrayList<StateTree> children = s.getChildren();
        double bestValue = objectiveValueSwedes(children.get(0).getState());
        //If Swede's turn then take minimum value
        if (s.getState().getTurnPlayer() == TablutBoardState.SWEDE) {
            for (StateTree child : children) {
                double m = minimaxValue(child, maxDepth);
                if (m < bestValue) {
                    bestValue = m;
                }
            }
            return bestValue;
        }
        //If Muscovite's turn then take maximum value
        for (StateTree child : children) {
            double m = minimaxValue(child, maxDepth);
            if (m > bestValue) {
                bestValue = m;
            }
        }
        return bestValue;
    }

    public static double objectiveValueSwedes(TablutBoardState bs){
        int winner = bs.getWinner();
        if (winner == TablutBoardState.SWEDE) {
            return -1;
        }else if (winner == TablutBoardState.MUSCOVITE){
            return 100;
        } else if (kingOnEdge(bs)){
            return 0;
        }
        int musc = bs.getNumberPlayerPieces(TablutBoardState.MUSCOVITE);
        return musc+1;
    }


    public static boolean kingOnEdge(TablutBoardState bs){
        Coord king = bs.getKingPosition();
        return king.x==0 || king.y==0 || king.x==8 || king.y==8;
    }

    public static boolean kingOnClearEdge(TablutBoardState bs){
        if (!kingOnEdge(bs)){
            return false;
        }
        Coord king = bs.getKingPosition();
        if (king.x==0){

        } else if (king.x==8){

        } else if (king.y == 0) {

        } else {

        }
        return true;

    }

}