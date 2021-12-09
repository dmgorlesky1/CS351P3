import java.util.*;
import java.io.*;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
    //TODO maybe delete parent parameter
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
        System.out.println("Running printTreeLevels...");
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
            //this.before = new File(filename + "Before.txt");//Just using these for testing
            //this.after = new File(filename + "After.txt");
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
        System.out.println("Running doWhile...");
        StringBuilder msg = new StringBuilder();
        PathNode node = this.root;
        PathNode node2 = null;
        msg.append(getFormat(node, 0));
        int i = 1;
        while(node.getLeft() != null){
            System.out.println("node.getLeft() value = " + node.getLeft().getValue());
            while(node2 != null){
                if(node2.getGenerationRight() != null){
                    msg.append(getFormat(node2, i));
                    node2 = node2.getGenerationRight();
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
        node2 = node.getGenerationRight();
        while(node2 != null){
            if(node2.getGenerationRight() != null) {
                if (node2.getGenerationRight() != null) {
                    msg.append(getFormat(node2, i));
                    i++;
                    node2 = node2.getGenerationRight();
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

    // REPLACED WITH swapNodes()
//    public boolean swap(PathNode node){
//        int left = node.getLeft().getValue();
//        int right = node.getRight().getValue();
//        int curr = node.getValue();
//
//        if(left < right){
//            if(left < curr){
//                return true;
//            }
//        } else {
//            if(right < curr){
//                return true;
//            }
//        }
//
//        return false;
//    }

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
        for (int i = this.treeDepth-1; i >= 0; i--) { // Cycling through the levels of the tree
            int levelSize = findLevelSize(getLevel(this.root, i));
            PathNode currentLevel = getLevel(this.root, i);
            PathNode parentLevel = getLevel(this.root, i-1);
            for (int j = levelSize; j >= 0; j--) { // Cycling through the nodes of a level
                System.out.println("i = " + i + "  j = " + j);
                // Bottom node
                PathNode b = getLevelNode(currentLevel, j);
                PathNode bLeft = getLeftNode(currentLevel, b);

                // Parent node
                PathNode a = b.getParent();
                PathNode aLeft = getLeftNode(parentLevel, a); // Causes null PointerException

                if (i==0) {
                    this.root = a;
//                    break;
                }

                if (b.getValue() < a.getValue()) {
                        swapNodes(a, aLeft, b, bLeft);
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
        System.out.println("findLevelSize running...");
        while (level != null && level.getGenerationRight() != null) {
//            System.out.println("level.getValue = " + level.getValue() +
//                    "\n level.getGenerationRight() = ");
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
     * Swaps 2 given nodes
     * @param a Parent of node to the swapped
     * @param aLeft The node to the left of the parent node
     * @param b Current node to be swapped
     * @param bLeft The node to the left of the current node
     */
    private void swapNodes(PathNode a, PathNode aLeft, PathNode b, PathNode bLeft) {
        System.out.println("swapping nodes: a = " + a.getValue() + " and b = " + b.getValue());
        // Swapping a.parent.child = b
        PathNode c = a.getParent();
        if (c != null) {
            if (c.getLeft() == a) {
                c.setLeft(b);
            } else {
                c.setRight(b);
            }
        }

        // Swapping b.parent = a.parent and a.parent = b
        b.setParent(a.getParent());
        a.setParent(b);

        // Swapping a.child -> b
        if (a.getLeft() == b) {
            PathNode tempRight = a.getRight();
            a.setLeft(b.getLeft());
            b.setLeft(a);

            tempRight.setParent(b); // Sets the parent of the child opposite b to b
            a.setRight(b.getRight());
            b.setRight(tempRight);
        } else { // Else b is on the right
            PathNode tempLeft = a.getLeft();
            a.setRight(b.getLeft());
            b.setRight(a);

            tempLeft.setParent(b); // Sets the parent of the child opposite b to b
            a.setLeft(b.getLeft());
            b.setLeft(tempLeft);
        }

        // Swapping a.genRight = b.genRight and b.genRight = a.genRight
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
     * Runs all the functions to read in the input file, build a tree, initialize all fields such
     * as isLevelEnd, Generation links, etc., then performs a min heap and writes a before
     * and after to a dot file to make a visualization from.
     * @param inputFilename Name of file with data points in it
     * @param outputFilename Name to create output files off of
     */
    public void go(String inputFilename, String outputFilename) {
        readPaths(inputFilename);
        buildCompleteTree(ZERO);//,ZERO);
        this.root = this.tempPath.get(ZERO);
        this.label = outputFilename;

        setLevelEnd(root);
        //Set last node
        this.tempPath.get(this.tempPath.size()-1).setLastNode(true);

        //Set generation links
        setGenerationLinks(root);

        doCleanup();
        //Used to test generation links
//        for(int i = 0; i < tempPath.size(); i++){
//            System.out.print("Node: " + tempPath.get(i).getValue());
//            if(tempPath.get(i).getGenerationRight() != null){
//                System.out.print(" Linked: " + tempPath.get(i).getGenerationRight().getValue());
//            }
//            System.out.println();
//        }

        //Test left and right
//        for(int i = 0; i < tempPath.size(); i++){
//            System.out.println("Node: " + tempPath.get(i).getValue() + " Left: " +
//                    tempPath.get(i).getLeft().getValue() +
//                    " Right: " + tempPath.get(i).getRight().getValue());
//        }


        //This is used to test if the last node isLastNode correctly
//        for(int i = 0; i < this.tempPath.size(); i++){
//            System.out.println(tempPath.get(i).getValue() + " " + tempPath.get(i).isLastNode());
//        }


        //This is used to test to make sure all the nodes are read in correctly
        //If there is more than 30 nodes total, add 30+32 to intArray
//        Integer[] intArray = new Integer[]{2, 6, 14, 30};
//        List<Integer> intList = new ArrayList<>(Arrays.asList(intArray));
//        System.out.println(this.root.getValue());
//            for(int i = 1; i < tempPath.size(); i++){
//                System.out.print(tempPath.get(i).getValue() + " ");
//                if(intList.contains(i)){
//                    System.out.println();
//                }
//            }

        createOutputFiles(outputFilename);

        printTreeLevels(ZERO);

        //Make sure data is correct (isLevelEnd, lastNode, genLinks, etc.)
        // Finds the depth of the tree
        findTreeDepth(this.root);
        minSort();

        printTreeLevels(ONE);
    }
}
