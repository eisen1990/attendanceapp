package com.planetory.io;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.RectF;
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
import android.support.v4.content.ContextCompat;
import android.util.Log;
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
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    BackPressCloseHandler backPressCloseHandler;

    //STORE_LOCATION 값은 추후 직원의 매장 정보를 가지고 오는데 사용될 변수
    private String STORE_LOCATION = "EASW";

    private final int TIMER_ID = 0;
    private boolean EMPLOY_STATE = false;

    private Timer timer;

    ClockView clockView;

    TextView dateNow;
    TextView wifiScan;

    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == TIMER_ID) {
                clockView.invalidate();
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

    public String getStartTimeFromDB() {
        /*
            DB 또는 앱에서 직원의 당일 스케줄 시작시간을 가지고 온다.
            현재는 null 값 반환한다.
            DB에서 받아온 Text 값을 float 형으로 교체해주는 루틴이 들어가야된다.
         */
        String start_time = null;


        return start_time;
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

            Log.d("BSSID value",result.BSSID);
            Log.d("SSID value",result.SSID);
            Log.i("capabilities value",result.capabilities);
            Log.i("frequency value",Integer.toString(result.frequency));
            Log.i("level value", Integer.toString(result.level));
            list.add(item);
            //현재는 SSID 일치하는 Wifi의 MAC 주소 가지고 온다.
            //앞으로 해당 매장에 출근했을 때 근무자의 매장 MAC 주소를 가지고 와서 해당 MAC 주소가 있는지 체크
            //함수 반환형도 바꿔줄 것.
            if(result.SSID.equals(location)) MACAddr = result.BSSID;
        }
        /*
            List View에 보여줄 세팅.
            사용하지 않음.
         */
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
        backPressCloseHandler = new BackPressCloseHandler(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dateNow = (TextView) findViewById(R.id.dateNow);
        wifiScan = (TextView) findViewById(R.id.wifi);

        clockView = (ClockView) findViewById(R.id.clock_view);

        //DB에서 오늘의 근무 시작 시간을 가지고 온다.
        clockView.setSTART_ANGLE(getStartTimeFromDB());
        setButtons();


        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            /*
                매 1초마다 Handler에 메세지 전송해서
                UI 업데이트를 해준다 디지털 시계 및 원호
             */
            @Override
            public void run() {
                try {
                    //시계 값 전달
                    Message msg = handler.obtainMessage();
                    msg.what = TIMER_ID;
                    msg.obj = getFormatDateTime();
                    handler.sendMessage(msg);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }, 1000, 1000);

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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setButtons() {
        //Start Button 세팅
        Button StartBtn = (Button)  findViewById(R.id.start_btn);
        StartBtn.setOnClickListener(onClickListener);

        //Stop Button 세팅
        Button StopBtn = (Button)  findViewById(R.id.stop_btn);
        StopBtn.setOnClickListener(onClickListener);

        Button BreakBtn = (Button)  findViewById(R.id.break_btn);
        BreakBtn.setOnClickListener(onClickListener);
    }

    Button.OnClickListener onClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_btn:
                    /*
                        근무 시작 버튼
                        Wifi 스캔 및 실제 출근시간 기록
                     */
                    String str = scanWifi(STORE_LOCATION);
                    wifiScan.setText(str);
                    EMPLOY_STATE = true;
                    clockView.setClockColor("#0000FF");
                    break;
                case R.id.stop_btn:
                    /*
                        근무 종료 버튼
                    */
                    Toast.makeText(MainActivity.this, "Stop", Toast.LENGTH_SHORT).show();
                    EMPLOY_STATE = false;
                    break;
                case R.id.break_btn:
                    /*
                        휴식 버튼
                        추후 토글 키로 변경할 예정
                     */
                    EMPLOY_STATE = false;
                    clockView.setClockColor("#FF0000");
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
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

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }
}
