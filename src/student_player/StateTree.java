package student_player;

import coordinates.*;
import tablut.*;

import java.util.*;

public class StateTree {

    private ArrayList<StateTree> children;
    private TablutBoardState state;
    private TablutMove prevMove;
    private int depth;

    public StateTree(TablutBoardState root){
        this(root, null, 0);
    }

    public StateTree(TablutBoardState root, TablutMove move, int depth){
        state = root;
        prevMove = move;
        this.depth = depth;
        children = new ArrayList<>();
    }

    public void expand(){
        for (TablutMove move: state.getAllLegalMoves()){
            TablutBoardState cbs = (TablutBoardState) state.clone();
            cbs.processMove(move);
            children.add(new StateTree(cbs, move, depth+1));
        }
    }

    public void expand(int depth){
        if (depth != 0){
            if (children.isEmpty()){
                expand();
            }
            for (StateTree child: children){
                child.expand(depth-1);
            }
        }
    }

    public TablutBoardState getState(){
        return state;
    }

    public TablutMove getPrevMove(){
        return prevMove;
    }

    public ArrayList<StateTree> getChildren(){
        return children;
    }

    public int getDepth(){
        return depth;
    }
}