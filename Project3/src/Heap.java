import java.util.*;
import java.io.*;

/**
 *
 */
public class Heap {
    /** Temporary storage for the paths starting at tempPath[1]. */
    private final ArrayList<PathNode> tempPath;
    /** Output files for before and after the heapify of a tree */
    private File before, after;
    /** Root of the tree */
    private PathNode root;
    /** Picking a side to send a value down when it has the same value as the root */
    private final Random random;

    /**
     * Constructor for a new Heap
     */
    public Heap(){
        this.tempPath = new ArrayList<>();
        this.root = null;
        this.random = new Random(); // POSSIBLY REMOVE LATER
    }

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
        } else if (newNode.getValue() < current.getValue()) {// If the new value is less than the
            // current id
            current.setLeft(addRecursive(current.getLeft(), newNode));
        } else if (newNode.getValue() > current.getValue()) {// If the new value is larger than the
            // current id
            current.setRight(addRecursive(current.getRight(), newNode));
        } else {// The id already exists
            return current;
        }
        return current;
    }

    public void insert(PathNode newNode) {
        this.root = this.addRecursive(this.root, newNode);
    }

    /**
     * Reads inputFile given at the command line and places the contents of each line into the
     * path field found in each PathNode object. The order is the same as found in the text file.
     * Adds the PathNode object to tempPath starting at tempPath[1].
     *
     * @param inputFile Name of the input file to be read.
     * @throws FileNotFoundException if the input file cannot be found.
     */
     public void readPaths(String inputFile) throws FileNotFoundException {
         File file = new File(inputFile);
         BufferedReader bufferedReader;
         int numEdges;

         try {
             bufferedReader = new BufferedReader(new FileReader(file));
             String s;

             while ((s = bufferedReader.readLine()) != null) {
                 String[] stringPath = s.split(" ");
                 numEdges = stringPath.length-1;
                 ArrayList<Integer> path = new ArrayList<>();

                 for (String value : stringPath) {
                     path.add(Integer.valueOf(value));
                 }

                 PathNode node = new PathNode(numEdges);
                 node.setPath(path);
                 this.insert(node);
                 this.tempPath.add(node);
             }
         } catch (IOException e) {
             e.printStackTrace();
         }
     }

    /**
     * Recursively builds a complete binary tree. Places PathNode objects in tempPath ArrayList into a
     * complete binary tree in order of appearance in the text file.
     *
     * @param index Index of the current node in tempPath.
     * @param parent Parent of the current node.
     * @return A reference to the node just placed in the tree.
     */
//    public PathNode buildCompleteTree(int index, int parent) {
//
//    }

    /**
     * Recursive method that sets isLevelEnd.
     * @param root Root of the subtree.
     */
    public void setLevelEnd(PathNode root){

    }

    /**
     * Recursive method that sets the "generation" link of PathNode objects from right-to-left.
     * generation is a term I use to indicate nodes on the same level (these may be siblings or
     * cousins)
     * @param root Root of the subtree.
     */
    public void setGenerationLinks(PathNode root){

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
            this.before = new File(filename + "Before.dot");
            this.after = new File(filename + "After.dot");
            if (!this.before.createNewFile()) {
                System.out.println(this.before.getName() + " already exists overwriting");
            }
            if (!this.after.createNewFile()) {
                System.out.println(this.after.getName() + " already exists overwriting");
            }
        } catch (IOException e) {
            System.out.println("An error occurred in createOutputFiles");
            e.printStackTrace();
        }
    }

    /**
     * Outputs graph to the before file
     */
    public void outputBefore() {
        try {
            FileWriter fileBefore = new FileWriter(this.before.getName());
            // First half styling example  ->  (index)[label="(num edges)(path)"];
            // Outputs the first half of the file
            for (int i = 0; i < this.tempPath.size(); i++) {
                // Outputs first index value
                fileBefore.write("\t" + i + "[label=\"");
                // Outputs the number of edges between nodes
                fileBefore.write((this.tempPath.get(i).getPath().size()-1) + " (");
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
                    fileBefore.write("\t" + index + " -> " + (index+1) + "\n");
                    fileBefore.write("\t" + index + " -> " + (index+2) + "\n");
                } else { // Outputs the connections for every other value
                    // Check if there is a left child
                    if (!((2*index) > this.tempPath.size()-1)) {
                        fileBefore.write("\t" + index + " -> " + (2*index) + "\n");
                    }
                    // Check if there is a right child
                    if (!((2*index+1) > this.tempPath.size()-1)) {
                        fileBefore.write("\t" + index + " -> " + (2*index+1) + "\n");
                    }
                }
            }
            fileBefore.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Outputs graph to the after file
     */
    public void outputAfter() {
        try {
            FileWriter fileAfter = new FileWriter(this.after.getName());
            fileAfter.write("TESTING");
            fileAfter.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the heap program
     * @param inputFilename Name of file with data points in it
     * @param outputFilename Name to create output files off of
     */
    public void go(String inputFilename, String outputFilename) {
        try {
            System.out.println("Creating output files.....");
            this.createOutputFiles(outputFilename);

            this.readPaths(inputFilename);

            this.outputBefore();

            this.outputAfter();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
