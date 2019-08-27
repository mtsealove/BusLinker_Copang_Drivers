package mtsealove.com.github.BuslinkerDrivers.Restful;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface RetrofitService {
    //로그인
    @POST("/Driver/Login")
    Call<Account> PostLogin(@Body LoginData loginData);
    //운행정보(오늘 이후)
    @GET("/Driver/RunInfoNext")
    Call<List<RunInfo>> GetRunInfoNext(@Query("DriverID") String driverID, @Query("key") String key);
    //운행정보(과거)
    @GET("/Driver/RunInfoPrev")
    Call<List<RunInfo>> GetRunInfoPrev(@Query("DriverID") String driverID, @Query("key") String key);
    //최근 배송건수 확인
    @GET("/Driver/RecentRunCnt")
    Call<Count> GetRecentRunCnt(@Query("DriverID") String driverID);
    //인증번호 발송 요청
    @POST("/Driver/VerifyPhone")
    Call<Verify> PostPhone(@Body PhoneNumber phoneNumber);
    //계정정보 업데이트
    @POST("/Driver/Update/Account")
    Call<PostResult> UpdateAccount(@Body AccountUpdate accountUpdate);
}
