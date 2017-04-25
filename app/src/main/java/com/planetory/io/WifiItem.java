package com.planetory.io;

public class WifiItem {
    private String BSSID;
    private String SSID;
    private String Capabilities;
    private String Freq;
    private String Level;

    public WifiItem(String BSSID, String SSID, String capabilities, String freq, String level) {
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.Capabilities = capabilities;
        this.Freq = freq;
        this.Level = level;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getCapabilities() {
        return Capabilities;
    }

    public void setCapabilities(String capabilities) {
        Capabilities = capabilities;
    }

    public String getFreq() {
        return Freq;
    }

    public void setFreq(String freq) {
        Freq = freq;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }
}
