package com.data;

import com.tools.HbaseTools;
import com.tools.cq_value;

import java.io.IOException;

public class getData2 {

    private String tableName = "table4";
    private String columnFamily = "People";
    private int second2Milli = 1000;


    /*
    此為取得第二張表的人而設計的
    因為是由rowKey來找出有關的人，因此rowkey的設計十分重要
    placeCode 為場所代碼
    time 為時間，格式為：2022-04-30T00：00：00
    ---------------------------------------------
    rowkey: xxx_placeCode_time
    time 只取到 2022-04-30T00 (小時位)，來方便統一
     */
    public String[] getData(HbaseTools hf, String placeCode, String time) throws IOException {

        // 取得rowKey (rk: xxx_placecode_time)
        String rowkey = getRowKey(placeCode, time);

        // 從資料庫中獲取資料
        String[] ans = hf.getCQData(tableName, rowkey, columnFamily);

        return ans;
    }

    public  String getRowKey(String pos, String time){

        String row_key_arr [] = new String[]{"100", "101", "102", "103", "104", "105", "106", "107", "108"};

        // 取得 int 版本 placecode 之後需要做運算
	//System.out.println("the placeCode: "+pos);
  	int pcode = Integer.parseInt(pos);

        // 取得 int 版本 year-month 之後做運算
        String year_month = time.substring(0, 4) + time.substring(5, 7);
        int t = Integer.parseInt(year_month);

        // 分配 rowkey_xxx
        int remainder = (pcode ^ t) % 9;

        // 取時間長度時，因為要放在rowkey中，需要方便的拿出來，故設計到 2022-04-30T00，來方便統一
        String ans = row_key_arr[remainder] + "_" + pos + "_" + time.substring(0, 13);

        return ans;
    }

}
