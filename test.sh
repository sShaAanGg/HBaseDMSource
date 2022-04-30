#!bin/bash
java -cp $CLASSPATH:target Processor > ./output/output1.txt
java -cp $CLASSPATH:target/DataGet.jar com.data.DataGet >> ./output/output1.txt
