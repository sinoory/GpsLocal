package com.sin.pub;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/** This example demonstrates how to create a websocket connection to a server. Only the most important callbacks are overloaded. */
public class IWebSocket  {
	static final String TAG="DBG";
	
	private MWebSocket mws=null;
	private Map<String,String> mhttpHeaders=null;
	private int mConnectTimeout=0;
	private URI mServerUri=null;
	private List<WSMsgListener> mMsgListeners=new ArrayList<WSMsgListener>();
	
	public IWebSocket( URI serverUri , Map<String,String> httpHeaders , int connectTimeout ) {
		mConnectTimeout=connectTimeout;
		mhttpHeaders=httpHeaders;
		mServerUri=serverUri;
		mws=new MWebSocket(mServerUri,mhttpHeaders,mConnectTimeout);
		mws.connect();
	}

	public interface WSMsgListener{
		public void onMessage( String message );
	}
	
	public void addListener(WSMsgListener ms){
		if(!mMsgListeners.contains(ms)){
			mMsgListeners.add(ms);
		}
	}
	public void rmListener(WSMsgListener ms){
		if(mMsgListeners.contains(ms)){
			mMsgListeners.remove(ms);
		}
	}
	
	class MWebSocket extends WebSocketClient {
	    public boolean isOpened=false;	
		public MWebSocket( URI serverUri , Map<String,String> httpHeaders , int connectTimeout ) {
			super(serverUri,new Draft_17(),httpHeaders,connectTimeout);
		}

		@Override
		public void onOpen( ServerHandshake handshakedata ) {
			Log.d("DBG", "ws opened connection" );
			// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
            isOpened=true;
		}

		@Override
		public void onMessage( String message ) {
			Log.d(TAG, "received: " + message );
			for (WSMsgListener ms : mMsgListeners){
				ms.onMessage(message);
			}
		}



		@Override
		public void onClose( int code, String reason, boolean remote ) {
			// The codecodes are documented in class org.java_websocket.framing.CloseFrame
			Log.d(TAG, "Connection closed by " + ( remote ? "remote peer" : "us" ) );
		}

		@Override
		public void onError( Exception ex ) {
			Log.d(TAG,"onError");
			ex.printStackTrace();
			// if the error is fatal then onClose will be called additionally
		}
		
	}

    public void close(){
        if(mws!=null){
            mws.close();
        }
    }

    public boolean isOpened(){
        return mws.isOpened;
    }
	public void sendmsg(String msg){
        Log.d("DBG","sendmsg mws.isClosed="+mws.isClosed()+",msg="+msg);
		if (mws.isClosed()){
			mws=new MWebSocket(mServerUri,mhttpHeaders,mConnectTimeout);
			mws.connect();
		}
		mws.send(msg);
	}
	
	
	
	public void sendmsg(String to,String msg){
		sendmsg(to,"msg",msg);
	}
	
	public void sendLocation(String to,String location){
		sendmsg(to,"location",location);
	}
	
	private void sendmsg(String to,String msgtype,String msg){
		//JSONArray jsonArray = new JSONArray();
		JSONObject object = new JSONObject();
		try {
			object.put("type", "send");
			object.put("to", to);
			object.put("Msgtype", msgtype);
			object.put("msg", URLEncoder.encode(msg,"UTF-8"));
			//jsonArray.put(object);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String js=object.toString();
        Log.d("DBG","sendmsg msg="+js);
		sendmsg(js);
	}

	public void report(String user){
		JSONObject object = new JSONObject();
		try {
			object.put("type", "report");
			object.put("user", user);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sendmsg(object.toString());
	}
}
