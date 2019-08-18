package mtsealove.com.github.BuslinkerDrivers.Restful;

import mtsealove.com.github.BuslinkerDrivers.SetIPActivity;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.concurrent.TimeUnit;

public class API {
    //Retrofit 서비스 초기화
    private OkHttpClient httpClient;
    private Retrofit retrofit;
    private RetrofitService retrofitService;

    public API() {
        //통신 연결 클라이언트
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        //데이터를 받아올 API
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + SetIPActivity.IP + ":3400")
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        retrofitService = retrofit.create(RetrofitService.class);
    }

    public RetrofitService getRetrofitService() {
        return retrofitService;
    }
}
