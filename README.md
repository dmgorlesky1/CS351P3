# CS351 Project 3
#### Authors: Dillon Gorlesky and Johathyn Strong
#### Version: 1.0
#### Last updated: 12/9/2021


## To compile

---
On the command line enter:

    javac *.java

## To run

---
On the command line enter:

    java Driver <Filename of graph> <label of output files>

The files outputted will be:
* (label)Before.dot
* (label)After.dot

These files can be inputted into the program graphViz which will turn them into a visual picture 
form. 

Todo this, install graphViz and run the commands:

    dot -Tpng -o(label)Before.png (label)Before.dot
    dot -Tpng -o(label)After.png (label)After.dot

## Known bugs

---
There are no known bugs at this time, however when minSorting a graph the root node may not flow 
all the way down to it's correct position.