import java.util.*;
import java.io.*;

/**
 *
 */
public class Heap {

    /** Temporary storage for the paths starting at tempPath[1]. */
    private ArrayList<PathNode> tempPath;

    /** Output files for before and after the heapify of a tree */
    private File before, after;

    private String label;

    /** Root of the tree */
    private PathNode root;

    /** This reads in the file */
    private Scanner scan;

    /**
     * Constructor for a new Heap
     */
    public Heap(){
        this.tempPath = new ArrayList<>();
        this.root = null;
        this.label = "";
    }
/*
    private PathNode addRecursive(PathNode current, PathNode newNode) {
        if (current == null) {// Used if the graph is empty
            return newNode;
        } else if (newNode.getValue() == current.getValue()) {
            int side = this.random.nextInt(2);
            if (side == 0) {
                current.setLeft(addRecursive(current.getLeft(), newNode));
            } else {
                current.setRight(addRecursive(current.getRight(), newNode));
            }
        // If the new value is less than the current id
        } else if (newNode.getValue() < current.getValue()) {
            current.setLeft(addRecursive(current.getLeft(), newNode));
        // If the new value is larger than the current id
        } else if (newNode.getValue() > current.getValue()) {
            current.setRight(addRecursive(current.getRight(), newNode));
        } else {// The id already exists
            return current;
        }
        return current;
    }

    public void insert(PathNode newNode) {
        this.root = this.addRecursive(this.root, newNode);
    }*/

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
     * @param root Root of the whole tree to begin printing from.
     */
    public void printTreeLevels(PathNode root){

    }


    /**
     * Creates before and after files based off of the given filename
     * @param filename First part of the output filename
     */
    public void createOutputFiles(String filename) {
        try {
            //CHANGE THESE TO .DOT FILES BEFORE SUBMITTING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            this.before = new File(filename + "Before.txt");
            this.after = new File(filename + "After.txt");
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
     * Outputs graph to the before and after file depending on parameter.
     * If time == 0, write to before
     * Else time == 1, write to after
     * @param time Which file needs to be written to.
     */
    public void outputBefore(int time) {
        try {
            String pathing = "";
            FileWriter fileBefore;
            if(time == 0){
                fileBefore = new FileWriter(this.before.getName());
                fileBefore.write("digraph " + label + "Before{\n");
            } else {
                fileBefore = new FileWriter(this.after.getName());
                fileBefore.write("digraph " + label + "After{\n");
            }
            // First half styling example  ->  (index)[label="(num edges)(path)"];
            // Outputs the first half of the file
            for (int i = 0; i < this.tempPath.size(); i++) {
                // Outputs first index value
                fileBefore.write("\t" + i + "[label=\"");
                // Outputs the number of edges between nodes
                pathing = tempPath.get(i).getPath().size() - 1 + "";
                fileBefore.write(pathing + " (");
                // Outputs the first path value
                fileBefore.write(String.valueOf(this.tempPath.get(i).getPath().get(0)));
                // Outputs every path value after
                for (int j = 1; j < this.tempPath.get(i).getPath().size(); j++) {
                    fileBefore.write( ", " + this.tempPath.get(i).getPath().get(j));
                }
                // Outputs the ending styling
                fileBefore.write(") \"];\n");
            }
            // Outputs the second half of the file
            for (int index = 0; index < this.tempPath.size(); index++) {
                // Outputs the connections for 0
                if (index == 0) {
                    fileBefore.write("\t" + index + " -> " + (index+1) + ";\n");
                    fileBefore.write("\t" + index + " -> " + (index+2) + ";\n");
                } else { // Outputs the connections for every other value
                    // Check if there is a left child
                    if (!((2*index) > this.tempPath.size()-1)) {
                        fileBefore.write("\t" + index + " -> " + (2*index) + ";\n");
                    }
                    // Check if there is a right child
                    if (!((2*index+1) > this.tempPath.size()-1)) {
                        fileBefore.write("\t" + index + " -> " + (2*index+1) + ";\n");
                    }
                }
            }
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
     * Runs the heap program
     * @param inputFilename Name of file with data points in it
     * @param outputFilename Name to create output files off of
     */
    public void go(String inputFilename, String outputFilename) {
        readPaths(inputFilename);
        buildCompleteTree(0,0);
        this.root = this.tempPath.get(0);
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

        outputBefore(0);

        //Do min heap


        //Make sure data is correct (isLevelEnd, lastNode, genLinks, etc)


        outputBefore(1);
    }
}
