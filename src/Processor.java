import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.hbase.MasterNotRunningException;

public class Processor {
    private static final int initialCapacity = 10000;
    // private static ArrayList<String> phoneNums = new
    // ArrayList<>(initialCapacity);
    // private static ArrayList<Long> ts = new ArrayList<>(initialCapacity);
    // private static ArrayList<Integer> placeCodes = new
    // ArrayList<>(initialCapacity);
    // private static ArrayList<Long> positionCodes = new
    // ArrayList<>(initialCapacity);

    // Maps the locations visited to the corresponding timestamps
    private static HashMap<Integer, LocalDateTime> loc2Timestamp = new HashMap<>(
            (int) Math.sqrt(initialCapacity));
    private static ArrayList<HashMap<Integer, LocalDateTime>> listOfMaps = new ArrayList<>(
            (int) Math.sqrt(initialCapacity));

    public static void main(String[] args) throws MasterNotRunningException, IOException {
        GetData1 getData1 = new GetData1();
        getData1.getData(loc2Timestamp);
        int i = 0;
        for (Map.Entry<Integer, LocalDateTime> entry : loc2Timestamp.entrySet()) {
            System.out.println("Key: " + Integer.toString(entry.getKey()) + ", Value: " + entry.getValue().toString());
            i++;
        }
        System.out.println("\nThere are " + Integer.toString(i) + " entries in loc2Timestamp.entrSet()");
    }
}
