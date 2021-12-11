import java.util.*;
import java.io.*;

/**
 * This is the Heap class meant to make a binary tree from user input then performs a
 * min heap on the tree. It then formats the nodes based off their generation links
 * and writes them to .dot files to be displayed in images.
 * @author Dillon Gorlesky
 * @author Johathyn Strong
 * @version 1.0 - 12/09/2021
 */
public class Heap {
    /** Temporary storage for the paths starting at tempPath[1]. */
    private final ArrayList<PathNode> tempPath;

    /** Output files for before and after the heapify of a tree */
    private File before, after;

    /** The name of the output files to be written to*/
    private String label;

    /** Root of the tree */
    private PathNode root;

    /** Size of the tree */
    private int treeDepth;

    /** This reads in the file */
    private Scanner scan;

    /** For a constant of value 1 */
    static final int ONE = 1;

    /** For a constant of value 0 */
    static final int ZERO = 0;

    /**
     * Constructor for a new Heap
     */
    public Heap(){
        this.tempPath = new ArrayList<>();
        this.root = null;
        this.treeDepth = 0;
        this.label = "";
    }

    /**
     * Reads inputFile given at the command line and places the contents of each line into the
     * path field found in each PathNode object. The order is the same as found in the text file.
     * Adds the PathNode object to tempPath starting at tempPath[1].
     *
     * @param inputFile Name of the input file to be read.
     */
    public void readPaths(String inputFile) {
        try{//Making a new scanner
            File file = new File(inputFile);
            scan = new Scanner(file);
        } catch(FileNotFoundException noFile){
            System.out.println("File not found. Please check input and try again.");
            System.out.println("Usage: java Driver file.txt <label>");
            scan.close();
            System.exit(1);
        }
        boolean check;
        StringBuilder lining;
        String[] line;
        PathNode node;
        while(scan.hasNextLine()){//Skips empty lines and tabs
            check = false;
            lining = new StringBuilder();
            line = scan.nextLine().split("\\s+");//splits line to get length
            for (String s : line) {
                if (!s.equals("")) {
                    lining.append(s).append(" ");
                    check = true;
                } else {
                    check = false;
                }
            }
            if(check) {
                //New node added
                node = new PathNode(lining.toString().split(" ").length - 1);
                node.setPath(settingPath(lining.toString().split(" ")));
                this.tempPath.add(node);
            }
        }
        //Empty file
        if(this.tempPath.size() < 1) {
            System.out.print("Error: File given is empty. Please retry.");
            System.out.println("Usage: java Driver file.txt <label>");
            scan.close();
            System.exit(1);
        }
    }

    /**
     * Sets the that each node follows
     * @param pathFound Read in path associated to the Node
     * @return List of the paths a node follows
     */
    public ArrayList<Integer> settingPath(String[] pathFound){
        ArrayList<Integer> newList = new ArrayList<>();
        for (String s : pathFound) {
            newList.add(Integer.parseInt(s));
        }
        return newList;
    }


    /**
     * Recursively builds a complete binary tree. Places PathNode objects in tempPath ArrayList
     * into a complete binary tree in order of appearance in the text file.
     *
     * @param index Index of the current node in tempPath.
    //     * @param parent Parent of the current node.
     * @return A reference to the node just placed in the tree.
     */
    public PathNode buildCompleteTree(int index) {//, int parent) {
        PathNode node = new PathNode();
        PathNode node2 = new PathNode();
        if(this.tempPath.size() > 2 * index + 1){
            node = buildCompleteTree(2 * index + 1);//, index);
        }
        if(this.tempPath.size() > 2 * index + 2){
            node2 = buildCompleteTree(2 * index + 2);//, index);
        }

        this.tempPath.get(index).setLeft(node);
        node.setParent(this.tempPath.get(index));
        this.tempPath.get(index).setRight(node2);
        node2.setParent(this.tempPath.get(index));

        return this.tempPath.get(index);
    }

    /**
     * Recursive method that sets isLevelEnd.
     * Will only ever be the left most nodes on the tree.
     * @param root Root of the subtree.
     */
    public void setLevelEnd(PathNode root){
        if(root != null){
            setLevelEnd(root.getLeft());
            root.setLevelEnd(true);
        }
    }

