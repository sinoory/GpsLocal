package com.sin.pub;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import android.util.Log;

public class httpsconn{
	
	
	
    @SuppressWarnings("deprecation") 
    private static class TrustAnyHostnameVerifier implements javax.net.ssl.HostnameVerifier {

        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }

    }


    public static String conn(String remoteUrl){
        //SCRIPT_MARK.d("DBG","https conn start");
        InputStream is = null;
        PrintWriter pw = null;
        String str_return = "";
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() },
                    new java.security.SecureRandom());

            final String url = remoteUrl;
            //final String url = remoteUrl.substring(0,remoteUrl.indexOf("/", 8)) + "/servlet/keeplive";
            URL console = new URL(url);
            javax.net.ssl.HttpsURLConnection conn =  (HttpsURLConnection) console.openConnection();
            //    javax.net.ssl.internal.www.protocol.https.HttpsURLConnectionOldImpl conn =  (HttpsURLConnectionOldImpl) console.openConnection();
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.connect();
            is = conn.getInputStream();
            DataInputStream indata = new DataInputStream(is);
            String ret = "";
            while (ret != null) {
                ret = indata.readLine();
                if (ret != null && !ret.trim().equals("")) {
                    str_return = str_return
                        + new String(ret.getBytes("ISO-8859-1"), "GBK");
                }
            }
            
            //SCRIPT_MARK.d("DBG","https conn res="+str_return);
            /*
               pw = ServletActionContext.getResponse().getWriter();
               if(is != null){
               pw.write("success");
               }else{
               pw.write("error");
               }
               */
            conn.disconnect();
        } catch (ConnectException e) {

        }catch (Exception e) {

        }
        finally {
            try {
                if(is != null){
                    is.close(); 
                }
                if(pw != null){
                    pw.close();
                }
            } catch (IOException e) {
            }
            return str_return;
        }
    }




}
