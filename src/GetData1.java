
// Schema of 'table1'
// | 1st | phone number | position(VERSIONS => 100) | position code | location |
import java.io.*;
// import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class GetData1 {
    // private static final int initialCapacity = 10000;
    // private static ArrayList<String> phoneNums = new
    // ArrayList<>(initialCapacity);
    // private static ArrayList<Long> ts = new ArrayList<>(initialCapacity);
    // private static ArrayList<Integer> placeCodes = new
    // ArrayList<>(initialCapacity);
    // private static ArrayList<Long> positionCodes = new
    // ArrayList<>(initialCapacity);

    // Maps the locations visited to the corresponding timestamps
    // private static HashMap<Integer, LocalDateTime> loc2Timestamp = new HashMap<>(
    // (int) Math.sqrt(initialCapacity));

    private static final String covidPatient = Processor.covidPatient;

    public static void getData(Table table, HashMap<Integer, Long> loc2Timestamp) throws IOException {
        Get get = new Get(Bytes.toBytes(covidPatient));
        get = get.addFamily(Bytes.toBytes("pos"));
        Result result = table.get(get);
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();

        System.out.println("\nget 'table1', '" + covidPatient + "'");

        // System.out.println("Entries of map.entrySet() from table1:\n");
        // int i = 0;
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : map.entrySet()) {
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> entry2 : entry.getValue().entrySet()) {
                for (Map.Entry<Long, byte[]> entry3 : entry2.getValue().entrySet()) {
                    // System.out.print(Bytes.toString(entry.getKey()) + ": ");
                    long timestampSecond = entry3.getKey() / 1000; // millisecond to second
                    // LocalDateTime dateTime = LocalDateTime.ofEpochSecond(entry3.getKey() / 1000,
                    // 0, ZoneOffset.of("+08:00"));
                    loc2Timestamp.put(Bytes.toInt(entry3.getValue()), timestampSecond);
                    // System.out.print(Long.toString(Bytes.toLong(entry2.getKey())) + '\t');
                    // System.out.println("timestamp = " + dateTime.toString() + ", value = "
                    // + Integer.toString(Bytes.toInt(entry3.getValue())));

                    // System.out.print("Key: " + Integer.toString(Bytes.toInt(entry3.getValue()))
                    // + ", Value: " + loc2Timestamp.get(Bytes.toInt(entry3.getValue())) + '\t');
                    // System.out.println("The " + (i + 1) + "-th Entry added");
                    // dateTime = null;
                    // i++;
                }
            }
        }
        // System.out.println("\nThere are " + Integer.toString(i) + " entries in
        // map.entrSet() from table1\n");
    }
}
