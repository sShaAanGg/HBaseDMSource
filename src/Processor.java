
// Now the Processor processes data from table1 and table2. PutData.java and GetData.java are NOT IN USED.
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.MasterNotRunningException;

public class Processor {
    private static final int initialCapacity = 10000;

    // The phone numbers which are going to be notified because their paths were
    // overlapping with those of the covid patients
    private static ArrayList<String> phoneNums = new ArrayList<>(initialCapacity);

    // Maps the locations visited to the corresponding timestamps
    private static HashMap<Integer, Long> loc2Timestamp = new HashMap<>(
            (int) Math.sqrt(initialCapacity));
    // private static ArrayList<HashMap<Integer, Long>> listOfMaps = new
    // ArrayList<>(
    // (int) Math.sqrt(initialCapacity));

    public static final String covidPatient = "0901615803";

    public static void main(String[] args) throws MasterNotRunningException, IOException {
        Connection connection = ConnectionFactory.createConnection();
        Table table1 = connection.getTable(TableName.valueOf("table1"));
        Table table2 = connection.getTable(TableName.valueOf("table2"));

        phoneNums.add(covidPatient);
        GetData1.getData(table1, loc2Timestamp);
        int i = 0;
        for (Map.Entry<Integer, Long> entry : loc2Timestamp.entrySet()) {
            System.out.println(
                    "\nKey: " + Integer.toString(entry.getKey()) + ", Value: "
                            + LocalDateTime.ofEpochSecond(entry.getValue(), 0, ZoneOffset.of("+08:00")).toString()
                            + " (Casted to LocalDateTime.toString())");
            GetData2.getData(table2, entry.getKey(), entry.getValue(), phoneNums);

            i++;
        }
        System.out.println("\nThere are " + Integer.toString(i) + " entries in loc2Timestamp.entrySet()\n");

        System.out.println(
                "The phone numbers below are going to be notified because their paths were overlapping with those of the covid patients");
        int j = 0;
        for (String phoNum : phoneNums) {
            if (j % 10 == 0) {
                System.out.print('\n');
            }
            System.out.print(phoNum + ' ');
            j++;
        }
        System.out.println("\n\nThere are " + Integer.toString(j) + " people in the notification list");
        // Close table and connection
        table1.close();
        table2.close();
        connection.close();
    }

}
