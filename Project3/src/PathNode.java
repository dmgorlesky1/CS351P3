import java.util.*;

public class PathNode implements Comparable {
    /** Value the PathNode holds */
    private int value;

    /** An ArrayList of vertex IDs ordered by appearance in the path. */
    private ArrayList<Integer> path;

    /** Reference to the left child. */
    private PathNode left;

    /** Reference to the right child. */
    private PathNode right;

    /** Reference to the parent. */
    private PathNode parent;

    /** Reference to the node directly to the right on the same tree level. */
    private PathNode generationRight; // right sibling or cousin

    /** True if the node is last in the level. */
    private boolean isLevelEnd;

    /** True if the node is the right-most node in the last level. */
    private boolean isLastNode;


    public PathNode(){
        this.value = 0;
        this.path = null;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.isLevelEnd = false;
        this.isLastNode = false;
        this.generationRight = null;
    }

    public PathNode(int value){
        this.value = value; // change later
        this.path = null;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.isLevelEnd = false;
        this.isLastNode = false;
        this.generationRight = null;
    }

    public PathNode(ArrayList<Integer> path, PathNode left, PathNode right,
                    boolean isLevelEnd, boolean isLastNode, PathNode parent){
        this.value = 0;
        this.path = path;
        this.left = left;
        this.right = right;
        this.parent = parent;
        this.isLevelEnd = isLevelEnd;
        this.isLastNode = isLastNode;
        this.generationRight = null;
    }


    public boolean hasLeft(){
        if(this.left != null){
            return true;
        }
        return false;
    }

    public boolean hasRight(){
        if(this.right != null){
            return true;
        }
        return false;
    }

    public int getValue() {
        return this.value;
    }

    public void setValue(int value){
        this.value = value;
    }

    public boolean isLastNode() {
        return isLastNode;
    }

    public void setLastNode(boolean lastNode) {
        isLastNode = lastNode;
    }

    public PathNode getLeft() {
        return left;
    }

    public void setLeft(PathNode left) {
        this.left = left;
    }

    public boolean isLevelEnd() {
        return isLevelEnd;
    }

    public void setLevelEnd(boolean levelEnd) {
        isLevelEnd = levelEnd;
    }

    public PathNode getGenerationRight() {
        return generationRight;
    }

    public void setGenerationRight(PathNode generationRight) {
        this.generationRight = generationRight;
    }

    public PathNode getParent() {
        return parent;
    }

    public void setParent(PathNode parent) {
        this.parent = parent;
    }

    public PathNode getRight() {
        return right;
    }

    public void setRight(PathNode right) {
        this.right = right;
    }

    /**NOT SURE IF THESE 2 ARE NEEDED */
    public ArrayList<Integer> getPath() {
        return path;
    }

    /**NOT SURE IF THESE 2 ARE NEEDED */
    public void setPath(ArrayList<Integer> path) {
        this.path = path;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}

