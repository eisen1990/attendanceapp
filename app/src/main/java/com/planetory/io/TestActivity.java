package com.planetory.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;

public class TestActivity extends AppCompatActivity {


    WifiManager mWifiManager;

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (mWifiManager == null) return;
                List<ScanResult> a = mWifiManager.getScanResults();
                Toast.makeText(getApplicationContext(), String.valueOf(a.size()), Toast.LENGTH_SHORT).show();
                for (ScanResult b : a){

                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Context context = this.getApplicationContext();

        if (!RequestContact()) {
            return;
        }

        registerReceiver(mWifiScanReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS 기능을 켜주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();

        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = tMgr.getLine1Number();
        String serialNumber = tMgr.getSimSerialNumber();
        Toast.makeText(this, phoneNumber + " | " + serialNumber, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mWifiScanReceiver);
        super.onDestroy();
    }

    private static final int PHONE_STATE = 0;
    private static final int COARSE_LOCATION = 1;

    protected boolean RequestContact(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        if (checkSelfPermission(READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if(shouldShowRequestPermissionRationale(READ_PHONE_STATE)){
                Toast.makeText(this, "테스트좀 하자...", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{READ_PHONE_STATE}, PHONE_STATE);
            return false;
        }
        if (checkSelfPermission(ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)){
                Toast.makeText(this, "테스트좀 합시다 좀.", Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{ACCESS_COARSE_LOCATION}, COARSE_LOCATION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_STATE || requestCode == COARSE_LOCATION){
            RequestContact();
        }
    }
}
