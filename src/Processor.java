import java.io.IOException;
import java.util.ArrayList;

import org.apache.hadoop.hbase.MasterNotRunningException;

public class Processor {
    private static final int initialCapacity = 10000;
    private static ArrayList<String> phoneNums = new ArrayList<>(initialCapacity);
    private static ArrayList<Long> ts = new ArrayList<>(initialCapacity);
    private static ArrayList<Integer> placeCodes = new ArrayList<>(initialCapacity);
    private static ArrayList<Long> positionCodes = new ArrayList<>(initialCapacity);
    
    public static void main(String[] args) throws MasterNotRunningException, IOException {
        
    }
}
