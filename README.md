The source code and bytecode for my HBase project DATA MANIPULATION

# Referenced libraries
1. org.apache.hadoop.hbase
2. org.apache.poi (the input source is excel format file)

# Case 1
Some points are visited by 1 covid-19 patient whose phone number is 0901615803. The data is of approximate 9109KB in .xlsx format in size and it involved in 4 week of data with timestamp.

## Hbase peformance (pseudo distibuted in localhost)
### 1st Schema
```
hbase:006:0> desc 'table1'
Table table1 is ENABLED
table1
COLUMN FAMILIES DESCRIPTION
{NAME => 'pos', BLOOMFILTER => 'ROW', IN_MEMORY => 'false', VERSIONS => '100', KEEP_DELETED_CELLS => 'FALSE', DATA_BLOCK_ENCODING => 'NONE', COMPRESSION =>
'NONE', TTL => 'FOREVER', MIN_VERSIONS => '0', BLOCKCACHE => 'true', BLOCKSIZE => '65536', REPLICATION_SCOPE => '0'}

1 row(s)
Quota is disabled
Took 0.1085 seconds
```
```
hbase:018:0> scan 'table1'
1000 row(s)
Took 27.7536 seconds
```
```
hbase:018:0> get 'table1', '0901615803'
1 row(s)
Took 0.0304 seconds
```
### 2nd Schema
### 2nd Schema
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
Took 0.0215 seconds
```
```
hbase:015:0> scan 'table2'
3946 row(s)
Took 26.5827 seconds
```
```
```
