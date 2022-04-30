
// Schema of 'table2'
// | 2nd | location | phone_numbers(VERSIONS => 100) | phone number | position code |
import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.io.PrintStream;

import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class GetData2 {
    public static void getData(Table table, int placeCode, long ts, ArrayList<String> phoneNums, PrintStream stream)
            throws IOException {

        Get get = new Get(Bytes.toBytes(Integer.toString(placeCode)));
        get = get.addFamily(Bytes.toBytes("pho"));
        Result result = table.get(get);
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();

        // stream.println("\nget 'table2', '" + Integer.toString(placeCode) + "'");

        /**
         * If the message's time stamp and another patient's
         * message's time stamp is in the same hour, then he / she would be added
         * to the list for notification.
         */
        // final long rangeSecond = 24 * 60 * 60;

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
                    LocalDateTime dateTime = LocalDateTime.ofEpochSecond(timestampSecond, 0, ZoneOffset.of("+08:00"));
                    // stream.println(dateTime.toString());
                    LocalDateTime patientTS = LocalDateTime.ofEpochSecond(ts, 0, ZoneOffset.of("+08:00"));

                    if (dateTime.getHour() != patientTS.getHour() || dateTime.getDayOfYear() != patientTS.getDayOfYear()
                            || dateTime.getDayOfYear() != patientTS.getDayOfYear()) {
                        continue;
                    } // dateTime.getHour() == patientTS.getHour()
                    // stream.print(dateTime.getHour() + " ");
                    // stream.print(patientTS.getHour() + " ");
                    phoneNums.add(Bytes.toString(entry2.getKey()));
                    // stream.println(Bytes.toString(entry2.getKey()) + " was added to the list for notification");

                    // stream.print(Bytes.toString(entry.getKey()) + ": ");

                    // Time difference calculation
                    // toBeAdded = (timestampSecond - ts <= rangeSecond); // ts < or =
                    // timestampSecond
                    // if (toBeAdded) {
                    // phoneNums.add(Bytes.toString(entry2.getKey()));
                    // System.out.println(Bytes.toString(entry2.getKey()) + " was added to the list
                    // for notification");
                    // } else {
                    // continue;
                    // }
                    dateTime = null;
                    patientTS = null;
                }
            }
            // i++;
        }
        // stream.println("\nThere are " + Integer.toString(i) + " entries in
        // map.entrSet() from table2\n");

    }
}
