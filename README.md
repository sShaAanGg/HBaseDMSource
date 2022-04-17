The source code and bytecode for my HBase project DATA MANIPULATION

# Referenced libraries
Java

1. org.apache.hadoop.hbase (essential for HBase client API)
2. org.apache.poi (for the excel format input file)

Python

3. numpy
4. matplotlib

## source code explanation
**There is no main() function in GetData(). Functions in GetData.*() are called by Processor.**

There are 2 tables currently. PutData1.java and PutData2.java puts data into table1 and table2 respectively; Processor calls getData() from **GetData1** and **GetData2**, which fetches data from **table1** through the row keys of covid-19 patients(their phone numbers); then we would get a Map<Integer, Long> which maps the place codes(locations) visited by them to the corresponding timestamps. Next, we can fetch data from **table2** to determine who must be quarantined. 

| Schema | Row Key | Column Family | Column Qualifier | value |
| --- | --- | --- | --- | --- |
| 1st | phone number | **pos**ition(VERSIONS => 100) | position code | location |
| 2nd | location | **pho**ne_numbers(VERSIONS => 100) | phone number | position code |
| 3rd | phone_number#day | **pos**ition(VERSIONS => 3) | position code | location |
| 4th | location#day | **pho**ne number(VERSIONS => 3) | phone_number | position code |
| 5th | phone_number#week | **pos**ition(VERSIONS => 20) | position code | location |
| 6th | location#week | **pho**ne number(VERSIONS => 20) | phone_number | position code |

<!-- (***OR use just 1 table to do this.*** That is, combine 2 table into one and its column families would be **pos**ition and **pho**ne numbers. Thus There would be **only 1 class for PutData and another for GetData**. Its schema would be like the one below:)
```
hbase:007:0> desc 'table'
Table table is ENABLED
table
COLUMN FAMILIES DESCRIPTION
{NAME => 'pho', BLOOMFILTER => 'ROW', IN_MEMORY => 'false', VERSIONS => '100', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', COMPRESSION =>
'NONE', TTL => 'FOREVER', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}

{NAME => 'pos', BLOOMFILTER => 'ROW', IN_MEMORY => 'false', VERSIONS => '100', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', COMPRESSION =>
'NONE', TTL => 'FOREVER', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}

2 row(s)
Quota is disabled
``` -->

# Case 1
Some points are visited by 1 covid-19 patient whose phone number is 0901615803. (The data is of approximate 9109KB in .xlsx format in size and it involved in 4 week of data with timestamp.) Thus we have a Map<Integer, LocalDateTime> which maps the place codes(locations) visited by 0901615803 to the corresponding timestamps.

## Hbase peformance (pseudo distibuted in localhost)
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
```
hbase:018:0> scan 'table1'
1000 row(s)
Took 28.0554 seconds
```
```
hbase:018:0> get 'table1', '0901615803'
1 row(s)
Took 0.0304 seconds
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
```
hbase:015:0> scan 'table2'
3946 row(s)
Took 26.5827 seconds
```

### 3rd Schema
| 3rd | phone_number#day | **pos**ition(VERSIONS => 3) | position code | location |

### 4th Schema
| 4th | location#day | **pho**ne number(VERSIONS => 3) | phone_number | position code |

### 5th Schema
| 5th | phone_number#week | **pos**ition(VERSIONS => 20) | position code | location |

### 6th Schema
| 6th | location#week | **pho**ne number(VERSIONS => 20) | phone_number | position code |

## Reference
1. https://javadoc.io/doc/org.apache.hbase/hbase-client/2.4.9/index.html
2. https://hbase.apache.org/2.4/book.html
3. https://cloud.google.com/bigtable/docs/schema-design
