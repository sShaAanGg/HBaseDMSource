package com.tools;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

/*
此為hbase增刪改查使用工具
 */

import javax.inject.Qualifier;
import java.io.IOException;

public class HbaseTools {

    private Admin admin = null;
    private Connection connection = null;
    private Configuration configuration = null;

    public void setDataConfig(Configuration conf) {

        //HBase配置文件
        /*
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", "192.168.10.102");
        configuration.set("zookeeper.znode.parent", "/hbase/master/master");
        */
        configuration = conf;

        //獲取連結對象
        try {
            connection = ConnectionFactory.createConnection(configuration);
            admin = connection.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 增、改
    public void putData(String tableName, String rowKey, String cF, String cN, String value) throws IOException {

        // 獲取表對象
        Table table = connection.getTable(TableName.valueOf(tableName));

        // 創建put對象
        Put put = new Put(Bytes.toBytes(rowKey));

        // 添加數據
        put.addColumn(Bytes.toBytes(cF), Bytes.toBytes(cN), Bytes.toBytes(value));

        // 執行添加操作
        table.put(put);

        // System.out.println("已完成添加");

        table.close();

        return;
    }

    // 增、改
    public void putData(String tableName, String rowKey, String cF, String cN, long timeStamp, String value) throws IOException {

        // 獲取表對象
        Table table = connection.getTable(TableName.valueOf(tableName));

        // 創建put對象
        Put put = new Put(Bytes.toBytes(rowKey));

        // 添加數據
        put.addColumn(Bytes.toBytes(cF), Bytes.toBytes(cN), timeStamp, Bytes.toBytes(value));

        // 執行添加操作
        table.put(put);

        // System.out.println("已完成添加");

        table.close();

        return;
    }

    // 刪除
    public void deleteData(String tableName, String rowKey, String cF, String cN) throws IOException {

        // 獲取 table 對象
        Table table = connection.getTable(TableName.valueOf(tableName));

        // 創建 Delete 對象(rowKey)
        Delete delete = new Delete(Bytes.toBytes(rowKey));
        delete.addColumns(Bytes.toBytes(cF), Bytes.toBytes(cN));
        // delete.addColumn(Bytes.toBytes(cF),Bytes.toBytes(cN));

        // 執行刪除動作
        table.delete(delete);

        System.out.println("刪除成功");

        table.close();
    }

    // 查詢數據
    // 全表掃描
    public void scanTable(String tableName) throws IOException {

        // 獲取 table 對象
        Table table = connection.getTable(TableName.valueOf(tableName));

        // 構建掃描器
        Scan scan = new Scan();

        ResultScanner results = table.getScanner(scan);

        // 遍歷數據並Print
        for (Result result : results) {
            Cell[] cells = result.rawCells();
            for (Cell cell : cells) {
                System.out.println("RK:" + Bytes.toString(CellUtil.cloneRow(cell))
                        + ", CF:" + Bytes.toString(CellUtil.cloneFamily(cell))
                        + ", CN:" + Bytes.toString(CellUtil.cloneQualifier(cell))
                        + ", VALUE:" + Bytes.toString(CellUtil.cloneValue(cell)));

            }
        }

        table.close();
    }

    public boolean tableExist(String tableName) throws IOException {

        //執行
        boolean tableExists = admin.tableExists(TableName.valueOf(tableName));

        //admin.close();

        return tableExists;
    }

    // create table
    public void createTable(String tableName, String... columnFamily) throws IOException {

        if (tableExist(tableName)) {
            System.out.println("表已經存在");
            return;
        }

        // 創建table description
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
        // HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));

        // 添加 columnFamily
        for (String cfs : columnFamily) {

            // hTableDescriptor.addFamily(hColumnDescriptor);
            // 創建 columnDescriptor
            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cfs));

            // 設置column建表參數
            columnFamilyDescriptorBuilder.setMaxVersions(3);

            // 保存設置到table descriptor
            tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptorBuilder.build());
        }

        //創建表
        admin.createTable(tableDescriptorBuilder.build());

