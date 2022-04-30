package com.tools;

public class timer {

    private long startTime = 0;
    private long stopTime = 0;
    private long AllRunTime = 0;
    private int AllRunTimeCount = 0;

    public void startTimer(){
        startTime = System.currentTimeMillis();
    }

    public void stopTimer(){
        stopTime = System.currentTimeMillis();
    }

    public void printRunTime(){

        if (startTime >= stopTime) {
            System.out.println("ERROR: timer doesn't be used correctly");
        }

        System.out.println("The code run: " + (stopTime-startTime) +"ms");

    }

    public long getRunTime(){

        if (startTime >= stopTime) {
            System.out.println("ERROR: timer doesn't be used correctly");
        }

        return stopTime-startTime;
    }

    public void recordRunTime(){
        AllRunTime += getRunTime();
    }

    public long getAverageRunTime(){
        return AllRunTime/AllRunTimeCount;
    }

    public void initializeTimer(){
        startTime = 0;
        stopTime = 0;
        AllRunTime = 0;
        AllRunTimeCount = 0;
    }

}