    /**
     * Recursive method that sets the "generation" link of PathNode objects from right-to-left.
     * generation is a term I use to indicate nodes on the same level (these may be siblings or
     * cousins)
     * @param root Root of the subtree.
     */
    public void setGenerationLinks(PathNode root){
        //Root of entire tree
        if(root.getParent() == null){
            if(root.hasRight()){
                root.getLeft().setGenerationRight(root.getRight());
                setGenerationLinks(root.getLeft());
                setGenerationLinks(root.getRight());
            }
        } else if(root.hasRight()){
            //Left --> Right
            root.getLeft().setGenerationRight(root.getRight());
            //Right --> Parent --> Parents generation link --> Left
            if(root.getGenerationRight() != null){
                if(root.getGenerationRight().hasLeft()){
                    root.getRight().setGenerationRight(root.getGenerationRight().getLeft());
                }
            }
            //Call both to set rest
            if(root.hasLeft()){
                setGenerationLinks(root.getLeft());
            }
            if(root.hasRight()){
                setGenerationLinks(root.getRight());
            }
        }
    }


    /**
     * Prints the path lengths from left-to-right at each level in the tree in the form specified
     * by the instructions.
     * Outputs graph to the before and after file depending on parameter.
     * If time == 0, write to before
     * Else time == 1, write to after
     * @param time Which file needs to be written to.
     */
    public void printTreeLevels(int time){
        try {
            FileWriter fileBefore;
            if(time == 0){
                fileBefore = new FileWriter(this.before.getName());
                fileBefore.write("digraph " + label + "Before{\n");
            } else {
                fileBefore = new FileWriter(this.after.getName());
                fileBefore.write("digraph " + label + "After{\n");
            }
            // Outputs the first half of the file
            fileBefore.write(doWhile());

            // Outputs the second half of the file
            fileBefore.write(doArrows());

            fileBefore.write("}");
            fileBefore.close();
        } catch(IOException e) {
            System.out.println("An error occurred in outputBefore file.");
            System.out.println("Usage: java Driver file.txt <label>");
            e.printStackTrace();
            scan.close();
            System.exit(1);
        }
    }


