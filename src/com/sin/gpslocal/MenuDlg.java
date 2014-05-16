package com.sin.gpslocal;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.sin.baidu.LocationOverlayDemo;
import com.sin.pub.AnimationUty;
import com.sin.pub.IGridMenuDialog;
import com.sin.pub.IWebSocket;
import com.sin.pub.IGridMenuActivity.GridMenuItem;
import com.sin.pub.IGridMenuActivity.IMenuClickLis;
import com.sin.pub.IWebSocket.WSMsgListener;

public class MenuDlg extends IGridMenuDialog {
	public static String TAG = "MenuDlg";
	IWebSocket ws = null;
	MsgHandler mh = new MsgHandler();

	public void setupGridMenu() {
		setResouce(R.layout.gridview_menu, R.id.gridview, R.layout.item_menu,
				R.id.item_image, R.id.item_text);
		mArrGridMenuItem.add(new GridMenuItem(R.drawable.menu_nightmode,
				"调试信息", new IMenuClickLis() {

					@Override
					public void onMenuClick() {
						// TODO Auto-generated method stub

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
		mArrGridMenuItem.add(new GridMenuItem(R.drawable.menu_search, "连接",
				new IMenuClickLis() {
					@Override
					public void onMenuClick() {
						// Toast.makeText(MapActivity.this, "设置GPS菜单被点击了",
						// Toast.LENGTH_LONG).show();

						try {
							Map<String, String> httpHeaders = new HashMap<String, String>();
							httpHeaders.put("Origin","http://192.168.5.103:9090");// go revel need this to work well for websocket
							ws = new IWebSocket(new URI("ws://192.168.5.103:9090/app/feed?user=android"),httpHeaders, 60);
							ws.addListener(new WSMsgListener() {

								@Override
								public void onMessage(String message) {
									// TODO Auto-generated method stub
									Log.d(TAG, "WSMsgListener msg=" + message);
									mh.receiveWebMsg(message);
									// MapActivity.this.mtv.setText(message);
								}

							});
						} catch (URISyntaxException e) {
							Log.d(TAG, "IWebSocket exception run");
							e.printStackTrace();
						}

					}
				}));

		mArrGridMenuItem.add(new GridMenuItem(R.drawable.menu_refresh, "上报",
				new IMenuClickLis() {

					@Override
					public void onMenuClick() {
						// TODO Auto-generated method stub
						ws.report("andsin");
					}
				}));
		mArrGridMenuItem.add(new GridMenuItem(R.drawable.menu_refresh, "共享位置",
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
										getLocationString(((LocationOverlayDemo) mAct).mLocation));
							}
						});
						// */

					}
				}));

	}

	public String getLocationString(BDLocation location) {

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
							jsonObject.getString("Msg"), "UTF-8");
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
			Message msg = android.os.Message.obtain();
			msg.what = MSG_RCV_WEBSOCKET_MSG;
			msg.obj = wsmsg;
			sendMessage(msg);

		}
	}

}