package jp.ikota.drive.network;


import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DribbleService {

    @GET("/"+DribbleURL.PATH_SHOTS+"?"+DribbleURL.ACCESS_TOKEN)
    void getShots(@Query("page") int page, @Query("per_page") int per_page, Callback<Shots> cb);

    @GET("/"+DribbleURL.PATH_SHOTS+"/{id}"+"?"+DribbleURL.ACCESS_TOKEN)
    void getShot(@Path("id") String id, Callback<Shot> cb);

    @GET("/"+DribbleURL.PATH_USERS+"/{id}/"+DribbleURL.PATH_LIKES+"?"+DribbleURL.ACCESS_TOKEN)
    void getUserLikes(@Query("page") int page, @Query("per_page") int per_page, @Path("id") String id, Callback<Likes> cb);
}
