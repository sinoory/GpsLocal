package com.sin.gpslocal;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;  
import com.baidu.mapapi.map.MKMapViewListener;  
import com.baidu.mapapi.map.MapController;  
import com.baidu.mapapi.map.MapPoi;  
import com.baidu.mapapi.map.MapView;  
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sin.pub.IGridMenuActivity;



public class MapActivity extends IGridMenuActivity {
	private static final String TAG="MapActivity";

	public static final String EXT_LONGGITUDE="longitude";
	public static final String EXT_LATITUDE="latitude";
	
	
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	TextView mtv=null;
	MapController mMapController=null;


    public void animateView(View v) {
        Log.d(TAG,"btnAnimListener v="+v);
        
        AnimationSet animSet = new AnimationSet(true);
        
        RotateAnimation ranim = new RotateAnimation(0f, 180f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f); // , 200, 200);
        
        ranim.setDuration(1000*3);
        
        TranslateAnimation tanim=new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        
        tanim.setDuration(600);
        
        //animSet.addAnimation(ranim);
        animSet.addAnimation(tanim);
        
        v.setAnimation(animSet);
    }
	
	void setupGridMenu(){
		setResouce(R.layout.gridview_menu,R.id.gridview,R.layout.item_menu,R.id.item_image,R.id.item_text);
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_nightmode,"调试信息",new IMenuClickLis(){

			@Override
			public void onMenuClick() {
				// TODO Auto-generated method stub
				mtv.setVisibility(mtv.getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
			}}));
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_refresh,"设置位置",new IMenuClickLis(){

			@Override
			public void onMenuClick() {
				// TODO Auto-generated method stub
				Button bt=((Button) MapActivity.this.findViewById(R.id.button1));
				bt.setVisibility(bt.getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
				EditText et =((EditText)findViewById(R.id.editText1));
				et.setVisibility(bt.getVisibility());
				if(bt.getVisibility()==View.VISIBLE){
					animateView(et);
					animateView(bt);
				}
				
				
			}}));
		
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_search,"设置GPS",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
				//Toast.makeText(MapActivity.this, "设置GPS菜单被点击了", Toast.LENGTH_LONG).show();
			}}));
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_quit,"退出",new IMenuClickLis(){

			@Override
			public void onMenuClick() {
				// TODO Auto-generated method stub
				if(mLastListener!=null){
					locationManager.removeUpdates(mLastListener);
				}
				MapActivity.this.finish();
			}}));
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	setupGridMenu();
        super.onCreate(savedInstanceState);
        mBMapMan=new BMapManager(getApplication());  
        mBMapMan.init(null);
        
        setContentView(R.layout.map_activity);
        mMapView=(MapView)findViewById(R.id.bmapsView);  
        mMapView.setBuiltInZoomControls(true);
        mMapController=mMapView.getController();  
        mMapController.setZoom(18);//设置地图zoom级别
        
        mtv=(TextView)findViewById(R.id.dbgtxt);
        
        Intent i = getIntent();
        
        float longitude=i.getFloatExtra(EXT_LONGGITUDE, -1.0f) ;//39.915f;//31.24f;//
        float latitude=i.getFloatExtra(EXT_LATITUDE, -1.0f);//116.404f;//121.48f;//
        if(longitude != -1.0f){
        	setlocal(longitude,latitude);
        	
        }else{
        	locatePosition();
        }
    	
        ((Button) this.findViewById(R.id.button1)).setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
            	String[] pos=((EditText)findViewById(R.id.editText1)).getText().toString().split(",");
            	double longitude=Double.parseDouble(pos[0]);
            	double latitude=Double.parseDouble(pos[1]);
        		setlocal(longitude,latitude);
        	}
        });

        ((Button) this.findViewById(R.id.buttonsetparam)).setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
            	String[] pos=((EditText)findViewById(R.id.editTextParam)).getText().toString().split(",");
            	msInterval=Integer.parseInt(pos[0]);
            	mDistance=Integer.parseInt(pos[1]);
            	locatePosition();
        	}
        });

        
    }
    
    
    
    void setlocal(double longitude,double latitude){
 	
    	Location location = new Location("");
    	location.setLatitude(latitude);
    	location.setLongitude(longitude);
    	updateView(location,"setlocal");
    }
    
    
    LocationManager locationManager=null;
    LocationListener mLastListener=null;
    int msInterval=0;
    int mDistance=0;
    void locatePosition(){
    	locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 从GPS获取最近的定位信息
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// 将location里的位置信息显示在EditText中
		updateView(location,"LastPos");
		if(mLastListener!=null){
			locationManager.removeUpdates(mLastListener);
		}
		mLastListener=new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				// 当GPS定位信息发生改变时，更新位置
				updateView(location,"onLocationChanged");
			}

			@Override
			public void onProviderDisabled(String provider) {
				updateView(null,"onProviderDisabled "+provider);
			}

			@Override
			public void onProviderEnabled(String provider) {
				// 当GPS LocationProvider可用时，更新位置
				updateView(locationManager
						.getLastKnownLocation(provider),"onProviderEnabled "+provider);

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				updateView(null,"onStatusChanged "+provider+" "+status);
			}
		};
		
		// 设置每2秒获取一次GPS的定位信息
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				msInterval, mDistance, mLastListener);

    }
    
    int cnt=0;
    private void updateView(Location location,String msg) {
    	String dbgs="["+cnt+++"]"+msg+"\n";
		if (location != null) {
	       	
        	// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
	       	
	       	double longitude=location.getLongitude();
	       	double latitude=location.getLatitude(); 
        	int y=(int)(longitude* 1E6);
        	int x=(int)(latitude* 1E6);
        	Log.d(TAG,"updateView GeoPoint x="+x+" y="+y+" lt="+longitude+" at="+latitude);
        	GeoPoint point =new GeoPoint(x,y);
        	GeoPoint p2 = CoordinateConvert.fromWgs84ToBaidu(point);
        	//GeoPoint p2 = CoordinateConvert.bundleDecode();
        	dbgs=dbgs+"updateView  x="+x+" y="+y+"\n lt="+longitude+" at="+latitude;
        	//用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)  
        	mMapController.setCenter(p2);//设置地图中心点  
        				
		}
		mtv.setText(dbgs);
    }
    
    @Override
    protected void onDestroy(){
            mMapView.destroy();
            if(mBMapMan!=null){
                    mBMapMan.destroy();
                    mBMapMan=null;
            }
            super.onDestroy();
    }
    @Override
    protected void onPause(){
            mMapView.onPause();
            if(mBMapMan!=null){
                   mBMapMan.stop();
            }
            super.onPause();
    }
    @Override
    protected void onResume(){
            mMapView.onResume();
            if(mBMapMan!=null){
                    mBMapMan.start();
            }
           super.onResume();
    }

	
}

