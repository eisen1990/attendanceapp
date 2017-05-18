package com.planetory.io;


public class AcceptWifiItem {

    private String store;
    private String BSSID;

    public AcceptWifiItem(String store, String BSSID) {
        this.store = store;
        this.BSSID = BSSID;
    }

    public String getBSSID() {
        return BSSID;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
