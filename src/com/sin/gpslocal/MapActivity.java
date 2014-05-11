package com.sin.gpslocal;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.drafts.Draft_10;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.sin.pub.IWebSocket;
import com.sin.pub.IWebSocket.WSMsgListener;



public class MapActivity extends IGridMenuActivity  {
	private static final String TAG="MapActivity";

	public static final String EXT_LONGGITUDE="longitude";
	public static final String EXT_LATITUDE="latitude";


	BMapManager mBMapMan = null;
	MapView mMapView = null;
	TextView mtv=null;
	MapController mMapController=null;

	IWebSocket ws=null;
	MsgHandler mh = new MsgHandler();

	class MsgHandler extends Handler {
		public static final int MSG_RCV_WEBSOCKET_MSG=0;
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RCV_WEBSOCKET_MSG:
				try {
					JSONObject jsonObject = new JSONObject((String)msg.obj);
					String msgtype=jsonObject.getString("Msgtype");
					String msgs=URLDecoder.decode(jsonObject.getString("Msg"),"UTF-8");
					if(msgtype.equals("location")){
						JSONObject local = new JSONObject(msgs);
						setlocal(local.getDouble("longitude"),local.getDouble("latitude"));
					}else if(msgtype.equals("msg")){
						mtv.setText(msgs);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
			}
		}
		
		public void receiveWebMsg(String wsmsg){
			Message msg = android.os.Message.obtain();
            msg.what = MSG_RCV_WEBSOCKET_MSG;
            msg.obj = wsmsg;
            sendMessage(msg);

		}
	}
	
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
				if(mtv.getVisibility()==View.VISIBLE){
					animateView(mtv);
				}
			}}));
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_refresh,"上报",new IMenuClickLis(){

			@Override
			public void onMenuClick() {
				// TODO Auto-generated method stub
				ws.report("andsin");
				//ws.report(et.getText().toString());
				/*//
            	String[] pos=((EditText)findViewById(R.id.editText1)).getText().toString().split(",");
            	double longitude=Double.parseDouble(pos[0]);
            	double latitude=Double.parseDouble(pos[1]);
        		setlocal(longitude,latitude);
				 */
			}}));
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_refresh,"接受位置",new IMenuClickLis(){

			@Override
			public void onMenuClick() {
				// TODO Auto-generated method stub
				ws.report("andsin");
				//ws.report(et.getText().toString());
				/*//
            	String[] pos=((EditText)findViewById(R.id.editText1)).getText().toString().split(",");
            	double longitude=Double.parseDouble(pos[0]);
            	double latitude=Double.parseDouble(pos[1]);
        		setlocal(longitude,latitude);
				 */
			}}));
		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_refresh,"共享位置",new IMenuClickLis(){

			@Override
			public void onMenuClick() {
				// TODO Auto-generated method stub
				final Button bt=((Button) MapActivity.this.findViewById(R.id.button1));
				final EditText ett=((EditText) MapActivity.this.findViewById(R.id.edtTo));
				bt.setVisibility(bt.getVisibility()==View.VISIBLE?View.INVISIBLE:View.VISIBLE);
				ett.setVisibility(bt.getVisibility());
				bt.setText("共享位置");
				animateView(bt);
				animateView(ett);

				bt.setOnClickListener(new Button.OnClickListener() {
					public void onClick(View v) {
						ws.sendLocation(ett.getText().toString(),getLocationString());
					}
				});


			}}));

		mArrGridMenuItem.add(new GridMenuItem( R.drawable.menu_search,"连接",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
				//Toast.makeText(MapActivity.this, "设置GPS菜单被点击了", Toast.LENGTH_LONG).show();

				try {
					Map<String,String> httpHeaders = new HashMap<String,String>();
					httpHeaders.put("Origin", "http://192.168.5.103:9090");//go revel need this to work well for websocket
					ws = new IWebSocket(new URI( "ws://192.168.5.103:9090/app/feed?user=android" ), httpHeaders,60);
					//ws = new IWebSocket(new URI( "ws://192.168.5.103:9090/app/feed?user=android" ));
					ws.addListener(new WSMsgListener(){

						@Override
						public void onMessage(String message) {
							// TODO Auto-generated method stub
							//Log.d(TAG, "WSMsgListener msg="+message);
							mh.receiveWebMsg(message);
							//MapActivity.this.mtv.setText(message);
						}
						
					});

					Log.d(TAG,"IWebSocket start connect");
					//ws.connect();
					Log.d(TAG,"IWebSocket finish connect");
				} catch (URISyntaxException e) {
					Log.d(TAG,"IWebSocket exception run");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

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
		/*
        ((Button) this.findViewById(R.id.buttonsetparam)).setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
            	String[] pos=((EditText)findViewById(R.id.editTextParam)).getText().toString().split(",");
            	msInterval=Integer.parseInt(pos[0]);
            	mDistance=Integer.parseInt(pos[1]);
            	locatePosition();
        	}
        });        
		 */



	}


	public String getLocationString(){
		
		double longitude=0;
		double latitude=0;
		if(mLastlocation!=null){
			longitude=mLastlocation.getLongitude();
			latitude=mLastlocation.getLatitude(); 
		}
		JSONArray jsonArray = new JSONArray();
		JSONObject object = new JSONObject();
		try {
			object.put("longitude", longitude);
			object.put("latitude", latitude);
			jsonArray.put(object);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String js=jsonArray.toString();
		return js;
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
	Location mLastlocation=null;
	void locatePosition(){
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 从GPS获取最近的定位信息
		Location location = locationManager
				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		// 将location里的位置信息显示在EditText中
		mLastlocation=location;
		
		updateView(location,"LastPos");
		if(mLastListener!=null){
			locationManager.removeUpdates(mLastListener);
		}
		mLastListener=new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				// 当GPS定位信息发生改变时，更新位置
				updateView(location,"onLocationChanged");
				mLastlocation=location;
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

