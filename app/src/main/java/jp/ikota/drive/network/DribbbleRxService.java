package jp.ikota.drive.network;


import jp.ikota.drive.data.model.Like;
import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.oauth.OauthRequestParams;
import jp.ikota.drive.network.oauth.OauthResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface DribbbleRxService {

    @GET("/"+DribbleURL.PATH_SHOTS+"?"+DribbleURL.ACCESS_TOKEN)
    Observable<Shots> getShots(@Query("page") int page, @Query("per_page") int per_page);

    @GET("/"+DribbleURL.PATH_SHOTS+"/{id}"+"?"+DribbleURL.ACCESS_TOKEN)
    Observable<Shot> getShot(@Path("id") String id);

    @GET("/"+DribbleURL.PATH_USERS+"/{id}/"+DribbleURL.PATH_LIKES+"?"+DribbleURL.ACCESS_TOKEN)
    Observable<Likes> getUserLikes(@Query("page") int page, @Query("per_page") int per_page, @Path("id") String id);

    @GET("/"+DribbleURL.PATH_SHOTS+"/{id}/"+DribbleURL.PATH_LIKE+"?"+DribbleURL.ACCESS_TOKEN)
    Observable<Like> getIfLikeAShot(@Path("id") String id);

    @POST("/"+DribbleURL.PATH_SHOTS+"/{id}/"+DribbleURL.PATH_LIKE)
    Observable<Response> likeAShot(@Path("id") String id, @Query("access_token") String access_token);

    @DELETE("/"+DribbleURL.PATH_SHOTS+"/{id}/"+DribbleURL.PATH_LIKE)
    Observable<Response> unlikeAShot(@Path("id") String id, @Query("access_token") String access_token);

    @POST("/oauth/token")
    Observable<OauthResponse> exchangeAccessToken(@Body OauthRequestParams params);
}
