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
        ArrayList<TablutMove> moves = boardState.getAllLegalMoves();
        TablutMove myMove = moves.get((int)(Math.random()*moves.size()));
        int depth=3;
        myMove = MyTools.abPrune(boardState, depth);
        //myMove = MyTools.forwardPruneMin(boardState, depth, 10);
        // Return your move to be processed by the server.
       // System.out.println(MyTools.kingMoves(boardState));
        return myMove;
    }
}