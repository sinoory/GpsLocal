package com.sin.gpslocal;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.iflytek.tts.TtsService.Tts;
import com.sin.baidu.LocationOverlayDemo;
import com.sin.pub.AnimationUty;
import com.sin.pub.IGridMenuDialog;
import com.sin.pub.IWebSocket;
import com.sin.pub.IGridMenuActivity.GridMenuItem;
import com.sin.pub.IGridMenuActivity.IMenuClickLis;
import com.sin.pub.IWebSocket.WSMsgListener;
import com.sin.pub.MJson;
import com.sintech.AddStationDlg;
import com.sintech.RunningBus;

public class MenuDlg extends IGridMenuDialog {
	public static String TAG = "MenuDlg";
	IWebSocket ws = null;
	MsgHandler mh = new MsgHandler();

	public void setupGridMenu() {
		setResouce(R.layout.gridview_menu, R.id.gridview, R.layout.item_menu,
				R.id.item_image, R.id.item_text);
		/*
		mArrGridMenuItem.add(new GridMenuItem(R.drawable.menu_nightmode,
				"调试信息", new IMenuClickLis() {

					@Override
					public void onMenuClick() {
						// TODO Auto-generated method stub
						int res=Tts.JniCreate("/sdcard/vBook/Resource.irf");
						Log.e("Tts","JniCreate="+res);
						Log.e("Tts","JniSetParam="+Tts.JniSetParam(1282, 9830));
						res=Tts.JniSpeak("Google I/O 2013开发者大会还未开始时便已让许多人心生期待.");
						Tts.JniSpeak("注意标红的部分，这个文件直接写到");
						Log.e("Tts","JniSpeak="+res);
					}
				}));
		mArrGridMenuItem.add(new GridMenuItem(R.drawable.menu_nightmode,
				"test", new IMenuClickLis() {

					@Override
					public void onMenuClick() {
						// TODO Auto-generated method stub
						MapView mapview=((LocationOverlayDemo)mAct).mMapView;
						BDLocation location=((LocationOverlayDemo) mAct).mLocation;
						MyOverlay mOverlay = new MyOverlay(mAct.getResources().getDrawable(R.drawable.icon_marka),mapview);	
				         GeoPoint p3 = new GeoPoint ((int)((location.getLatitude()+0.0005f)*1E6),(int)((location.getLongitude()+0.0005f)*1E6));
				         OverlayItem item3 = new OverlayItem(p3,"覆盖物3","");
				         item3.setMarker(mAct.getResources().getDrawable(R.drawable.icon_marka));
				         mOverlay.addItem(item3);
				         
				         mapview.getOverlays().add(mOverlay);
				         mapview.refresh();

					}
				}));
				*/
		changeItem("连接",new GridMenuItem(R.drawable.menu_search,R.drawable.menu_search, "连接",
				new IMenuClickLis() {
					@Override
					public void onMenuClick() {
						// Toast.makeText(MapActivity.this, "设置GPS菜单被点击了",
						// Toast.LENGTH_LONG).show();

						try {
							Map<String, String> httpHeaders = new HashMap<String, String>();
							httpHeaders.put("Origin","http://121.43.234.157:5050");
							ws = new IWebSocket(new URI("ws://121.43.234.157:5050/testwebsocket"),httpHeaders, 6000);
                            Log.d("DBG","new socket finished,ws="+ws);
							//ws = new IWebSocket(new URI("ws://121.43.234.157:5050/app/feed?user=android"),httpHeaders, 60);
							ws.addListener(new WSMsgListener() {

								@Override
								public void onMessage(String message) {
									// TODO Auto-generated method stub
									Log.d("DBG", "WSMsgListener msg=" + message);
									mh.receiveWebMsg(message);
									// MapActivity.this.mtv.setText(message);
								}

							});
						} catch (Exception e) {
							Log.d("DBG", "XXXXXXXXXXXXXXXIWebSocket exception runXXXXXXXXXXX");
							e.printStackTrace();
						}

					}
				}));

		changeItem("上报",new GridMenuItem(R.drawable.menu_refresh,R.drawable.menu_refresh, "上报",
				new IMenuClickLis() {

					@Override
					public void onMenuClick() {
			            final EditText et=new EditText(mAct); 
			            new AlertDialog.Builder(mAct)   
			                .setTitle("用户名")   
			                .setView(et) 
			                .setPositiveButton("确定", new DialogInterface.OnClickListener(){ 
			                    @Override 
			                    public void onClick(DialogInterface arg0, int arg1) { 
			                        String newname=et.getText().toString(); 
			                        if(newname.equals("")){ 
			                            return; 
			                        } 
						            ws.report(newname);
			                    } 
			                     
			                })   
			                .setNegativeButton("取消", null)   
			                .show();

					}
				}));
		changeItem("showUser",new GridMenuItem( R.drawable.menu_refresh,R.drawable.menu_refresh,"showUser",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
		        Intent intent = new Intent();
		        intent.setClass(mAct, UserList.class);
		        mAct.startActivity(intent);
				
			}}));
		changeItem("addStation",new GridMenuItem( R.drawable.menu_refresh,R.drawable.menu_refresh,"addStation",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
                Intent i = new Intent(mAct, AddStationDlg.class);
                i.putExtra("BOTTOM_EDIT_TXT",getLocationStr(((LocationOverlayDemo) mAct).mLocation));
                i.putExtra("MID_EDIT_TXT",(((LocationOverlayDemo) mAct).mAddressStr));
                i.putExtra("FIRST_EDIT_TXT",
                    PreferenceManager.getDefaultSharedPreferences(mAct).getString("lastLine",""));
                MJson.stringToJsonLine();
                mAct.startActivity(i);

			}}));
		changeItem("showBus",new GridMenuItem( R.drawable.menu_refresh,R.drawable.menu_refresh,"showBus",new IMenuClickLis(){
			@Override
			public void onMenuClick() {
                Intent i = new Intent(mAct, RunningBus.class);
                mAct.startActivity(i);
			}}));
		changeItem("共享位置",new GridMenuItem(R.drawable.menu_refresh,R.drawable.menu_refresh, "共享位置",
				new IMenuClickLis() {

					@Override
					public void onMenuClick() {
						// TODO Auto-generated method stub
						// /*
						final Button bt = ((Button) mAct
								.findViewById(R.id.button1));
						final EditText ett = ((EditText) mAct
								.findViewById(R.id.edtTo));
						bt.setVisibility(bt.getVisibility() == View.VISIBLE ? View.INVISIBLE
								: View.VISIBLE);
						ett.setVisibility(bt.getVisibility());
						bt.setText("共享位置");
						if (bt.getVisibility() == View.VISIBLE) {
							AnimationUty.animateLeft2RightView(bt);
							AnimationUty.animateLeft2RightView(ett);
						}
						bt.setOnClickListener(new Button.OnClickListener() {
							public void onClick(View v) {
								ws.sendLocation(
										ett.getText().toString(),
										getLocationJsStr(((LocationOverlayDemo) mAct).mLocation));
							}
						});
						// */

					}
				}));

	}

	public String getLocationStr(BDLocation location) {

		double longitude = 0;
		double latitude = 0;
		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
		}
        return longitude+":"+latitude;
    }
	public String getLocationJsStr(BDLocation location) {

		double longitude = 0;
		double latitude = 0;
		if (location != null) {
			longitude = location.getLongitude();
			latitude = location.getLatitude();
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
		String js = jsonArray.toString();
		return js;
	}

    public void onExit(){
        ws.close();
    }

class MyOverlay extends ItemizedOverlay{

	public MyOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker, mapView);
	}
	

	@Override
	public boolean onTap(int index){
		OverlayItem item = getItem(index);
		return true;
	}
	
	@Override
	public boolean onTap(GeoPoint pt , MapView mMapView){
		return false;
	}
	
}

    void showPosition(double longitude,double latitude){
        Log.d("DBG","showPosition("+longitude+","+latitude+")");
        MapView mapview=((LocationOverlayDemo)mAct).mMapView;
        BDLocation location=((LocationOverlayDemo) mAct).mLocation;
        MyOverlay mOverlay = new MyOverlay(mAct.getResources().getDrawable(R.drawable.icon_marka),mapview);	
        GeoPoint p3 = new GeoPoint ((int)((latitude)*1E6),(int)((longitude)*1E6));
        OverlayItem item3 = new OverlayItem(p3,"覆盖物3","");
        item3.setMarker(mAct.getResources().getDrawable(R.drawable.icon_marka));
        mOverlay.addItem(item3);

        mapview.getOverlays().add(mOverlay);
        mapview.refresh();

    }
	

	class MsgHandler extends Handler {
		public static final int MSG_RCV_WEBSOCKET_MSG = 0;

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_RCV_WEBSOCKET_MSG:
				try {
					JSONObject jsonObject = new JSONObject((String) msg.obj);
					String msgtype = jsonObject.getString("Msgtype");
					String msgs = URLDecoder.decode(
							jsonObject.getString("msg"), "UTF-8");
					if (msgtype.equals("location")) {
						JSONObject local = new JSONObject(msgs);
						showPosition(local.getDouble("longitude"),local.getDouble("latitude"));
					} else if (msgtype.equals("msg")) {
						// mtv.setText(msgs);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

		public void receiveWebMsg(String wsmsg) {
            Log.d("DBG","receiveWebMsg msg="+wsmsg);
			Message msg = android.os.Message.obtain();
			msg.what = MSG_RCV_WEBSOCKET_MSG;
			msg.obj = wsmsg;
			sendMessage(msg);

		}
	}

}
