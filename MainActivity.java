package com.experiment.chickenjohn.materialdemo;

import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public android.os.Handler uiRefreshHandler = new android.os.Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case 0 :
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        naviHeaderSet(btManager.isConnected(),0);
                    }
                    break;
                case 1 :
                    if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                        naviHeaderSet(btManager.isConnected(),0);
                    }
                    btManager.enableBluetooth();
                    break;
                case 2 :
                    //Log.v("Received data:", Integer.toString(msg.arg1));
                    ecgDatabaseManager.addRecord(new EcgData(msg.arg1,msg.arg2));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
    private bluetoothManager btManager= new bluetoothManager(uiRefreshHandler);
    private EcgDatabaseManager ecgDatabaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            portLoading();
        }
        else{
            landLoading();
        }
        ecgDatabaseManager = new EcgDatabaseManager(this);
        btManager.enableBluetooth();
    }

    private void portLoading(){
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.portToolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ListView mainList = (ListView) findViewById(R.id.main_list);
        MeasuredData data1 = new MeasuredData("心率", 20);
        MeasuredData data2 = new MeasuredData("血糖", 360);
        SimpleAdapter mainListAdapter = new SimpleAdapter(this, generateList(data1, data2), R.layout.list_layout, new String[]{"typename", "value"}, new int[]{R.id.main_list_item_title, R.id.main_list_item_content});
        mainList.setAdapter(mainListAdapter);
    }

    private void landLoading(){
        setContentView(R.layout.activity_main_land);
        SurfaceView surfaceViewLand = (SurfaceView)findViewById(R.id.surfaceView_land);
        Toolbar toolbar = (Toolbar)findViewById(R.id.landToolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        int currentOrientation = this.getResources().getConfiguration().orientation;
        if(currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
            portLoading();
            Message uiRefreshMessage = Message.obtain();
            uiRefreshMessage.what = 0;
            uiRefreshHandler.sendMessage(uiRefreshMessage);
        }
        else if(currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
            landLoading();
        }
    }

    @Override
    protected void onResume(){
        this.registerReceiver(btManager.btReceiver, btManager.regBtReceiver());
        super.onResume();
    }

    @Override
    protected void onDestroy(){
        btManager.disableBluetooth();
        unregisterReceiver(btManager.btReceiver);
        ecgDatabaseManager.closeEcgDatabase();
        super.onDestroy();
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

        switch (id)
        {
            case R.id.wave_output:
                break;
            case R.id.data_output:
                break;
            case R.id.data_clear:
                break;
            case R.id.data_send:
                break;
            case R.id.btconnection:
                btManager.enableBluetooth();
                break;
            default:
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public ArrayList<HashMap<String,String>> generateList(MeasuredData... dataArray){
        ArrayList<HashMap<String,String>> finalList = new ArrayList<HashMap<String, String>>();
        for(MeasuredData dataTemp : dataArray){
            HashMap<String,String> dataMap = new HashMap<String, String>();
            dataMap.put("typename",dataTemp.getTypeName());
            dataMap.put("value",dataTemp.getValue());
            finalList.add(dataMap);
        }
        return finalList;
    }

    public boolean naviHeaderSet(boolean isConnected, int transNumber){
        TextView btAddressTextView = (TextView)this.findViewById(R.id.bt_address_textview);
        TextView connectTextView = (TextView)this.findViewById(R.id.connecttextview);
        TextView transNumberTextView = (TextView)this.findViewById(R.id.trans_number_textview);
        if (btAddressTextView != null) {
            if (isConnected) {
                btAddressTextView.setText("蓝牙设备地址：" + btManager.btAddress);
                connectTextView.setText("设备已连接");
                transNumberTextView.setText("接收数据："+Integer.toString(transNumber));
            } else {
                btAddressTextView.setText("蓝牙设备地址：");
                connectTextView.setText("设备未连接");
                transNumberTextView.setText("接收数据：");
            }
            return true;
        }
        else{
            return false;
        }
    }
}
