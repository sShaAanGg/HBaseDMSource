The source code for my HBase project data manipulation  
The source code for data generation is located in https://github.com/Linshuanting/HbaseDataGenerate

Contributors:  
[sShaAanGg](https://github.com/sShaAanGg)  
[Linshuanting](https://github.com/Linshuanting)
## Environment
1. **Centos7.9**
2. **Java-1.8.0_202**
3. **Hadoop-3.2.1**
4. **Zookeeper-3.6.3**
5. **Hbase-2.3.7**
6. **maven-3.8.5**

## Referenced libraries
Java

org.apache.hadoop.hbase (essential for HBase client API)  
~~org.apache.poi (for the excel format input file)~~

---
**Please run the commands below at the root directory (HBaseDMsource)**
### CLASSPATH environment variable
```export CLASSPATH=$HBASE_HOME/lib/*:$HADOOP_HOME/lib/native/*:$HADOOP_HOME/share/hadoop/client/*:$HADOOP_HOME/share/hadoop/common/lib/*:$HADOOP_HOME/share/hadoop/common/*```
### Compilation
```javac -d target/ -cp target:$CLASSPATH src/*.java```

```javac -d target -cp target:$CLASSPATH src/com/tools/*.java```  
```javac -d target -cp target/com/tools/:$CLASSPATH:target/ src/com/data/*.java```
### jar creation
```cd target && jar -cfe DataGet.jar com/data/DataGet com/data/*.class com/tools/*.class && cd ..```
### Execution
```java -cp $CLASSPATH:target/ Processor```  
```java -cp $CLASSPATH:target/DataGet.jar com.data.DataGet```
## source code explanation
**There is no main() function in GetData(). Functions in GetData.*() are called by Processor.**

There are 2 tables currently. PutData1.java and PutData2.java puts data into table1 and table2 respectively; Processor calls getData() from **GetData1** and **GetData2**, which fetches data from **table1** through the row keys of covid-19 patients(their phone numbers); then we would get a Map<Integer, Long> which maps the place codes(locations) visited by them to the corresponding timestamps. Next, we can fetch data from **table2** to determine who must be quarantined. 

| Schema | Row Key | Column Family | Column Qualifier | value |
| --- | --- | --- | --- | --- |
| 1st | phone number | **pos**ition(VERSIONS => 100) | position code | location |
| 2nd | location | **pho**ne_numbers(VERSIONS => 100) | phone number | position code |
<!-- | 3rd | phone_number#day | **pos**ition(VERSIONS => 3) | position code | location |
| 4th | location#day | **pho**ne number(VERSIONS => 3) | phone_number | position code |
| 5th | phone_number#week | **pos**ition(VERSIONS => 20) | position code | location |
| 6th | location#week | **pho**ne number(VERSIONS => 20) | phone_number | position code | -->

### MAP Schema
| Map | (RK) place code | (CF) pos | (CQ) position code | (value) isPositionCodeExist |
```
hbase(main):004:0> desc 'MAP'
Table MAP is ENABLED                                                                                                                                          
MAP                                                                                                                                                           
COLUMN FAMILIES DESCRIPTION                                                                                                                                   
{NAME => 'pos', BLOOMFILTER => 'ROW', IN_MEMORY => 'false', VERSIONS => '1', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', COMPRESSION => 'NON
E', TTL => 'FOREVER', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}                                              

1 row(s)
Quota is disabled
```
```
hbase(main):005:0> scan "MAP"
1000000 row(s)
Took 127.8527 seconds
```
### PEOPLE Schema
| Map | (RK) phone number | (CF) liv | (CQ) living pattern | (value) name |
```
hbase(main):007:0> desc 'PEOPLE'
Table PEOPLE is ENABLED                                                                                                                                       
PEOPLE                                                                                                                                                        
COLUMN FAMILIES DESCRIPTION                                                                                                                                   
{NAME => 'liv', BLOOMFILTER => 'ROW', IN_MEMORY => 'false', VERSIONS => '1', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', COMPRESSION => 'NON
E', TTL => 'FOREVER', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}                                              

1 row(s)
Quota is disabled
```
```
hbase(main):009:0> scan 'PEOPLE'
99955 row(s)
Took 22.5512 seconds
```
(It was really weird that 45 people were lost)

# Test Condition
Some points are visited by 1 covid-19 patient whose phone number is 0999999228. Thus we have a Map<Integer, LocalDateTime> which maps the place codes(locations) visited by 0999999228 to the corresponding timestamps.

## Hbase peformance evaluation (distibuted mode)
### 1st Schema
| 1st | phone number | position(VERSIONS => 100) | position code | location |  
***Row key: phone number; Column family: position(VERSIONS => 100)***; Column qualifier: position code; value: locaion
```
hbase:018:0> desc 'table1'
Table table1 is ENABLED
table1
COLUMN FAMILIES DESCRIPTION
{NAME => 'pos', BLOOMFILTER => 'ROW', IN_MEMORY => 'false', VERSIONS => '100', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', COMPRESSION =>
'NONE', TTL => 'FOREVER', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}

1 row(s)
Quota is disabled
```

### 2nd Schema
| 2nd | location | phone_numbers(VERSIONS => 100) | phone number | position code |  
***Row key: location; Column family: phone_numbers(VERSIONS => 100)***; Column qualifier: phone number; value: position code
```
hbase:011:0> desc 'table2'
Table table2 is ENABLED
table2
COLUMN FAMILIES DESCRIPTION
{NAME => 'pho', BLOOMFILTER => 'ROW', IN_MEMORY => 'false', VERSIONS => '100', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', COMPRESSION =>
'NONE', TTL => 'FOREVER', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}

1 row(s)
Quota is disabled
```
## Result of our experiment
![](/../main/assets/Result.png)
### Pattern 01 Table Design

#### Table01
**row_key**: (String) phonenum  
**columnFamily**: pos   
**columnQualifier**: (long)positionCode 
**value**: (int)placeCode   

#### Table02
**row_key**: (int)placeCode 
**columnFamily**: pho   
**columnQualifier**: (String)phonenum   
**value**: (long)positionCode   

### Pattern 02 Table Design

#### Table03
**row_key**: (String)xxx_phonenum   
**columnFamily**: All_of_the_time   
**columnQualifier**: (String)time   
**value**: (String)placeCode  

#### Table04
**row_key**: (String)xxx_placecode_time   
**columnFamily**: People   
**columnQualifier**: (String)phonenum   
**value**: null

### Pattern 03 Table Design

#### Table03
**row_key**: (String)xxx_phonenum   
**columnFamily**: All_of_the_time   
**columnQualifier**: (String)time   
**value**: (String)placeCode

#### Table05
**row_key**: (String)xxx_placecode   
**columnFamily**: All_position_time   
**columnQualifier**: (String)time   
**value**: phonenum


## Reference
1. https://javadoc.io/doc/org.apache.hbase/hbase-client/2.4.9/index.html
2. https://hbase.apache.org/2.4/book.html
3. https://cloud.google.com/bigtable/docs/schema-design
