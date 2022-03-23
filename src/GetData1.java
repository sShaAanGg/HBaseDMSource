import java.io.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.function.BiConsumer;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.MasterNotRunningException;
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
    // private static int sizeOfList;

    public static void main(String[] args) throws MasterNotRunningException, IOException {
        Connection connection = ConnectionFactory.createConnection();
        Table table = connection.getTable(TableName.valueOf("table1"));

        Get get = new Get(Bytes.toBytes("0901615803"));
        Result result = table.get(get);
        NavigableMap<byte[], byte[]> familyMap = result.getFamilyMap(Bytes.toBytes("pos"));
        NavigableMap<byte[], NavigableMap<byte[], NavigableMap<Long, byte[]>>> map = result.getMap();

        System.out.println("\nget 'table1', '0901615803'");
        System.out.println("Values of familyMap.values(): ");
        for (Map.Entry<byte[], byte[]> entry : familyMap.entrySet()) {
            System.out.println("pos: " + Long.toString(Bytes.toLong(entry.getKey())) + "\tvalue = "
                    + Integer.toString(Bytes.toInt(entry.getValue())));
        }
        // System.out.println("Values of familyMap.values(): ");
        // System.out.println(familyMap.values().toString());
        // System.out.println("values of map.values()");
        // System.out.println(map.values());

        // Close table and connection
        table.close();
        connection.close();
    }
}
