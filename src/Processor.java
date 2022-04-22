
// Now the Processor processes data from table1 and table2. PutData.java and GetData.java are NOT IN USED.
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
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
    /**
     * The phone numbers which are going to be notified because their paths were
     * overlapped with those of the covid patients
     */
    private static ArrayList<String> phoneNums = new ArrayList<>(initialCapacity);

    /** Maps the locations visited to the corresponding timestamps */
    private static HashMap<Integer, Long> loc2Timestamp = new HashMap<>(
            (int) Math.sqrt(initialCapacity));

    // private static ArrayList<HashMap<Integer, Long>> listOfMaps = new
    // ArrayList<>(
    // (int) Math.sqrt(initialCapacity));

    public static final String covidPatient = "0900060382";

    public static void main(String[] args) throws MasterNotRunningException, IOException {
        Connection connection = ConnectionFactory.createConnection();
        Table table1 = connection.getTable(TableName.valueOf("table1"));
        Table table2 = connection.getTable(TableName.valueOf("table2"));
        File output = new File("../output/output1.txt");
        File metadata = new File("../output/output1_metadata.txt");
        if (!output.createNewFile()) {
            output.delete();
        } else if (!metadata.createNewFile()) {
            metadata.delete();
        }
        output.createNewFile();
        metadata.createNewFile();
        PrintStream stream = new PrintStream(output);
        PrintStream metadataStream = new PrintStream(metadata);

        phoneNums.add(covidPatient);

        ZoneOffset offset = ZoneOffset.of("+08:00");
        long time1 = LocalDateTime.now().toInstant(offset).toEpochMilli();
        GetData1.getData(table1, loc2Timestamp);

        int i = 0;
        for (Map.Entry<Integer, Long> entry : loc2Timestamp.entrySet()) {
            System.out.println(
                    "\nKey: " + Integer.toString(entry.getKey()) + ", Value: "
                            + LocalDateTime.ofEpochSecond(entry.getValue(), 0, offset).toString()
                            + " (Casted to LocalDateTime.toString())");
            GetData2.getData(table2, entry.getKey(), entry.getValue(), phoneNums);
            i++;
        }
        System.out.println("\nThere are " + Integer.toString(i) + " entries in loc2Timestamp.entrySet()\n");

        long time2 = LocalDateTime.now().toInstant(offset).toEpochMilli();
        stream.println(
                "It took " + Long.toString(time2 - time1) + " milliseconds to complete the process of data from HBase");

        System.out.println("Output is directed to metadataStream");
        metadataStream.println(
                "The phone numbers below are going to be notified because their paths were overlapped with those of the covid patients");
        int j = 0;
        for (String phoNum : phoneNums) {
            if (j % 10 == 0) {
                metadataStream.print('\n');
            }
            metadataStream.print(phoNum + ' ');
            j++;
        }
        metadataStream.println();
        stream.println("There are " + Integer.toString(j) + " people in the notification list");

        // Close table and connection, and stream
        stream.close();
        metadataStream.close();
        stream = null;
        metadataStream = null;
        output = null;
        metadata = null;

        table1.close();
        table2.close();
        connection.close();
        table1 = null;
        table2 = null;
        connection = null;
    }

}
