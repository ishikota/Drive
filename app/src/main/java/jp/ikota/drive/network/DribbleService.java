package jp.ikota.drive.network;


import jp.ikota.drive.data.model.Like;
import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.oauth.OauthRequestParams;
import jp.ikota.drive.network.oauth.OauthResponse;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

public interface DribbleService {

    @GET("/"+DribbleURL.PATH_SHOTS+"?"+DribbleURL.ACCESS_TOKEN)
    void getShots(@Query("page") int page, @Query("per_page") int per_page, Callback<Shots> cb);

    @GET("/"+DribbleURL.PATH_SHOTS+"/{id}"+"?"+DribbleURL.ACCESS_TOKEN)
    void getShot(@Path("id") String id, Callback<Shot> cb);

    @GET("/"+DribbleURL.PATH_USERS+"/{id}/"+DribbleURL.PATH_LIKES+"?"+DribbleURL.ACCESS_TOKEN)
    void getUserLikes(@Query("page") int page, @Query("per_page") int per_page, @Path("id") String id, Callback<Likes> cb);

    @GET("/"+DribbleURL.PATH_SHOTS+"/{id}/"+DribbleURL.PATH_LIKE+"?"+DribbleURL.ACCESS_TOKEN)
    void getIfLikeAShot(@Path("id") String id, Callback<Like> cb);

    @POST("/"+DribbleURL.PATH_SHOTS+"/{id}/"+DribbleURL.PATH_LIKE)
    void likeAShot(@Path("id") String id, @Query("access_token") String access_token, Callback<Response> cb);

    @DELETE("/"+DribbleURL.PATH_SHOTS+"/{id}/"+DribbleURL.PATH_LIKE)
    void unlikeAShot(@Path("id") String id, @Query("access_token") String access_token, Callback<Response> cb);

    @POST("/oauth/token")
    void exchangeAccessToken(@Body OauthRequestParams params, Callback<OauthResponse> cb);
}
