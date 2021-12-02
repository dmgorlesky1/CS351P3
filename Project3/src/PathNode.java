import java.util.*;

/**
 * This class is meant to hold all the information and functions involving each individual
 * node with appropriate functions to get, set, or check if it has a valid value
 * @Author Dillon Gorlesky
 * @Author Johathyn Strong
 * @Version 1.0
 * @Date 12/01/2021
 */
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

    /**
     * Default constructor setting everything to null and the value to a designated value
     */
    public PathNode(){
        this.value = 9999;
        this.path = null;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.isLevelEnd = false;
        this.isLastNode = false;
        this.generationRight = null;
    }

    /**
     * Constructor for initializing a PathNode with a path value and nothing else
     * @param value path length being added to new node
     */
    public PathNode(int value){
        this.value = value;
        this.path = null;
        this.left = null;
        this.right = null;
        this.parent = null;
        this.isLevelEnd = false;
        this.isLastNode = false;
        this.generationRight = null;
    }

    /**
     * This function checks to see if the current node has a left-child node
     * @return true or false if child node found
     */
    public boolean hasLeft(){
        return this.left != null;
    }

    /**
     * This function checks to see if the current node has a right-child node
     * @return true or false if child node found
     */
    public boolean hasRight(){
        return this.right != null;
    }

    /**
     * Gets the value of the current node
     * @return int value of current node value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Sets the value of the current node
     * @param value new value of current node
     */
    public void setValue(int value){
        this.value = value;
    }

    /**
     * Checks if current node is also the last node
     * @return true or false if last node
     */
    public boolean isLastNode() {
        return isLastNode;
    }

    /**
     * Sets the current node to be the last node
     * @param lastNode true or false to be new last node
     */
    public void setLastNode(boolean lastNode) {
        isLastNode = lastNode;
    }

    /**
     * Gets left child of current node
     * @return left child of node
     */
    public PathNode getLeft() {
        return left;
    }

    /**
     * Sets left child of current node
     * @param left new left child
     */
    public void setLeft(PathNode left) {
        this.left = left;
    }

    /**
     * Checks to see if current node is a level end
     * @return true or false if level end
     */
    public boolean isLevelEnd() {
        return isLevelEnd;
    }

    /**
     * Checks to see if the current node has a generation connection
     * @return true or false if node has generation
     */
    public boolean hasGenerationRight(){
        return this.value != 9999;
    }

    /**
     * Sets level end of current node
     * @param levelEnd true or false for a level end
     */
    public void setLevelEnd(boolean levelEnd) {
        isLevelEnd = levelEnd;
    }

    /**
     * Gets right generation connection
     * @return node connected by a generation link
     */
    public PathNode getGenerationRight() {
        return generationRight;
    }

    /**
     * Sets the generation right node of current node
     * @param generationRight sibling or cousin of current node
     */
    public void setGenerationRight(PathNode generationRight) {
        this.generationRight = generationRight;
    }

    /**
     * Gets parent of current node
     * @return parent of node
     */
    public PathNode getParent() {
        return parent;
    }

    /**
     * Sets parent of current node
     * @param parent new parent
     */
    public void setParent(PathNode parent) {
        this.parent = parent;
    }

    /**
     * Gets right child of current node
     * @return right child of node
     */
    public PathNode getRight() {
        return right;
    }

    /**
     * Sets right child of current node
     * @param right new right child
     */
    public void setRight(PathNode right) {
        this.right = right;
    }

    /**
     * Gets the path of the current node
     * @return arraylist path of current node
     */
    public ArrayList<Integer> getPath() {
        return path;
    }

    /**
     * Sets path of current node
     * @param path new path of node
     */
    public void setPath(ArrayList<Integer> path) {
        this.path = path;
    }

    /**
     * Compares current node to object
     * @param o object to be compared to
     * @return 0 is true 1 and -1 is false
     */
    @Override
    public int compareTo(Object o) {
        // If the object is compared with itself then return true
        if (o == this) {
            return 0;
        }

        /* Check if o is an instance of PathNode or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof PathNode)) {
            return -1;
        }

        // typecast o to Complex so that we can compare data members
        PathNode c = (PathNode) o;

        // Compare the data members and return accordingly
        if(this.getValue() == c.getValue()){
            if(this.getPath() == c.getPath()){
                if(this.getGenerationRight() == c.getGenerationRight()){
                    if(this.getParent() == c.getParent()){
                        return 0;
                    }
                }
            }
        }
        return -1;
    }
}