        System.out.println("表創建成功");
        return;
    }

    public void createTable(String tableName, String [] splitKeys, String... columnFamily) throws IOException {

        if (tableExist(tableName)) {
            System.out.println("表已經存在");
            return;
        }

        byte[][] bytesplitKeys = arr_to_byteArr(splitKeys);

        // 創建table description
        TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(TableName.valueOf(tableName));
        // HTableDescriptor hTableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));

        // 添加 columnFamily
        for (String cfs : columnFamily) {

            // hTableDescriptor.addFamily(hColumnDescriptor);
            // 創建 columnDescriptor
            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(cfs));

            // 設置column建表參數
            columnFamilyDescriptorBuilder.setMaxVersions(3);

            // 保存設置到table descriptor
            tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptorBuilder.build());
        }

        //創建表
        admin.createTable(tableDescriptorBuilder.build(), bytesplitKeys);

        System.out.println("表創建成功");
        return;
    }

    // 將 string [] 轉成 byte [][]
    public byte[][] arr_to_byteArr(String[] arr){

        int i = 0;
        byte[][] bytes = new byte[arr.length][];

        for (String item : arr){
            byte[] bytes1 = Bytes.toBytes(item);
            bytes[i] = bytes1;
            i++;
        }

        return bytes;
    }

    // delete table
    public void deleteTable(String tableName) throws IOException {

        if (!tableExist(tableName)) {
            System.out.println("表並沒有存在");
            return;
        }

        // 使表不可用
        admin.disableTable(TableName.valueOf(tableName));

        // 執行刪除操作
        admin.deleteTable(TableName.valueOf(tableName));

        System.out.println("表已刪除");
    }

    // 查詢數據
    // 獲取指定column
    public void getData(String tableName, String rowKey, String cF, String cN) throws IOException {

        // 獲取表對象
        Table table = connection.getTable(TableName.valueOf(tableName));

        // 創建一個Get對象
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addColumn(Bytes.toBytes(cF), Bytes.toBytes(cN));
        // get.readAllVersions();

        // 獲取數據的操作
        Result result = table.get(get);
        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            printfCell(cell);
        }
    }

    // 根據只指定 RK & CF，獲取 範圍內的 column
    public void getData(String tableName, String rowKey, String cF) throws IOException {

        // 獲取表對象
        Table table = connection.getTable(TableName.valueOf(tableName));

        // 創建一個Get對象
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes(cF));
        // get.readAllVersions();

        // 獲取數據的操作
        Result result = table.get(get);
        Cell[] cells = result.rawCells();
        for (Cell cell : cells) {
            printfCell(cell);
        }
    }

    // 只取得 Column Qualifier 的資料
    public String [] getCQData(String tableName, String rowKey, String cF) throws IOException {

        // 獲取表對象
        Table table = connection.getTable(TableName.valueOf(tableName));

        // 創建一個Get對象
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes(cF));
        // get.readAllVersions();

        // 獲取數據的操作
        int i = 0;
        Result result = table.get(get);
        Cell[] cells = result.rawCells();
        String [] ans = new String[result.size()];
        for (Cell cell : cells) {

            // 查看 cell 裡面資料
            // printfCell(cell);

            // 存儲 column qualifier 到 ans 中
            ans [i] = Bytes.toString(CellUtil.cloneQualifier(cell));
            i++;
        }

        return ans;
    }

    // 只取得 value 的資料
    public String [] getValueData(String tableName, String rowKey, String cF, String cN) throws IOException {

        // 獲取表對象
        Table table = connection.getTable(TableName.valueOf(tableName));

        // 創建一個Get對象
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addColumn(Bytes.toBytes(cF), Bytes.toBytes(cN));
        get.readAllVersions();

        // 獲取數據的操作
        int i = 0;
        Result result = table.get(get);
        Cell[] cells = result.rawCells();
        String [] ans = new String[result.size()];
        for (Cell cell : cells) {

            // 查看 cell 裡面資料
            // printfCell(cell);

            // 存儲 value 到 ans 中
            ans [i] = Bytes.toString(CellUtil.cloneValue(cell));
            i++;
        }

        return ans;
    }

    // 限定 stamp 範圍，來取得 column qualifier, value 的資料
    public cq_value getCqValueData(String tableName, String rowKey, String cF, long minStamp, long maxStamp) throws IOException {

        // 獲取表對象
        Table table = connection.getTable(TableName.valueOf(tableName));

        // 創建一個Get對象
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes(cF));
        get.setColumnFamilyTimeRange(Bytes.toBytes(cF), minStamp, maxStamp);
        // get.readAllVersions();

        // 獲取數據的操作
        int i = 0;
        Result result = table.get(get);
        Cell[] cells = result.rawCells();
        String[] ans_val = new String[result.size()];
        String[] ans_cq = new String[result.size()];
        for (Cell cell : cells) {

            // 查看 cell 裡面資料
            // printfCell(cell);

            // 存儲 cq, value 到 ans 中
            ans_val [i] = Bytes.toString(CellUtil.cloneValue(cell));
            ans_cq [i] = Bytes.toString(CellUtil.cloneQualifier(cell));
            i++;
        }

        cq_value Cq_Value = new cq_value(ans_cq, ans_val);

        return Cq_Value;
    }



    // 打印 hbase 單位資料 裡面存儲的資料
    public void printfCell(Cell cell){
        System.out.println("RK:" + Bytes.toString(CellUtil.cloneRow(cell))
                + ", CF:" + Bytes.toString(CellUtil.cloneFamily(cell))
                + ", CN:" + Bytes.toString(CellUtil.cloneQualifier(cell))
                + ", VALUE:" + Bytes.toString(CellUtil.cloneValue(cell)));
    }


    public Admin getAdmin() {
        return admin;
    }


}
