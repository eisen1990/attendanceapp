package com.planetory.io;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WifiControl {

    private Context context;


    public WifiControl(Context context) {
        this.context = context;
    }

    public String scanWifi(String location) {
        /*
            출근 버튼을 누를 경우 수행되는 Wi-Fi 스캔 함수.
            근무자의 출근 위치를 받아서 해당 매장의 Wi-Fi를 스캔할 수 있는지 확인한다.
         */

        WifiManager wifiManager;
        ConnectivityManager connManager;
        List<ScanResult> scanResult;
        SimpleAdapter adapter;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

        if (isLocationAwareness()) return "wifiaperror";


        String MACAddr = null;
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        scanResult = wifiManager.getScanResults();
        list.clear();

        /*
        for (ScanResult result : scanResult) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("bssid", "BSSID : " + result.BSSID);
            item.put("ssid", "SSID : " + result.SSID);
            item.put("capabilities", "Capablities : " + result.capabilities);
            item.put("freq", "Freq : " + result.frequency);
            item.put("level", "Signal Level : " + result.level);

            list.add(item);
            //현재는 SSID 일치하는 Wifi의 MAC 주소 가지고 온다.
            //앞으로 해당 매장에 출근했을 때 근무자의 매장 MAC 주소를 가지고 와서 해당 MAC 주소가 있는지 체크
            //함수 반환형도 바꿔줄 것.
            if (result.SSID.equals(location)) MACAddr = result.BSSID;
//            ((TextView) ((Activity) context).findViewById(R.id.wifi)).setText(MACAddr);
        }
        */
        MACAddr = scanResult.get(0).BSSID;
        return MACAddr;
    }

    public JSONObject scanWifi() {
        WifiManager wifiManager;
        ConnectivityManager connManager;
        List<ScanResult> scanResult;
        SimpleAdapter adapter;

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        scanResult = wifiManager.getScanResults();
//        wifiItems.clear();

        JSONObject obj = new JSONObject();
        try {
            JSONArray jsonArray = new JSONArray();
            for (ScanResult result : scanResult) {
                JSONObject sObj = new JSONObject();
                sObj.put("BSSID", result.BSSID);
                sObj.put("SSID", result.SSID);
//                sObj.put("Capabilities", result.capabilities);
//                sObj.put("Freq", result.frequency);
//                sObj.put("Level", result.level);
                jsonArray.put(sObj);
            }
            obj.put("wifiaplist", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("eisen", obj.toString());

        return obj;
    }

    public ArrayList<WifiItem> scanWifi(boolean b) {
        ArrayList<WifiItem> wifiItems = new ArrayList<>();

        WifiManager wifiManager;
        ConnectivityManager connManager;
        List<ScanResult> scanResult;
        SimpleAdapter adapter;

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        scanResult = wifiManager.getScanResults();
        wifiItems.clear();
        for (ScanResult result : scanResult) {
            WifiItem item = new WifiItem(result.BSSID, result.SSID, result.capabilities, String.valueOf(result.frequency), String.valueOf(result.level));
            wifiItems.add(item);
        }
        return wifiItems;
    }

    public boolean isLocationAwareness() {
        /*
            위치 인식 메서드 추후 구현
            Wifi 스캔하기 전에 위치 기반부터 검사한다.
         */

        return false;
    }

    public boolean isWifi() {
        boolean flag = false;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if(wifiManager.isWifiEnabled()) {
            flag = true;
        }

        return flag;
    }

    public void setWifi() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
    }

}
