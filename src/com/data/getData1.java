package com.data;

import com.tools.HbaseTools;
import com.tools.cq_value;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class getData1 {

    private String tableName = "table3";
    private String columnFamily = "All_of_the_time";
    private int second2Milli = 1000;

    /*
    此為固定獲得第一張 table 的 cq & value，此資料(str array)會存在 cq_value 的 object 中
    輸入 phonenum、時間範圍(需要的資料時間範圍)，即可獲得資料
    maxtime、mintime 的格式要為 Ex: 2022-04-30T00:00:00
    phonenum 格式為 09xxxxxxxx
     */
    public cq_value getData(HbaseTools hf, String phonenum, String minTime, String maxTime) throws IOException {

        // 取得 xxx_phonenum (rk: xxx_phonenum)
        String rowkey = getRowKey(phonenum);

        // 取得 stamp 的範圍，用來縮小資料
        long mints = getStampTime(minTime);
        long maxts = getStampTime(maxTime);

        // 從資料庫中獲取資料
        cq_value ans = hf.getCqValueData(tableName, rowkey, columnFamily, mints, maxts);

        return ans;
    }

    // time 的格式要為 Ex: 2022-04-30T00:00:00
    public long getStampTime (String time){

        LocalDateTime dateTime = LocalDateTime.parse(time);
        long ts = dateTime.toEpochSecond(ZoneOffset.of("+08:00")) * second2Milli;
        return ts;
    }

    public String getRowKey (String phoneNum){

        String row_key_arr [] = new String[]{"000", "001", "002", "003", "004", "005", "006", "007", "008"};

        // 計算需要的xxx
        int remainder = Integer.parseInt(phoneNum) % 9;

        return row_key_arr[remainder] + "_" + phoneNum;
    }
}
