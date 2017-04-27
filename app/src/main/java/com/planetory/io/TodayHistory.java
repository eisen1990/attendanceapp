package com.planetory.io;


import java.util.ArrayList;
import java.util.HashMap;

public class TodayHistory {
    private String startTime;
    private ArrayList<HashMap<String, String>> breakTimes;
    private String endTime;

    public TodayHistory(String startTime, ArrayList<HashMap<String, String>> breakTimes, String endTime) {
        this.startTime = startTime;
        this.breakTimes = breakTimes;
        this.endTime = endTime;
    }

    public TodayHistory() {
        this.startTime = "00:00:00";
        this.endTime = null;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public ArrayList<HashMap<String, String>> getBreakTimes() {
        return breakTimes;
    }

    public void setBreakTimes(HashMap<String, String> breakTime) {
        this.breakTimes.add(breakTime);
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
