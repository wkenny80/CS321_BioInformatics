****************
* Project Lab4
* Class CS 321
* 12/16/2021
**************** 

# Team Members

Last Name       | First Name      | GitHub User Name
--------------- | --------------- | --------------------
Kenny           | William         | wkenny80
Kircher         | KC              | kckirch
Halbert         | Justin          | jhalbotbsu


OVERVIEW:

 The GeneBankCreateBTree and GeneBankSearch functionalities of this project
 read in a gene file and create and store a GeneBank from the data in the form of
 a BTree. The search functionality then searches the tree and outputs the results.


INCLUDED FILES:

 * BTree.java
 * BTreeNode.java
 * Cache.java
 * DLLNode.java
 * GeneBankCreateBTree.java
 * GeneBankSearch.java
 * README-submission
 * TreeObject.java


COMPILING AND RUNNING:

 From the directory containing all source files, compile the
 driver class (and all dependencies) with the command:
 $ javac *.java

 Run the compiled classes file with the commands:
 $ java GeneBankCreateBTree <0/1(no/with Cache)> <degree> <gbk file> <sequence length>
[<cache size>] [<debug level>]
 $ java GeneBankSearch <0/1(no/with Cache)> <btree file> <query file> [<cache size>]
[<debug level>]

 Console output will give the results after the program finishes.


PROGRAM DESIGN AND IMPORTANT CONCEPTS:

 The Cache gives the opportunity for the user to improve performance of the creation of the
 BTree.
 
 ----------------------------------------------------------------------------