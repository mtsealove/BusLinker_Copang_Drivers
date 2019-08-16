package mtsealove.com.github.BuslinkerDrivers.Restful;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface RetrofitService {
    //로그인
    @POST("/Login")
    Call<Account> PostLogin(@Body LoginData loginData);
    //운행정보(오늘 이후)
    @GET("/RunInfoNext")
    Call<List<RunInfo>> GetRunInfoNext(@Query("DriverID") String driverID, @Query("key") String key);
    //운행정보(과거)
    @GET("/RunInfoPrev")
    Call<List<RunInfo>> GetRunInfoPrev(@Query("DriverID") String driverID, @Query("key") String key);
    //토큰 등록
    @POST
    Call<String> PostToken(@Body String token);
}
