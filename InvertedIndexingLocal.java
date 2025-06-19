import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * The InvertedIndexingLocal class performs inverted indexing on a local Hazelcast map based on a given keyword. It
 * retrieves files from the map, counts the occurrences of the keyword in each file, and stores the results in a local
 * hashtable.
 *
 * @author SahithiC
 */

public class InvertedIndexingLocal {
    public InvertedIndexingLocal() {
    }

    /**
     * Main method to run the inverted indexing process.
     *
     * @param args Command line arguments. Expects a single argument: the keyword to search for.
     */
    public static void main(String[] args) {
        // validate arguments
        if (args.length != 1) {
            System.out.println("usage: java InvertedIndexingLocal keyword ");
            return;
        }
        String keyword = args[0];

        // prepare a local map
        Hashtable<String, Integer> local = new Hashtable<>();

        // start hazelcast and retrieve a cached map
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();

        // start a timer
        Date startTimer = new Date();

        IMap<String, String> map = hz.getMap("files");
        // examine each file
        Iterator<String> iter_hazel = map.keySet().iterator();

        while (iter_hazel.hasNext()) {
            String name = iter_hazel.next();
            String value = map.get(name);
            String[] words = value.split(" ");
            // prepare a word counter.
            int counter = 0;
            // for each words[i], did you find keyword? if so increment the word counter.
            for (int i = 0; i < words.length; i++) {
                if (words[i].equalsIgnoreCase(keyword)) {
                    counter++;
                }
            }
            // if the counter is positive put this file name with the counter value in local hashtable.
            if (counter > 0) {
                local.put(name, counter);
            }
        }

        Date endTimer = new Date(); // before showing the result, stop the timer.

        // show the result
        Iterator<String> iter_local = local.keySet().iterator();
        while (iter_local.hasNext()) {
            String name = iter_local.next();
            System.out.println("File[" + name + "] has " + local.get(name));
        }
        System.out.println(" ");
        System.out.println("Elapsed time = " + (endTimer.getTime() - startTimer.getTime()));
    }
}
