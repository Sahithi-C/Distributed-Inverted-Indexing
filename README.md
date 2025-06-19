#  Local vs Remote Execution of Hazelcast-based Inverted Indexing 

## Introduction:

This project implements two versions of inverted indexing program using Hazelcast’s distributed map that maintains a database of <key, value> items, each with key = a file name and value = its text data. One version is InvertedIndexingLocal.java that retrieves each file from the database, counts the occurrences of a given word, and prints out the file name and the number of occurrences. The other version is InvertedIndexingRemote.java that dispatches InvertedIndexingEach.class to each remote cluster node where it counts the occurrences of a given word in only files local to that remote node. The purpose of this project is to understand Hazelcast’s mechanism of remote execution and to measure its execution performance. 

<img width="488" alt="image" src="https://github.com/user-attachments/assets/b6bbf46b-999b-47e6-802d-4754e5ddf93b" />

**DBCreator.java** - Receives a directory of text files; joins a Hazelcast environment; creates a IMap<String, String> named “files” where key = a file name and value = entire file contents; reads each text file from the directory; add this file name and its contents to the IMap as a new key-value tuple.

**RemoteKeyViewer.java** - Joins a Hazelcasst environment; retrieves a set of cluster members; and dispatches a RemoteKeySearchCallable program to each member. Upon a response and completion of each cluster member’s execution of RemoteKeySearchCallable, it prints out the execution output in onResponse( ) and onComplete( ) respectively. The output is the number of files and their names local to each cluster member.

**RemoteKeySearchCallable.java** - Invokes the call( ) method at each cluster member; accesses the shared IMap named “files”; retrieves the keys, (i.e., file names) of only tuples <file name, contents> local to that cluster member; and returns the number of local files and their names.

**InvertedIndexingLocal.java** - Receives an inverted-indexing keyword in args[0]; joins a Hazelcast environment; starts a timer for performance measurement; retrieves the shared IMap named “files”; retrieves an iterator of all key-value tuples; counts the occurrence of the keyword of each file; stops the timer; and prints out “File[name] has #occurences”.

**InvertedIndexingRemote.java** - Receives an inverted-indexing keyword in args[0]; joins a Hazelcasst environment; starts a timer for performance measurement; retrieves a set of cluster members; and dispatches an InvertedIndexingEach program to each member. Upon completion of all cluster members’ execution of InvertedIndexingEach, it moves each cluster member’s <filename, occurrences> tuples to the local hash; stops the timer; and prints out this local hash contents as “File[name] has #occurences”.

**InvertedIndexingEach.java** - Invokes the call( ) method at each cluster member; accesses the shared IMap named “files”; retrieves the keys, (i.e., file names) of only tuples <file name, contents> local to that cluster member; counts the occurrence of the keyword of each file; and returns a string that includes “filename1 count1 filename2 count2...".

## Documentation:

**InvertedIndexingLocal.java** - This class performs inverted indexing on a local Hazelcast map. It takes a keyword as input and retrieves files from the map. It starts a Hazelcast instance and gets access to the map. For each file in the map. If the keyword is found in a file, the file name and the number of times the keyword appears are saved in a local hashtable. The program also tracks the time it takes to perform this entire operation. Once all files are checked, it prints out each file name along with the count of keyword appearances, followed by the total time it took. 

**InvertedIndexingRemote.java** - The class enables distributed inverted indexing on a cluster using Hazelcast. It accepts a keyword as a command-line argument and performs inverted indexing on the cluster members. It starts a Hazelcast instance, submits a callable object to each member, and collects the results asynchronously. The class utilizes a callback to process the results received from the cluster members. It populates a local map with the inverted index data and prints the file names along with their corresponding counts. The total time taken is also displayed. 

**InvertedIndexingEach.java** - The class performs inverted indexing on a specified keyword. It implements the Callable interface to support concurrent execution and the HazelcastInstanceAware interface to receive the Hazelcast instance. The class represents a task that operates on a distributed Hazelcast map containing files. It retrieves the local entries from the map, splits the values into words, and counts the occurrences of the keyword. It stores the results in a local hashtable. The class provides a method to execute the indexing task and returns a string representation of the inverted index result.

