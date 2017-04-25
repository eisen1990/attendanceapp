package com.planetory.io;

public class HistoryItem {
    private String date;
    private String punch_in;
    private String punch_out;

    public HistoryItem(String date, String punch_in, String punch_out) {
        this.date = date;
        this.punch_in = punch_in;
        this.punch_out = punch_out;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPunch_in() {
        return punch_in;
    }

    public void setPunch_in(String punch_in) {
        this.punch_in = punch_in;
    }

    public String getPunch_out() {
        return punch_out;
    }

    public void setPunch_out(String punch_out) {
        this.punch_out = punch_out;
    }
}
