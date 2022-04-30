
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

/** @author https://github.com/sShaAanGg */
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

    private static final String[] covidPatients = { "0999970292", "0924287276", "0924270243", "0924166304",
            "0924055403", "0923954960", "0923939462", "0923876364", "0923835919", "0923799856" };

    public static void main(String[] args) throws MasterNotRunningException, IOException {
        Connection connection = ConnectionFactory.createConnection();
        Table table1 = connection.getTable(TableName.valueOf("table1"));
        Table table2 = connection.getTable(TableName.valueOf("table2"));
        // Table table1 = connection.getTable(TableName.valueof("scale1"));
        // Table table2 = connection.getTable(TableName.valueof("scale2"));
        File output = new File("./output/output1.txt");
        File metadata = new File("./output/output1_metadata.txt");

        long timeCost = 0;
        for (int i = 0; i < 10; i++) {
            timeCost += fromTable1and2(table1, table2, output, metadata, covidPatients[i]);
        }
        System.out.println("\n" + Long.toString(timeCost / 10));
        // test(table1, table2);
        // Close table and connection, and stream
        // output = null;
        metadata = null;

        table1.close();
        table2.close();
        connection.close();
        table1 = null;
        table2 = null;
        connection = null;
    }

    // public static String getFirstPatients() {
    // return covidPatients;
    // }

    private static long fromTable1and2(Table table1, Table table2, File output, File metadata, String covidPatient)
            throws IOException {
        // if (!output.createNewFile()) {
        // output.delete();
        // }
        if (!metadata.createNewFile()) {
            metadata.delete();
        }
        // output.createNewFile();
        metadata.createNewFile();

        PrintStream metadataStream = new PrintStream(metadata);

        // phoneNums.add(covidPatient);

        ZoneOffset offset = ZoneOffset.of("+08:00");
        long time1 = LocalDateTime.now().toInstant(offset).toEpochMilli();
        GetData1.getData(table1, loc2Timestamp, covidPatient);

        // int i = 0;
        for (Map.Entry<Integer, Long> entry : loc2Timestamp.entrySet()) {
            // System.out.println(
            // "\nKey: " + Integer.toString(entry.getKey()) + ", Value: "
            // + LocalDateTime.ofEpochSecond(entry.getValue(), 0, offset).toString()
            // + " (Casted to LocalDateTime.toString())");
            GetData2.getData(table2, entry.getKey(), entry.getValue(), phoneNums, metadataStream);
            // i++;
        }
        // System.out.println("\nThere are " + Integer.toString(i) + " entries in
        // loc2Timestamp.entrySet()\n");

        long time2 = LocalDateTime.now().toInstant(offset).toEpochMilli();
        System.out.println(
                "It took " + Long.toString(time2 - time1)
                        + " milliseconds to complete the processing of data from HBase");

        // System.out.println("Output is directed to metadataStream");
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
        System.out.println("There are " + Integer.toString(j) + " people in the notification list");

        // stream.close();
        metadataStream.close();
        // stream = null;
        metadataStream = null;
        return (time2 - time1);
    }

    private static void test(Table table1, Table table2, String covidPatient) throws IOException {
        File output = new File("./output/log1.txt");
        File output2 = new File("./output/log2.txt");
        if (!output.createNewFile())
            output.delete();
        output.createNewFile();
        if (!output2.createNewFile())
            output2.delete();
        output2.createNewFile();
        PrintStream stream = new PrintStream(output);
        PrintStream stream2 = new PrintStream(output2);

        ZoneOffset offset = ZoneOffset.of("+08:00");
        GetData1.getData(table1, loc2Timestamp, covidPatient);

        int i = 0;
        for (Map.Entry<Integer, Long> entry : loc2Timestamp.entrySet()) {
            stream.println(
                    "\nKey: " + Integer.toString(entry.getKey()) + ", Value: "
                            + LocalDateTime.ofEpochSecond(entry.getValue(), 0, offset).toString()
                            + " (Casted to LocalDateTime.toString())");
            GetData2.getData(table2, entry.getKey(), entry.getValue(), phoneNums, stream2);
            i++;
        }
        stream.println("\nThere are " + Integer.toString(i) + " entries in loc2Timestamp.entrySet()\n");

        stream2.close();
        output2 = null;
        stream2 = null;
        stream.close();
        output = null;
        stream = null;
    }

}
