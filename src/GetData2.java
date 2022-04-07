
// Schema of 'table2'
// | 2nd | location | phone_numbers(VERSIONS => 100) | phone number | position code |
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class GetData2 {
    // TODO: modifications
    public static void getData(Table table, int placeCode, long ts, ArrayList<String> phoneNums) throws IOException {
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

        // System.out.println("Entries of map.entrySet() from table2:\n");
        // int i = 0;
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : map.entrySet()) {
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> entry2 : entry.getValue().entrySet()) {
                boolean toBeAdded = (phoneNums.contains(Bytes.toString(entry2.getKey())) ? false : true);
                if (toBeAdded) {

                } else {
                    continue;
                }
                for (Map.Entry<Long, byte[]> entry3 : entry2.getValue().entrySet()) {
                    long timestampSecond = entry3.getKey() / 1000; // convert millisecond to second

                    // ts is the timestamp of the covid patient; ts > timestamp means this person
                    // had been here before the covid patient had so we need not to notify the
                    // person.
                    if (ts > timestampSecond) {
                        continue;
                    } // else, ts < or = timestampSecond
                      // System.out.print(Bytes.toString(entry.getKey()) + ": ");

                    // TODO: time difference calculation
                    toBeAdded = (timestampSecond - ts <= rangeSecond); // ts < or = timestampSecond
                    if (toBeAdded) {
                        phoneNums.add(Bytes.toString(entry2.getKey()));
                        System.out.println(Bytes.toString(entry2.getKey()) + " was added to the list for notification");
                    } else {
                        continue;
                    }
                    // System.out.print(Bytes.toString(entry2.getKey()) + '\t');
                    // System.out.println("timestamp = "
                    // + LocalDateTime.ofEpochSecond(entry3.getKey() / 1000, 0,
                    // ZoneOffset.of("+08:00")).toString()
                    // + ", value = " + Long.toString(Bytes.toLong(entry3.getValue())));
                    // i++;
                }
            }
        }
        // System.out.println("\nThere are " + Integer.toString(i) + " entries in
        // map.entrSet() from table2\n");
    }
}
