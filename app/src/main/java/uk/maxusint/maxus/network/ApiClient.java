package uk.maxusint.maxus.network;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //private static final String BASE_URL = "http://192.168.0.8/maxusint/public/";
    private static final String BASE_URL = "http://192.168.1.103/maxusint/public/";
    private static ApiClient mInstance;
    private Retrofit retrofit;


    private ApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static synchronized ApiClient getInstance() {
        if (mInstance == null) {
            mInstance = new ApiClient();
        }
        return mInstance;
    }

    public ApiService getApi() {
        return retrofit.create(ApiService.class);
    }
}
