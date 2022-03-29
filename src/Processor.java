
// Now the Processor processes data from table1 and table2. PutData.java and GetData.java are not in used.
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.hbase.MasterNotRunningException;

public class Processor {
    private static final int initialCapacity = 10000;
    private static ArrayList<String> phoneNums = new ArrayList<>(initialCapacity);

    // Maps the locations visited to the corresponding timestamps
    private static HashMap<Integer, Long> loc2Timestamp = new HashMap<>(
            (int) Math.sqrt(initialCapacity));
    private static ArrayList<HashMap<Integer, Long>> listOfMaps = new ArrayList<>(
            (int) Math.sqrt(initialCapacity));

    public static final String covidPatient = "0901615803";

    public static void main(String[] args) throws MasterNotRunningException, IOException {
        GetData1.getData(loc2Timestamp);
        int i = 0;
        for (Map.Entry<Integer, Long> entry : loc2Timestamp.entrySet()) {
            System.out.println(
                    "Key: " + Integer.toString(entry.getKey()) + ", Value(Casted to LocalDateTime.toString()): "
                            + LocalDateTime.ofEpochSecond(entry.getValue(), 0, ZoneOffset.of("+8:00")).toString());
            GetData2.getData(entry.getKey(), entry.getValue(), phoneNums);
            i++;
        }
        System.out.println("\nThere are " + Integer.toString(i) + " entries in loc2Timestamp.entrySet()");
    }

}
