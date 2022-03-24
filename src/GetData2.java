// | 2nd | location | phone_numbers(VERSIONS => 100) | phone number | position code |
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

public class GetData2 {
    private static final int initialCapacity = 10000;
    private static ArrayList<String> phoneNums = new ArrayList<>(initialCapacity);
    private static ArrayList<Long> ts = new ArrayList<>(initialCapacity);
    private static ArrayList<Integer> placeCodes = new ArrayList<>(initialCapacity);
    private static ArrayList<Long> positionCodes = new ArrayList<>(initialCapacity);

    public static void main(String[] args) throws MasterNotRunningException, IOException {
        Connection connection = ConnectionFactory.createConnection();
        Table table = connection.getTable(TableName.valueOf("table2"));
        // TODO: modifications
        Get get = new Get(Bytes.toBytes(""));
        Result result = table.get(get);
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();

        System.out.println("\nget 'table2', ''");

        // System.out.println("Entries of map.entrySet():\n");
        int i = 0;
        for (Map.Entry<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> entry : map.entrySet()) {
            for (Map.Entry<byte[], NavigableMap<Long, byte[]>> entry2 : entry.getValue().entrySet()) {
                for (Map.Entry<Long, byte[]> entry3 : entry2.getValue().entrySet()) {
                    System.out.print(Bytes.toString(entry.getKey()) + ": ");
                    System.out.print(Long.toString(Bytes.toLong(entry2.getKey())) + '\t');

                    LocalDateTime dateTime = LocalDateTime.ofEpochSecond(entry3.getKey() / 1000, 0,
                            ZoneOffset.of("+08:00"));
                    System.out.println("timestamp = " + dateTime.toString() + ", value = "
                            + Integer.toString(Bytes.toInt(entry3.getValue())));

                    dateTime = null;
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
