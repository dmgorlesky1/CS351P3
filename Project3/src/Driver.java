import java.util.*;
import java.io.*;

/**
 *
 */
public class Driver {

    /**
     *
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

        //Create variables here

        try{
            //Call go method
            System.out.println("Calling Heap.go()");
            Heap heap = new Heap();
            heap.go(args[0], args[1]);

        } catch(Exception e){
//            System.out.println("An Error has occurred please try again");//CHANGE ME TO PROPER MSG
//            System.out.println(e.getMessage());
//            System.out.println(e);
            e.printStackTrace(); // Changed to this, so it is easier to see where the error is
            System.exit(1);
        }
    }
}
