package com.planetory.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView dateNow;
    TextView wifiScan;

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == 0) {
                String time = (String) msg.obj;
                dateNow.setText(time);
            }
            return true;
        }
    });

    public String getFormatDateTime() {
        /*
            현재 시간을 문자열로 반환하는 함수.
            Main Activity에서 시계 UI를 업데이트하는 Thread에서 사용된다.
         */
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        return new SimpleDateFormat("HH:mm:ss").format(date);
    }

    public String scanWifi() {
        /*
            출근 버튼을 누를 경우 수행되는 Wi-Fi 스캔 함수.
         */

        //Wifi 관련 클래스 정의
        WifiManager wifiManager;
        ConnectivityManager connManager;
        List<ScanResult> scanResult;
        SimpleAdapter adapter;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();

        String MACAddr = null;
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        scanResult = wifiManager.getScanResults();
        list.clear();

        for(ScanResult result: scanResult) {
            HashMap<String,String> item = new HashMap<String,String>();
            item.put("bssid","BSSID : " + result.BSSID);
            item.put("ssid","SSID : " + result.SSID);
            item.put("capabilities","Capablities : " + result.capabilities);
            item.put("freq","Freq : " + result.frequency);
            item.put("level","Signal Level : " + result.level);
            list.add(item);
            if(result.SSID.equals("EASWLabs")) MACAddr = result.BSSID;
        }
        /*
        String[] from = new String[] {
                "bssid",
                "ssid",
                "capabilities",
                "freq",
                "level"
        };

        int[] to = new int[] {
                R.id.bssid,
                R.id.ssid,
                R.id.capabilities,
                R.id.Freq,
                R.id.level
        };

        adapter = new SimpleAdapter( this, list, R.layout.activity_main, from, to);

//        setListAdapter(adapter);
        */

        return MACAddr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateNow = (TextView) findViewById(R.id.dateNow);
        wifiScan = (TextView) findViewById(R.id.wifi);

        Thread timerThread = new Thread(new Runnable() {
            /*
                Main Activity의 UI 업데이트를 담당하는 Thread의 Runnable 구현
                초기에 thread.run()으로 할 때 메인 Thread의 콜스택을 사용해서 안됐었음.
                thread.start()로 실행하니 정상작동함.
             */
            @Override
            public void run() {
                while(true) {
                    try {
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        msg.obj = getFormatDateTime();
                        handler.sendMessage(msg);

                        Thread.sleep(1000);
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }
            }
        });
        timerThread.start();

        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        Button StartBtn = (Button)  findViewById(R.id.start_btn);
        StartBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String str = scanWifi();
                wifiScan.setText(str);
            }
        });

        /*
            근무 종료 버튼, Service Thread 종료한다.
            stopService(intent);
         */
        Button StopBtn = (Button)  findViewById(R.id.stop_btn);
        StopBtn.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Stop", Toast.LENGTH_SHORT).show();
                intent = new Intent(MainActivity.this, TimerService.class);
                stopService(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
