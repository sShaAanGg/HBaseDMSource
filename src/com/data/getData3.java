package com.data;

import com.tools.HbaseTools;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class getData3 {

    private final int second2Milli = 1000;
    private String tableName = "table5";
    private String ColumnFamily = "All_position_time";

    /*
    此為取得第三張表的人而設計的
    是由rowKey + column qualifier 尋找 value
    placeCode 為場所代碼
    time 為時間，格式為：2022-04-30T00：00：00
    ---------------------------------------------
    rowkey: xxx_placeCode
    time 最後只會用到 2022-04-30T00 (小時位)，來尋找資料，因為第三張表的資料就是如此存入的
     */
    public String[] getData(HbaseTools hf, String placeCode, String time) throws IOException {

        // 取得rowKey (rk: xxx_placecode_time)
        String rowkey = getRowKey(placeCode, time);

        // 這裡的 time 因使用簡化time，故已經限定好範圍了，不須再度尋找範圍時間
        // time：2022-04-30T00
        time = time.substring(0, 13);

        // 從資料庫中獲取資料 2022-04-30T00
        String[] ans = hf.getValueData(tableName, rowkey, ColumnFamily, time);

        return ans;
    }

    // 獲得 xxx_placeCode
    public String getRowKey(String pos, String time){

        String row_key_arr [] = new String[]{"200", "201", "202", "203", "204", "205", "206", "207", "208"};

        // 取得 int 版本 placecode 之後需要做運算
        int pcode = Integer.parseInt(pos);

        // 取得 int 版本 year-month 之後做運算
        String year_month = time.substring(0, 4) + time.substring(5, 7);
        int t = Integer.parseInt(year_month);

        // 分配 rowkey_xxx
        int remainder = (pcode ^ t) % 9;

        String ans = row_key_arr[remainder] + "_" + pos;

        return ans;
    }

    private long getStampTime(String time){

        LocalDateTime dateTime = LocalDateTime.parse(time);
        long ts = dateTime.toEpochSecond(ZoneOffset.of("+08:00")) * second2Milli;
        return  ts;

    }


}