    /**
     * Creates before and after files based off of the given filename
     * @param filename First part of the output filename
     */
    public void createOutputFiles(String filename) {
        try {
            this.before = new File(filename + "Before.dot");
            this.after = new File(filename + "After.dot");

            if (!this.before.createNewFile()) {
                System.out.println("WARNING: " + this.before.getName() +
                        " already exists. Overwriting.");
            }
            if (!this.after.createNewFile()) {
                System.out.println("WARNING: " + this.after.getName() +
                        " already exists. Overwriting.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred in createOutputFiles");
            System.out.println("Usage: java Driver file.txt <label>");
            e.printStackTrace();
            scan.close();
            System.exit(1);
        }
    }

    /**
     * Formats the bottom part of the graph printout
     * @return Bottom part of graph printout
     */
    public String doArrows(){
        StringBuilder msg = new StringBuilder();
        int len = tempPath.size()-1;
        int i1 = 1;
        int i2 = 0;
        for(int i = 0; i < len; i++){
            if(i1 < len) {
                msg.append("\t").append(i2).append(" -> ").append(i1).append(";\n");
                i1++;
                msg.append("\t").append(i2).append(" -> ").append(i1).append(";\n");
                i2++;
                i1++;
            }
        }
        if(len % 2 != 0){
            msg.append("\t").append(i2).append(" -> ").append(i1).append(";\n");
        }
        return msg.toString();
    }

    /**
     * Formats the top part of the graph printout
     * @return Top part of graph printout
     */
    public String doWhile(){
        StringBuilder msg = new StringBuilder();
        PathNode node = root;
        PathNode node2 = null;
        msg.append(getFormat(node, 0));
        int i = 1;
        //Gets the nodes links for connections
        while(node.getLeft() != null){
            while(node2 != null){
                if(node2.getGenerationRight() != null){
                    msg.append(getFormat(node2, i));
                    node2 = (node2 == node2.getGenerationRight()) ? null : node2.getGenerationRight();
                } else {
                    msg.append(getFormat(node2, i));
                    node2 = null;
                }
                i++;
            }
            node = node.getLeft();
            node2 = node.getGenerationRight();
            msg.append(getFormat(node, i));
            i++;
        }
        //Gets last levels nodes by generation
        node2 = node.getGenerationRight();
        while(node2 != null){
            if(node2.getGenerationRight() != null) {
                if (node2.getGenerationRight() != null) {
                    msg.append(getFormat(node2, i));
                    i++;
                    node2 = (node2 == node2.getGenerationRight()) ? null : node2.getGenerationRight();
                } else {
                    msg.append(getFormat(node2, i));
                    i++;
                    node2 = null;
                }
            } else {
                msg.append(getFormat(node2, i));
                i++;
                node2 = null;
            }
        }
        return msg.toString();
    }

    /**
     * Formats a node's path for the graph printout
     * @param node Current node
     * @param i Index of current node in graph
     * @return Formatted output of current nodes path
     */
    public String getFormat(PathNode node, int i){
        StringBuilder msg = new StringBuilder();
        if(node.getPath() != null){
            // Outputs first index value
            msg.append("\t").append(i).append("[label=\"");
            // Outputs the number of edges between nodes
            msg.append(node.getPath().size() - 1).append("(");
            // Outputs the first path value
            msg.append(node.getPath().get(ZERO));
            // Outputs every path value after
            for (int j = 1; j < node.getPath().size(); j++) {
                msg.append(", ").append(node.getPath().get(j));
            }
            // Outputs the ending styling
            msg.append(")\"];\n");
        }
        return msg.toString();
    }

    /**
     * Min sorts the binary tree
     *     steps to get starting node in min sort
     *     1) traverse left child till you reach the end
     *     2) save that node to CurrentLevel
     *     3) move to the right through the generation till you reach the last added node
     *     4) check the value of the last added node to the value of the nodes parent
     *       -if the value is smaller swap with the parent and continue
     *       -else do nothing
     *        a        b
     *       / \  ->  / \
     *      b        a
     */
    public void minSort() {
        // Cycling through the levels of the tree
        for (int level = this.treeDepth-1; level >= 0; level--) {
            int levelSize = findLevelSize(getLevel(this.root, level));
            PathNode currentLevel = getLevel(this.root, level);
            PathNode parentLevel = getLevel(this.root, level-1);
            // Cycling through the nodes of a level
            for (int levelPos = levelSize; levelPos >= 0; levelPos--) {
                // Bottom node
                PathNode b = getLevelNode(currentLevel, levelPos);
                PathNode bLeft = getLeftNode(currentLevel, b);

                if (level == 1) {
                    flowDown(b.getParent(), 0);// should flow down the root
                    level = 0;
                } else {
                    try {
                        // Parent node
                        PathNode a = b.getParent();
                        PathNode aLeft = getLeftNode(parentLevel, a); // Causes null PointerException
                        if (level==0) {
                            System.out.println(a);
                            if(a != null) {
                                root = a;
                            }
                        }

                        if (b.getValue() < a.getValue()) {
                            bubbleUp(a, aLeft, b, bLeft, level);
                        }
                    } catch (NullPointerException e) {
                     //   System.out.println("a is NULL");
                    }
                }
            }
        }
    }

    /**
     * Find a node in a level based off of an index
     * @param levelRoot Root node of the level
     * @param index Index of wanted node
     * @return Node at index in level
     */
    public PathNode getLevelNode(PathNode levelRoot, int index) {
        // Loop till moved to wanted level
        if (index == 0) {
            return levelRoot;
        } else {
            return getLevelNode(levelRoot.getGenerationRight(), (index-1));
        }
    }

    /**
     * Find the total depth of the tree using the root
     * @param root Root of a tree
     */
    public void findTreeDepth(PathNode root) {
        if (root == null) {
            return;
        }
        this.treeDepth++;
        findTreeDepth(root.getLeft());
    }

    /**
     * Find the length of the current level
     * @param level Starting node of the level
     * @return Size of the length
     */
    public int findLevelSize(PathNode level) {
        int levelSize = 0;
        while (level != null && level.getGenerationRight() != null) {
            if (levelSize == 9) {
                System.out.println("Infinite loop detected");
                break;
            }
            levelSize++;
            level = level.getGenerationRight();
        }
        return levelSize;
    }

    // Vertical movement, thinks of levels as a linked list using the leftmost node as the
    // starting node
    /**
     * Travels the graph to the left to get the starting node of a wanted level
     * @param root Root of the tree
     * @param index Index of the level to stop at
     * @return Starting node of the selected node
     */
    public PathNode getLevel(PathNode root, int index) {
        if (index < this.treeDepth) {
            if (index <= 0) {
                return root;
            }
            return getLevel(root.getLeft(), (index-1));
        } else {
            return root;
        }
    }

    // Horizontal movement, thinks the level is a LinkedList and uses getGenerationalRight to
    // move through it
    /**
     * Horizontal movement of the levels, used to find the node right before currentNode
     * @param levelNode Starting node in the level
     * @param currentNode Node to find the left generational node of
     * @return Left generational node of the currentNode
     */
    public PathNode getLeftNode(PathNode levelNode, PathNode currentNode) {
        if (levelNode == currentNode || levelNode.getGenerationRight() == null) {
            return levelNode;
        }

        if (levelNode.getGenerationRight().equals(currentNode)) {
            return levelNode;
        }
        return getLeftNode(levelNode.getGenerationRight(), currentNode);
    }

    /**
     * Flows down a node if it is larger then one of its children
     * @param a Current node
     * @param currentLevel level of the current node
     */
    public void flowDown(PathNode a, int currentLevel) {
        System.out.println("size" + tempPath.size());
        if(a.getLeft() != null) {
            if(a.getRight() != null) {
                PathNode smallestChild = (a.getLeft().getValue() < a.getRight().getValue()) ?
                        a.getLeft() : a.getRight();
                if (a.getRight() != null) {
                    if (a.getLeft().getValue() == a.getRight().getValue()) {
                        smallestChild = a.getLeft();
                    }
                }
                if (a.getValue() > smallestChild.getValue()) {
                    swapNodes(a, smallestChild, 1);
                }
            } else {
                if(a.getValue() > a.getLeft().getValue()){
                    swapNodes(a, a.getLeft(), 1);
                }
            }
        }
    }

    public void swapNodes(PathNode a, PathNode b, int mode) {
       // System.out.println("swapping nodes: a = " + a.getValue() + " and b = " + b.getValue());
        PathNode c = a.getParent();
        //If c isn't root
        if (c != null) {
            if (c.getLeft() == a) {
                c.setLeft(b);
            } else {
                c.setRight(b);
            }
        } else {//a is root
            if(a.getLeft() != null){
                if (a.getRight() == b) {
                    a.setGenerationRight(a.getRight().getGenerationRight());
                    a.getLeft().setGenerationRight(a);
                } else {
                    a.setGenerationRight(b.getGenerationRight());
                    b.setGenerationRight(null);
                }
                this.root = b;
            }
        }

        // Swapping b.parent = a.parent and a.parent = b
        b.setParent(a.getParent());
        a.setParent(b);

        setGenerationLinks(root);

        // Swapping a.child -> b
        if (a.getLeft() == b) {
            PathNode tempRight = a.getRight();
            a.setLeft(b.getLeft());
            b.setLeft(a);

            // Sets the parent of the child opposite b to b
            if(tempRight != null) { tempRight.setParent(b); }
            a.setRight(b.getRight());
            b.setRight(tempRight);
            a.setGenerationRight(tempRight);

        } else { // Else b is on the right
            PathNode tempLeft = a.getLeft();
            a.setRight(b.getRight());
            b.setRight(a);

            tempLeft.setParent(b); // Sets the parent of the child opposite b to b
            tempLeft.setGenerationRight(a);
            a.setLeft(b.getLeft());
            b.setLeft(tempLeft);
        }
        doMode1(a, b, mode);
//        if (mode == 0) {
//            if (b.getValue() < b.getParent().getValue()) {
//                a = b.getParent();
//                swapNodes(a, b, 1);
//            }
//        }
//        if(mode == 1) {
//            PathNode smallestChild = null;
//            if (a.getLeft() == null) {
//                smallestChild = a.getRight();
//            } else if (a.getRight() == null) {
//                smallestChild = a.getLeft();
//            } else {
//                smallestChild = (a.getLeft().getValue() < a.getRight().getValue()) ?
//                        a.getLeft() : a.getRight();
//            }
//            if (smallestChild != null) {
//                if (a.getValue() > smallestChild.getValue()) {
//                    swapNodes(a, smallestChild, 1);
//                }
//            }
//        }
    }

    /**
     * This is a helper function for swapNodes to handle different mode values
     * @param a Parent node being checked if needs to swapped
     * @param b Child node being checked if needs to swapped
     * @param mode if it's the root or not
     */
    public void doMode1(PathNode a, PathNode b, int mode){
        if (mode == 0) {
            if (b.getValue() < b.getParent().getValue()) {
                a = b.getParent();
                swapNodes(a, b, 1);
            }
        }
        if(mode == 1) {
            PathNode smallestChild = null;
            if (a.getLeft() == null) {
                smallestChild = a.getRight();
            } else if (a.getRight() == null) {
                smallestChild = a.getLeft();
            } else {
                smallestChild = (a.getLeft().getValue() < a.getRight().getValue()) ?
                        a.getLeft() : a.getRight();
            }
            if (smallestChild != null) {
                if (a.getValue() > smallestChild.getValue()) {
                    swapNodes(a, smallestChild, 1);
                }
            }
        }
    }

    /**
     * Bubbles up a node if it is less then its parent
     * @param a Parent of node to the swapped
     * @param aLeft The node to the left of the parent node
     * @param b Current node to be swapped
     * @param bLeft The node to the left of the current node
     */
    private void bubbleUp(PathNode a, PathNode aLeft, PathNode b, PathNode bLeft,
                          int currentLevel) {
        PathNode tempGenRight = a.getGenerationRight();
        a.setGenerationRight(b.getGenerationRight());
        b.setGenerationRight(tempGenRight);

        // Swapping a's genLeft with b and b's genLeft with a
        if (aLeft != a) {
            aLeft.setGenerationRight(b);
        }
        if (bLeft != b) {
            bLeft.setGenerationRight(a);
        }
        swapNodes(a, b, 0);
    }

    /**
     * Cleans tempPath Nodes, replacing filler values with null
     */
    public void doCleanup(){
        for (PathNode pathNode : tempPath) {
            if (pathNode.getLeft().getValue() == 9999) {
                pathNode.setLeft(null);
            }
            if (pathNode.getRight().getValue() == 9999) {
                pathNode.setRight(null);
            }
        }
    }

    /**
     * This function is a tester function for printing the levels of the tree
     */
    public void print(){
        PathNode t = root;
        PathNode t2 = t.getGenerationRight();
        System.out.println("\n\n\n");
        while(t != null){
            System.out.println("Node value is: " + t.getValue() + "(" + t.getPath() + ")");
            while(t2 != null){
                System.out.println("Generation link: " + t2.getValue()+"(" + t2.getPath() + ")");
                t2 = t2.getGenerationRight();
            }
            t = t.getLeft();
            System.out.println(t);
            if(t != null) t2 = t;
        }
    }

    /**
     * Runs all the functions to read in the input file, build a tree, initialize all fields such
     * as isLevelEnd, Generation links, etc., then performs a min heap and writes a before
     * and after to a dot file to make a visualization from.
     * @param inputFilename Name of file with data points in it
     * @param outputFilename Name to create output files off of
     */
    public void go(String inputFilename, String outputFilename) {
        readPaths(inputFilename);
        buildCompleteTree(ZERO);
        this.root = this.tempPath.get(ZERO);
        this.label = outputFilename;
        setLevelEnd(root);
        //Set last node
        this.tempPath.get(this.tempPath.size()-1).setLastNode(true);

        //Set generation links
        setGenerationLinks(root);

        doCleanup();

        createOutputFiles(outputFilename);

        printTreeLevels(ZERO);

        // Finds the depth of the tree
        findTreeDepth(this.root);

        minSort();

        printTreeLevels(ONE);
    }
}

