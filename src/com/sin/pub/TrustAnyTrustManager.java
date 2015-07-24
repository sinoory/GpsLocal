package com.sin.pub;

import javax.net.ssl.X509TrustManager;

 
class TrustAnyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] arg0,
                String arg1) throws java.security.cert.CertificateException {
        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] arg0,
                String arg1) throws java.security.cert.CertificateException {
        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }


