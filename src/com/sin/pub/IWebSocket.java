package com.sin.pub;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ServerHandshake;

import android.util.Log;

/** This example demonstrates how to create a websocket connection to a server. Only the most important callbacks are overloaded. */
public class IWebSocket extends WebSocketClient {
	static final String TAG="IWebSocket";
	public IWebSocket( URI serverUri , Draft draft ) {
		super( serverUri, draft );
	}

	public IWebSocket( URI serverURI ) {
		super( serverURI );
	}
	
	public IWebSocket( URI serverUri , Map<String,String> httpHeaders , int connectTimeout ) {
		super(serverUri,new Draft_17(),httpHeaders,connectTimeout);
	}

	@Override
	public void onOpen( ServerHandshake handshakedata ) {
		Log.d(TAG, "opened connection" );
		// if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
	}

	@Override
	public void onMessage( String message ) {
		Log.d(TAG, "received: " + message );
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