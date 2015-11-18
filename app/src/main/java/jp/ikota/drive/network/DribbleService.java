package jp.ikota.drive.network;


import jp.ikota.drive.data.model.Shots;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface DribbleService {

    @GET("/"+DribbleURL.PATH_SHOTS+"?"+DribbleURL.ACCESS_TOKEN)
    void getShots(@Query("page") int page, @Query("per_page") int per_page, Callback<Shots> cb);

}
