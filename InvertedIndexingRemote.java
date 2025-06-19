import com.hazelcast.cluster.Member;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.MultiExecutionCallback;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * The InvertedIndexingRemote class performs inverted indexing on a distributed cluster using Hazelcast. It submits a
 * callable object to all members of the cluster and collects the results asynchronously.
 *
 * @author SahithiC
 */
public class InvertedIndexingRemote {

    /**
     * Entry point of the program. Validates the arguments, prepares a local map, starts Hazelcast instance, and submits
     * a callable object to perform inverted indexing on all cluster members.
     *
     * @param args The command-line arguments. Expects a single argument: the keyword to search for.
     */
    public static void main(String[] args) {
        // Validate arguments
        if (args.length != 1) {
            System.out.println("Usage: java InvertedIndexingRemote keyword");
            return;
        }
        String keyword = args[0];

        // Prepare a local map
        Hashtable<String, Integer> local = new Hashtable<>();

        // Start Hazelcast and retrieve a cached map
        HazelcastInstance hz = Hazelcast.newHazelcastInstance();

        // Start a timer
        Date startTimer = new Date();

        // Create an executor service object from Hazelcast's getExecutorService()
        IExecutorService exec = hz.getExecutorService("exec");

        // Instantiate a callable object for performing inverted indexing
        Callable<String> invertedIndexingEach = new InvertedIndexingEach(keyword);

        // Define a callback to process the results from the cluster members
        MultiExecutionCallback callback = new MultiExecutionCallback() {
            @Override
            public void onResponse(Member member, Object theTime) {
                // Not used in this implementation
            }

            @Override
            public void onComplete(Map<Member, Object> msgs) {
                // Process the results received from the cluster members
                msgs.values().stream()
                        .map(Object::toString)
                        .map(str -> str.split(" "))
                        .filter(words -> words.length >= 2)
                        .forEach(words -> {
                            for (int i = 0; i < words.length; i += 2) {
                                local.put(words[i], Integer.parseInt(words[i + 1]));
                            }
                        });

                Date endTimer = new Date();

                // Print the results
                local.forEach((name, count) -> System.out.println("File[" + name + "] has " + count));

                System.out.println("\nElapsed time => " + (endTimer.getTime() - startTimer.getTime()));
            }
        };

        // Submit the callable object to all members of the cluster
        exec.submitToMembers(invertedIndexingEach, hz.getCluster().getMembers(), callback);
    }
}
