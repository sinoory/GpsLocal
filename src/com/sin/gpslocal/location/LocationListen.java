package com.sin.gpslocal.location;

import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationListen{

	private static final String TAG="Location";
	public void getLocation(Context context){
		double latitude=0.0;
		double longitude =0.0;

		LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
				if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
					android.location.Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
					if(location != null){
						latitude = location.getLatitude();
						longitude = location.getLongitude();
						}
				}else{
					LocationListener locationListener = new LocationListener() {
						
						// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
						@Override
						public void onStatusChanged(String provider, int status, Bundle extras) {
							Log.d(TAG,"onStatusChanged provider="+provider+" status="+status );
						}
						
						// Provider被enable时触发此函数，比如GPS被打开
						@Override
						public void onProviderEnabled(String provider) {
							
						}
						
						// Provider被disable时触发此函数，比如GPS被关闭 
						@Override
						public void onProviderDisabled(String provider) {
							
						}
						

						@Override
						public void onLocationChanged(
								android.location.Location location) {
							// TODO Auto-generated method stub
							Log.d(TAG,"onLocationChanged location="+location);
						}


						
					};
					locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000, 0,locationListener);   
					android.location.Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);   
					if(location != null){   
						latitude = location.getLatitude(); //经度   
						longitude = location.getLongitude(); //纬度
					}   
				}		
	}
	
}