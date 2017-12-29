package com.example.mymaptest;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity implements BDLocationListener,SmsReceive.BRInteraction
{
    private MapView mMapView = null;
    private BaiduMap mBaiduMap;
    private MapStatus mapStatus;
    private BitmapDescriptor bitmap;
    private OverlayOptions option;

    private LatLng point;
    double mLat1 = 30.488312;
    double mLon1 = 114.417308;

    //    private Button ding;
    private ImageView zoomIn,zoomOut,location;
    //    private EditText lat,lon;
//    private TextView smsContent;
    private String address;

    private NotificationManager notifyManager;
    private NotificationCompat.Builder builder;
    private Bitmap mBitmap;

    private SharedPreferences sp;
    private SharedPreferences.Editor edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
//        初始化控件
        initView();
//        初始化数据
        initData();
//        动态注册广播
        initBroadCast();
//        动态申请权限（安卓6.0以上）
        requestPermission();
//        ding.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View view)
//            {
//                mLat1 = Double.parseDouble(lat.getText().toString());
//                mLon1 = Double.parseDouble(lon.getText().toString());
//                point = new LatLng(mLat1, mLon1);
//                //构建Marker图标
//                bitmap = BitmapDescriptorFactory.fromResource(R.drawable.position);
//                //构建MarkerOption，用于在地图上添加Marker
//                option = new MarkerOptions().position(point).icon(bitmap);
//                //在地图上添加Marker，并显示
//                mBaiduMap.addOverlay(option);
//                mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
//                //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
//                MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
//                //改变地图状态
//                mBaiduMap.animateMapStatus(mMapStatusUpdate);
//            }
//        });
        zoomIn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                float zoomLevel = mBaiduMap.getMapStatus().zoom;
                if(zoomLevel<=20){
//					MapStatusUpdateFactory.zoomIn();
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomIn());
                    zoomOut.setEnabled(true);
                }else{
                    Toast.makeText(getApplication(), "已经放至最大！", Toast.LENGTH_SHORT).show();
                    zoomIn.setEnabled(false);
                }
            }
        });
        zoomOut.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                float zoomLevel = mBaiduMap.getMapStatus().zoom;
                if(zoomLevel>4){
                    mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomOut());
                    zoomIn.setEnabled(true);
                }else{
                    zoomOut.setEnabled(false);
                    Toast.makeText(getApplication(), "已经缩至最小！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        location.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String number = "15926291683";
                String message = "PU>>position#";
                SmsManager smsManager = SmsManager.getDefault();
                PendingIntent paIntent = PendingIntent.getBroadcast(MainActivity.this,0,new Intent("SMS_SENT"),0);
                PendingIntent deliveryIntent = PendingIntent.getBroadcast(MainActivity.this,0,new Intent("SMS_DELIVERED"),0);
                ArrayList<String> smses = smsManager.divideMessage(message);
                Iterator<String> iterator = smses.iterator();
                while(iterator.hasNext())
                {
                    String temp = iterator.next();
                    smsManager.sendTextMessage(number,null,temp,paIntent,deliveryIntent);
                }
            }
        });
    }
    public void initView()
    {
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.bmapView);
//        ding = (Button) findViewById(R.id.dingwei);
        zoomIn = (ImageView) findViewById(R.id.big);
        zoomOut = (ImageView) findViewById(R.id.small);
        location = (ImageView) findViewById(R.id.location);
//        lat = (EditText) findViewById(lat);
//        lon = (EditText) findViewById(lon);
//        smsContent = (TextView) findViewById(R.id.content);
        mMapView.showZoomControls(false);
        mMapView.removeViewAt(1);
        mMapView.removeViewAt(2);
        mBaiduMap = mMapView.getMap();
        notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.big);
        sp = MainActivity.this.getSharedPreferences("data",MODE_PRIVATE);
        edit = sp.edit();
    }
    public void initData()
    {
        mLat1 = Double.parseDouble(sp.getString("lat",""));
        mLon1 = Double.parseDouble(sp.getString("lon",""));
        point = new LatLng(mLat1, mLon1);
        //构建Marker图标
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.position);
        //构建MarkerOption，用于在地图上添加Marker
        option = new MarkerOptions().position(point).icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        //改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
    }
    public void initBroadCast()
    {
        IntentFilter intentFilter = new IntentFilter();
//        谷歌为了避免不良之人监听短信已经此广播隐藏，但是动作依然存在
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        SmsReceive smsReceive = new SmsReceive();
        registerReceiver(smsReceive,intentFilter);
        smsReceive.setBRInteractionListener(this);
    }


    @Override
    public void onReceiveLocation(BDLocation bdLocation)
    {
        address = bdLocation.getAddrStr();
    }

    @Override
    public void setText(String content)
    {
//        smsContent.setText(content);
        String[] cont = null;
        cont = content.split("#");mBaiduMap.clear();
//        lat.setText(cont[1]);
//        lon.setText(cont[2]);
        edit.putString("lat",cont[1]);
        edit.putString("lon",cont[2]);
        edit.commit();//将每次接收的经纬度保存到本地，用于数据初始化
//        mLat1 = Double.parseDouble(lat.getText().toString());
//        mLon1 = Double.parseDouble(lon.getText().toString());
        mLat1 = Double.parseDouble(cont[1]);
        mLon1 = Double.parseDouble(cont[2]);
        point = new LatLng(mLat1, mLon1);
        //构建Marker图标
        bitmap = BitmapDescriptorFactory.fromResource(R.drawable.position);
        //构建MarkerOption，用于在地图上添加Marker
        option = new MarkerOptions().position(point).icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);
        mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
        //改变地图状态
        mBaiduMap.animateMapStatus(mMapStatusUpdate);
        showNotification(cont[0]);
        notifyManager.notify(1,builder.build());
    }
    public void requestPermission()
    {
        boolean per = checkPersitionAll(new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS});
        if(per)
        {
            return;
        }
        else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.SEND_SMS}
                    ,1);
        }
    }
    public boolean checkPersitionAll(String[] permissions)
    {
        for(String permission : permissions)
        {
            if(ContextCompat.checkSelfPermission(this,permission)!= PackageManager.PERMISSION_GRANTED)
            {
                return false;
            }
        }
        return true;
    }
    public void showNotification(String flag)
    {
        if("0".equals(flag))
        {
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.logo)
                    .setContentTitle("警报")
                    .setContentText("手动报警")
                    .setLargeIcon(mBitmap);
        }
        else if("1".equals(flag))
        {
            builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.logo)
                    .setContentTitle("警报")
                    .setContentText("手动报警")
                    .setLargeIcon(mBitmap);
        }
        Intent intent = new Intent(this,MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pIntent);
        builder.setFullScreenIntent(pIntent,true);
        builder.setAutoCancel(true);

    }
//    权限申请回调
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                }
                return;
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("退出", "onDestroy: ");
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
