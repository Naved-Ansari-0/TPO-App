package in.navedansari.tpoapp.utils;

import java.util.List;
import in.navedansari.tpoapp.models.LoginRequest;
import in.navedansari.tpoapp.models.LoginResponse;
import in.navedansari.tpoapp.models.PlacementRecord;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @GET("data")
    Call<List<PlacementRecord>> getData(
            @Query("email") String email,
            @Query("token") String token,
            @Query("year") String year,
            @Query("type") String type,
            @Query("rollNo") String rollNo,
            @Query("name") String name,
            @Query("gender") String gender,
            @Query("course") String course,
            @Query("branch") String branch,
            @Query("company") String company,
            @Query("drive") String drive,
            @Query("minctc") String minctc,
            @Query("maxctc") String maxctc
    );
}
