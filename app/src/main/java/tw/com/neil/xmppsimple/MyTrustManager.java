package tw.com.neil.xmppsimple;

import android.util.Log;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class MyTrustManager implements X509TrustManager {

    private String TAG = "MyTrustManager";

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException {
        Log.i(TAG, "checkClientTrusted:" + s);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException {
        Log.i(TAG, "checkServerTrusted:" + s);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        Log.i(TAG, "getAcceptedIssuers");
        return new X509Certificate[0];
    }

}
