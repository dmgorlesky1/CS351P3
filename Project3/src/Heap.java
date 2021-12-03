import java.nio.file.Path;
import java.util.*;
import java.io.*;

/**
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * @Author Dillon Gorlesky
 * @Author Johathyn Strong
 * @Version 1.0
 * @Date 12/01/2021
 */
public class Heap {
    /** Temporary storage for the paths starting at tempPath[1]. */
    private ArrayList<PathNode> tempPath;

    /** Output files for before and after the heapify of a tree */
    private File before, after;

    /** The name of the output files to be written to*/
    private String label;

    /** Root of the tree */
    private PathNode root;

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
         } catch(FileNotFoundException fnfe){
             System.out.println("File not found. Please check input and try again.");
             System.out.println("Usage: java Driver file.txt <label>");
             scan.close();
             System.exit(1);
         }
         boolean check;
         String lining;
         String[] line;
         PathNode node;
         while(scan.hasNextLine()){//Skips empty lines and tabs
             check = false;
             lining = "";
             line = scan.nextLine().split("\\s+");//splits line to get length
             for(int i = 0; i < line.length; i++) {
                 if(!line[i].equals("")) {
                     lining += line[i] + " ";
                     check = true;
                 } else {
                     check = false;
                 }
             }
             if(check) {
                 //New node added
                 node = new PathNode(lining.split(" ").length - 1);
                 node.setPath(settingPath(lining.split(" ")));
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
     *
     * @param pathFound
     * @return
     */
    public ArrayList<Integer> settingPath(String[] pathFound){
        ArrayList<Integer> newList = new ArrayList<Integer>();
        for(int i = 0; i < pathFound.length; i++){
            newList.add(Integer.parseInt(pathFound[i]));
        }
        return newList;
    }


    /**
     * Recursively builds a complete binary tree. Places PathNode objects in tempPath ArrayList
     * into a complete binary tree in order of appearance in the text file.
     *
     * @param index Index of the current node in tempPath.
     * @param parent Parent of the current node.
     * @return A reference to the node just placed in the tree.
     */
    public PathNode buildCompleteTree(int index, int parent) {
        PathNode node = new PathNode();
        PathNode node2 = new PathNode();
        if(this.tempPath.size() > 2 * index + 1){
            node = buildCompleteTree(2 * index + 1, index);
        }
        if(this.tempPath.size() > 2 * index + 2){
            node2 = buildCompleteTree(2 * index + 2, index);
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
     *
     * @return
     */
    public String doArrows(){
        String msg = "";
        int len = tempPath.size()-1;
        int i1 = 1;
        int i2 = 0;
        for(int i = 0; i < len; i++){
            if(i1 < len) {
                msg += "\t" + i2 + " -> " + i1 + ";\n";
                i1++;
                msg += "\t" + i2 + " -> " + i1 + ";\n";
                i2++;
                i1++;
            }
        }
        if(len % 2 != 0){
            msg += "\t" + i2 + " -> " + i1 + ";\n";
        }
        return msg;
    }

    /**
     *
     * @return
     */
    public String doWhile(){
        String msg = "";
        PathNode node = this.root;
        PathNode node2 = null;
        msg += getFormat(node, 0);
        int i = 1;
        while(node.getLeft().getValue() != 9999){
            while(node2 != null){
                if(node2.getGenerationRight() != null){
                    msg += getFormat(node2, i);
                    node2 = node2.getGenerationRight();
                } else {
                    msg += getFormat(node2, i);
                    node2 = null;
                }
                i++;
            }
            node = node.getLeft();
            node2 = node.getGenerationRight();
            msg += getFormat(node, i);
            i++;
        }
        node2 = node.getGenerationRight();
        while(node2 != null){
            if(node2.getGenerationRight() != null) {
                if (node2.getGenerationRight().getValue() != 9999) {
                    msg += getFormat(node2, i);
                    i++;
                    node2 = node2.getGenerationRight();
                } else {
                    msg += getFormat(node2, i);
                    i++;
                    node2 = null;
                }
            } else {
                msg += getFormat(node2, i);
                i++;
                node2 = null;
            }
        }
        return msg;
    }

    /**
     *
     * @param node
     * @param i
     * @return
     */
    public String getFormat(PathNode node, int i){
        String msg = "";
        if(node.getValue() != 9999){
            // Outputs first index value
            msg += "\t" + i + "[label=\"";
            // Outputs the number of edges between nodes
            msg += node.getPath().size() - 1 + "(";
            // Outputs the first path value
            msg += node.getPath().get(ZERO);
            // Outputs every path value after
            for (int j = 1; j < node.getPath().size(); j++) {
                msg += ", " + node.getPath().get(j);
            }
            // Outputs the ending styling
            msg += ")\"];\n";
        }
        return msg;
    }

    //steps to get starting node in min sort
    // 1) traverse left child till you reach the end
    // 2) save that node to CurrentLevel
    // 3) move to the right through the generation till you reach the last added node
    // 4) check the value of the last added node to the value of the nodes parent
    //  if the value is smaller swap with the parent and continue
    //  else do nothing
//    public void minSort() {
//        PathNode currentLevel = this.root;
//        ArrayList<PathNode> levelNodes = new ArrayList<>();
//        while (currentLevel.getLeft().getValue() != 9999) { // Getting the bottom level of the graph
//            currentLevel = currentLevel.getLeft();
//        } // Saving the beginning of the level
//        PathNode current = currentLevel;
//        while (current.hasGenerationRight()) { // Getting the last added node
//            levelNodes.add(current);
//            current = current.getGenerationRight();
//        }
//        for (int i = (levelNodes.size()-1); i >= 0; i--) { // May cause infinite loop
//            if (levelNodes.get(i).getValue() < levelNodes.get(i).getParent().getValue()) {
//                // Saving all needed values for use when swapping
//                PathNode B = levelNodes.get(i);
//                PathNode BgenLeft = i-1 == -1 ? null : levelNodes.get(i-1);
//                PathNode A = levelNodes.get(i).getParent();
//                PathNode currentParent = B.getParent();
//                PathNode currentLeft = B.getLeft();
//                PathNode currentRight = B.getRight();
//                PathNode currentGenRight = B.getGenerationRight();
//
//                PathNode parentParent = A.getParent();
//                PathNode parentLeft = A.getLeft();
//                PathNode parentRight = A.getRight();
//                PathNode parentGenRight = A.getGenerationRight();
//
//                // Swapping current node
//
//                B.setParent(A.getParent());
//                PathNode Bleft = B.getLeft(), Bright = B.getRight(), BgenRight = B.getGenerationRight();
//                if (A.getLeft() == B) {
//                    B.setLeft(A);
//                    B.setRight(A.getRight());
//                } else {
//                    B.setLeft(A.getLeft());
//                    B.setRight(A);
//                }
//                B.setGenerationRight(A.getGenerationRight());
//
//                A.setParent(B);
//                A.setLeft(Bleft);
//                A.setRight(Bright);
//                A.setGenerationRight(BgenRight);
//
//                if (BgenLeft != null) {
//                    BgenLeft.setGenerationRight(A);
//                }
//
//                this.setGenerationLinks(this.root);
//            }
//            // else do nothing
//        }
//    }

    public <T extends Comparable<T>> void heapifyTree(PathNode root) {
        if (root == null) {
            return;
        }

        heapifyTree(root.getLeft());
        heapifyTree(root.getRight());
        bubbleDown(root);
    }

    private void bubbleDown(PathNode node) {
        PathNode smallestNode = node;

        if (node.getLeft() != null && node.getLeft().getValue() < node.getValue()) {
            smallestNode = node.getLeft();
        }

        if (node.getRight() != null && node.getRight().getValue() < node.getValue()) {
            smallestNode = node.getRight();
        }

        if (smallestNode != node) {
            swapNodeValues(node, smallestNode);
            bubbleDown(smallestNode);
        }
    }

    private void swapNodeValues(PathNode a, PathNode b) {
        b.setParent(a.getParent());
        a.setParent(b);

        if (a.getLeft() == b) {
            PathNode tempRight = a.getRight();
            a.setLeft(b.getLeft());
            b.setLeft(a);
            a.setRight(b.getRight());
            b.setRight(tempRight);
        } else { // Else b is on the right
            PathNode templeft = a.getLeft();
            a.setRight(b.getLeft());
            b.setRight(a);
            a.setLeft(b.getLeft());
            b.setLeft(templeft);
        }

        PathNode tempGenRight = a.getGenerationRight();
        a.setGenerationRight(b.getGenerationRight());
        b.setGenerationRight(tempGenRight);

//        int temp = a.getValue();
//        a.setValue(b.getValue());
//        b.setValue(temp);
    }


    /**
     * Runs all the functions to read in the input file, build a tree, initialize all fields such
     * as isLevelEnd, Generation links, etc, then performs a min heap and writes a before
     * and after to a dot file to make a visualization from.
     * @param inputFilename Name of file with data points in it
     * @param outputFilename Name to create output files off of
     */
    public void go(String inputFilename, String outputFilename) {
        readPaths(inputFilename);
        buildCompleteTree(ZERO,ZERO);
        this.root = this.tempPath.get(ZERO);
        this.label = outputFilename;

        setLevelEnd(root);
        //Set last node
        this.tempPath.get(this.tempPath.size()-1).setLastNode(true);

        //Set generation links
        setGenerationLinks(root);

        //Used to test generation links
        /**for(int i = 0; i < tempPath.size(); i++){
            System.out.print("Node: " + tempPath.get(i).getValue());
            if(tempPath.get(i).getGenerationRight() != null){
                System.out.print(" Linked: " + tempPath.get(i).getGenerationRight().getValue());
            }
            System.out.println();
        }*/

        //Test left and right
        /**for(int i = 0; i < tempPath.size(); i++){
            System.out.println("Node: " + tempPath.get(i).getValue() + " Left: " +
                    tempPath.get(i).getLeft().getValue() +
                    " Right: " + tempPath.get(i).getRight().getValue());
        }*/


        //This is used to test if the last node isLastNode correctly
        /**for(int i = 0; i < this.tempPath.size(); i++){
            System.out.println(tempPath.get(i).getValue() + " " + tempPath.get(i).isLastNode());
        }*/


        //This is used to test to make sure all the nodes are read in correctly
        //If there is more than 30 nodes total, add 30+32 to intArray
/**     Integer[] intArray = new Integer[]{2, 6, 14, 30};
        List<Integer> intList = new ArrayList<>(Arrays.asList(intArray));
        System.out.println(this.root.getValue());
            for(int i = 1; i < tempPath.size(); i++){
                System.out.print(tempPath.get(i).getValue() + " ");
                if(intList.contains(i)){
                    System.out.println();
                }
            }*/

        createOutputFiles(outputFilename);

        printTreeLevels(ZERO);

        //Do min heap


        //Make sure data is correct (isLevelEnd, lastNode, genLinks, etc)
//        minSort();
        heapifyTree(this.root);

        printTreeLevels(ONE);
    }
}
