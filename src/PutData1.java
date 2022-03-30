
// Schema of 'table1'
// | 1st | phone number | position(VERSIONS => 100) | position code | location |
import java.io.*;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
// import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
// import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class PutData1 {
    private static final int initialCapacity = 10000;
    private static ArrayList<String> phoneNums = new ArrayList<>(initialCapacity);
    private static ArrayList<Long> ts = new ArrayList<>(initialCapacity);
    private static ArrayList<Integer> placeCodes = new ArrayList<>(initialCapacity);
    private static ArrayList<Long> positionCodes = new ArrayList<>(initialCapacity);
    private static int sizeOfList;

    public static void main(String[] args) throws MasterNotRunningException, IOException {
        // Instantiating a Connection class object and table object
        Connection connection = ConnectionFactory.createConnection();
        Table table = connection.getTable(TableName.valueOf("table1"));
        sizeOfList = 0;
        final int numOfInFiles = 4;
        for (int i = 0; i < numOfInFiles; i++) {
            readFromXlsx("/home/shang/repo/myHBaseProject/HbaseDataGenerate/test/data/data_from_1-"
                    + Integer.toString(1 + (i * 7)) + "_sorted.xlsx");
        }

        ArrayList<Put> puts = new ArrayList<>(sizeOfList);
        putData(puts);

        table.put(puts);
        System.out.println("Data was inserted Successfully");

        // Close table and connection
        table.close();
        connection.close();
    }

    private static void readFromXlsx(String filePath) throws IOException {
        File file = new File(filePath);
        XSSFWorkbook xssf;
        Sheet sheet;
        if (file.exists()) {
            FileInputStream fileIn = new FileInputStream(filePath);
            xssf = new XSSFWorkbook(fileIn);
            sheet = xssf.getSheetAt(0);
        } else {
            IOException exception = new IOException("File Not Found!");
            exception.printStackTrace();
            throw exception;
        }

        final int second2Milli = 1000;
        for (Row row : sheet) {
            sizeOfList++;
            phoneNums.add(row.getCell(0).getStringCellValue()); // phone numbers
            LocalDateTime dateTime = LocalDateTime.parse(row.getCell(1).getStringCellValue()); // date time
            ts.add(dateTime.toEpochSecond(ZoneOffset.of("+08:00")) * second2Milli);
            placeCodes.add((int) row.getCell(2).getNumericCellValue());
            positionCodes.add((long) row.getCell(3).getNumericCellValue());

            dateTime = null;
            row = null;
        }
        sheet = null;
        xssf.close();
    }

    private static void putData(ArrayList<Put> puts) {
        for (int i = 0; i < sizeOfList; i++) {
            Put put = new Put(Bytes.toBytes(phoneNums.get(i)));
            put.addColumn(Bytes.toBytes("pos"), Bytes.toBytes(positionCodes.get(i)), ts.get(i),
                    Bytes.toBytes(placeCodes.get(i)));
            puts.add(put);
            put = null;
        }
    }

}
