package com.sin.gpslocal;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {
	private static final String TAG="MainActivity";
	
	private LocationManager locationManager;
	private EditText show;
	Button btTest;
	

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
                
        setContentView(R.layout.activity_main);
        
        show = (EditText) findViewById(R.id.editText1);
        btTest=(Button) this.findViewById(R.id.button1);
        btTest.setOnClickListener(new Button.OnClickListener() {
        	public void onClick(View v) {
        		
          
      }
        });
        
        		
        		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        		// 从GPS获取最近的定位信息
        		Location location = locationManager
        				.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        		// 将location里的位置信息显示在EditText中
        		updateView(location,"LastPos");
        		
        		// 设置每2秒获取一次GPS的定位信息
        		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
        				0, 0, new LocationListener() {

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
        				});
            	
        		
        	
        //};
  
    }

    private int cnt=0;
	private void updateView(Location location,String msg) {
		if (location != null) {
			

			StringBuffer sb = new StringBuffer();
			sb.append("["+cnt+++"]").append(msg).append("\n");
			sb.append("实时的位置信息：\n经度：");
			sb.append(location.getLongitude());
			sb.append("\n纬度：");
			sb.append(location.getLatitude());
			sb.append("\n高度：");
			sb.append(location.getAltitude());
			sb.append("\n速度：");
			sb.append(location.getSpeed());
			sb.append("\n方向：");
			sb.append(location.getBearing());
			sb.append("\n精度：");
			sb.append(location.getAccuracy());
			show.setText(sb.toString());
		} else {
			// 如果传入的Location对象为空则清空EditText
			show.setText("["+cnt+++"]"+msg);
		}
	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
  
    
}
