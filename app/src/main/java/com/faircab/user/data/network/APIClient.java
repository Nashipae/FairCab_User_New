package com.faircab.user.data.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.faircab.user.BuildConfig;
import com.faircab.user.MvpApplication;
import com.faircab.user.R;
import com.faircab.user.data.SharedHelper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

//import com.facebook.stetho.okhttp3.StethoInterceptor;

public class APIClient {

    private static Retrofit retrofit = null;
    private static X509TrustManager x509TrustManager;

    public static ApiInterface getAPIClient() {
        if (retrofit == null) {
            try {
                retrofit = new Retrofit
                        .Builder()
                        .baseUrl(BuildConfig.BASE_URL)
                        .client(getHttpClient(MvpApplication.getInstance().context))
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException e) {
                e.printStackTrace();
            }
        }
        return retrofit.create(ApiInterface.class);
    }
    private static SSLContext getSSLConfig(Context context) throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
        try (InputStream cert = context.getResources().openRawResource(R.raw.__6ixtaxi_com)) {
            ca = cf.generateCertificate(cert);
        }

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore   = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);
        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        x509TrustManager= (X509TrustManager) tmf.getTrustManagers()[0];
        sslContext.init(null, tmf.getTrustManagers(), new SecureRandom());

        return sslContext;
    }

    private static OkHttpClient getHttpClient(Context context) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        SSLContext sslContext=getSSLConfig(context);

        return new OkHttpClient().newBuilder()
                //.cache(new Cache(MvpApplication.getInstance().getCacheDir(), 10 * 1024 * 1024)) // 10 MB
                .connectTimeout(10, TimeUnit.SECONDS)
                .sslSocketFactory(sslContext.getSocketFactory(),x509TrustManager )

                .addNetworkInterceptor(new AddHeaderInterceptor())
                .addInterceptor(new DecryptedPayloadInterceptor(new DecryptedPayloadInterceptor.DecryptionStrategy() {
                    @Override
                    public String decrypt(String stream) throws Exception {
                        if (!SharedHelper.apiState.equals("")) {
                            SharedHelper.apiState="";

                            return new String(Base64.decode(stream , Base64.DEFAULT), StandardCharsets.UTF_8);
                        }else {
                            return stream;
                        }
                    }

                    String getString(String str){

                        String replacement = "";
                        String toBeReplaced;
                        if (str.length()>60){
                            toBeReplaced = str.substring(5, 45);

                        }else {
                            toBeReplaced = str.substring(4, 44);

                        }
                        Log.e("sdsdsd",toBeReplaced);

                        Log.e("sdsdsd",str.replace(toBeReplaced, replacement));
                        return str.replace(toBeReplaced, replacement);
                    }
                }))
                //.addNetworkInterceptor(new StethoInterceptor())
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .addInterceptor(interceptor)
                .build();
    }

    private static class AddHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();
            builder.addHeader("X-Requested-With", "XMLHttpRequest");
            builder.addHeader(
                    "Authorization",
                    SharedHelper.getKey(MvpApplication.getInstance(), "access_token", ""));
            Log.d("RRR TOKEN", SharedHelper.getKey(MvpApplication.getInstance(), "access_token", ""));
            return chain.proceed(builder.build());
        }
    }
}
class DecryptedPayloadInterceptor implements Interceptor {

    private final DecryptionStrategy mDecryptionStrategy;

    public interface DecryptionStrategy {
        String decrypt(String stream) throws Exception;
    }

    public DecryptedPayloadInterceptor(DecryptionStrategy mDecryptionStrategy) {
        this.mDecryptionStrategy = mDecryptionStrategy;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
            Response.Builder newResponse = response.newBuilder();
            String contentType = response.header("Content-Type");
            if (TextUtils.isEmpty(contentType)) contentType = "application/json";
            String decrypted = null;
            if (mDecryptionStrategy != null) {
                try {
                    decrypted = mDecryptionStrategy.decrypt(response.body().string());
                    Log.e("sdsd",decrypted);

                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e("sdsd",e.getMessage());
                }
            } else {
                throw new IllegalArgumentException("No decryption strategy!");
            }
            newResponse.body(ResponseBody.create(MediaType.parse(contentType), decrypted));
            return newResponse.build();

    }
}