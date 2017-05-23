package com.planetory.io;

public class RequestWifiStoreItem {

    private String store_name;
    private String store_address;

    public RequestWifiStoreItem(String store_name, String store_address) {
        this.store_name = store_name;
        this.store_address = store_address;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public String getStore_address() {
        return store_address;
    }

    public void setStore_address(String store_address) {
        this.store_address = store_address;
    }
}
