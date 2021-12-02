import java.io.*;

/**
 * This is the driver of the program. It makes sure the command line arguments are valid before
 * calling heap to perform the rest of the tasks.
 * @Author Dillon Gorlesky
 * @Author Johathyn Strong
 * @Version 1.0
 * @Date 12/01/2021
 */
public class Driver {

    /**
     * This is the main class which handles command line arguments and exceptions from Heap.
     * @param args command line arguments
     */
    public static void main(String[] args){
        if(args.length < 2){//Makes sure file and label is supplied
            System.out.println("Proper usage: java Driver file.txt <label>");
            System.exit(1);
        }

        String fileName = args[0];
        File file = new File(fileName);
        if(!file.exists()){//If file doesn't exist
            System.out.println("File does not exist. Please try again.");
            System.out.println("Usage: java Driver file.txt <label>");
            System.exit(1);
        }

        if(file.length() == 0){//If file is empty check
            System.out.println("File is empty, please correct.");
            System.exit(1);
        }

        String[] tokens = fileName.split("\\.(?=[^\\.]+$)");
        if(!tokens[1].equals("txt")){
            //Makes sure extension is a proper text file
            System.out.println("Improper file supplied, please try again.");
            System.out.println("Usage: java Driver file.txt <label>");
            System.exit(1);
        }

        try{
            //Call go method
            Heap heap = new Heap();
            heap.go(args[0], args[1]);
        } catch(Exception e){
            System.out.println("An Error has occurred please try again");
            System.out.println("Usage: java Driver file.txt <label>");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
