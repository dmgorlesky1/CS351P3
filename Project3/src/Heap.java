import java.util.*;
import java.io.*;

/**
 *
 */
public class Heap {


    /** Temporary storage for the paths starting at tempPath[1]. */
    private ArrayList<PathNode> tempPath;


    public Heap(){}


    /**
     * Reads inputFile given at the command line and places the contents of each line into the
     * path field found in each PathNode object. The order is the same as found in the text file.
     * Adds the PathNode object to tempPath starting at tempPath[1].
     *
     * @param inputFile Name of the input file to be read.
     * @throws FileNotFoundException if the input file cannot be found.
     */
     //readPaths(String inputFile) throws FileNotFoundException{}


    /**
     * Recursively builds a complete binary tree. Places PathNode objects in tempPath ArrayList into a
     * complete binary tree in order of appearance in the text file.
     *
     * @param index Index of the current node in tempPath.
     * @param parent Parent of the current node.
     * @return A reference to the node just placed in the tree.
     */
    //PathNode buildCompleteTree(int index, int parent){}

    /**
     * Recursive method that sets isLevelEnd.
     * @param root Root of the subtree.
     */
    //setLevelEnd(PathNode root){}


    /**
     * Recursive method that sets the "generation" link of PathNode objects from right-to-left.
     * generation is a term I use to indicate nodes on the same level (these may be siblings or
     * cousins)
     * @param root Root of the subtree.
     */
    //setGenerationLinks(PathNode root){}

    /**
     * Prints the path lengths from left-to-right at each level in the tree in the form specified
     * by the instructions.
     * @param root Root of the whole tree to begin printing from.
     */
    //printTreeLevels(PathNode root){}
}
