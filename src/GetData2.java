
// | 2nd | location | phone_numbers(VERSIONS => 100) | phone number | position code |
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class GetData2 {
    // private static final int initialCapacity = 10000;
    // private static ArrayList<String> phoneNums = new
    // ArrayList<>(initialCapacity);
    // private static ArrayList<Long> ts = new ArrayList<>(initialCapacity);
    // private static ArrayList<Integer> placeCodes = new
    // ArrayList<>(initialCapacity);
    // private static ArrayList<Long> positionCodes = new
    // ArrayList<>(initialCapacity);

    // TODO: modifications
    public static void getData(int placeCode, long ts, ArrayList<String> phoneNums) throws IOException {
        Connection connection = ConnectionFactory.createConnection();
        Table table = connection.getTable(TableName.valueOf("table2"));
        Get get = new Get(Bytes.toBytes(placeCode));
        get = get.addFamily(Bytes.toBytes("pho"));
        Result result = table.get(get);
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();
        // TODO: modifications
        System.out.println("\nget 'table2', '" + Integer.toString(placeCode) + "'");

        // If the time difference of the message's time stamp and another patient's
        // message's time stamp is shorter than 24 hours, then he / she would be added
        // to the list for notification.
        final long rangeSecond = 24 * 60 * 60;

        System.out.println("Entries of map.entrySet() from table2:\n");
        int i = 0;
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : map.entrySet()) {
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> entry2 : entry.getValue().entrySet()) {
                for (Map.Entry<Long, byte[]> entry3 : entry2.getValue().entrySet()) {
                    System.out.print(Bytes.toString(entry.getKey()) + ": ");
                    long timestampSecond = entry3.getKey() / 1000; // millisecond to second

                    // TODO: time difference calculation
                    System.out.print(Bytes.toString(entry2.getKey()) + '\t');
                    System.out.println("timestamp = "
                            + LocalDateTime.ofEpochSecond(entry3.getKey() / 1000, 0, ZoneOffset.of("+8:00")).toString()
                            + ", value = " + Long.toString(Bytes.toLong(entry3.getValue())));

                    i++;
                }
            }
        }
        System.out.println("\nThere are " + Integer.toString(i) + " entries in map.entrSet()");

        // Close table and connection
        table.close();
        connection.close();
    }
}
