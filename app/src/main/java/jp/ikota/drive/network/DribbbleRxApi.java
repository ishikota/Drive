package jp.ikota.drive.network;

import android.support.annotation.NonNull;

import jp.ikota.drive.data.model.Like;
import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.oauth.OauthRequestParams;
import jp.ikota.drive.network.oauth.OauthResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DribbbleRxApi implements DribbbleRxService {

    private final DribbbleRxService service;

    public DribbbleRxApi(@NonNull DribbbleRxService service) {
        this.service = service;
    }

    @Override
    public Observable<Shots> getShots(@Query("page") int page, @Query("per_page") int per_page) {
        return useDefaultScheduler(service.getShots(page, per_page));
    }

    @Override
    public Observable<Shot> getShot(@Path("id") String id) {
        return useDefaultScheduler(service.getShot(id));
    }

    @Override
    public Observable<Likes> getUserLikes(@Query("page") int page, @Query("per_page") int per_page, @Path("id") String id) {
        return useDefaultScheduler(service.getUserLikes(page, per_page, id));
    }

    @Override
    public Observable<Like> getIfLikeAShot(@Path("id") String id) {
        return useDefaultScheduler(service.getIfLikeAShot(id));
    }

    @Override
    public Observable<Response> likeAShot(@Path("id") String id, @Query("access_token") String access_token) {
        return useDefaultScheduler(service.likeAShot(id, access_token));
    }

    @Override
    public Observable<Response> unlikeAShot(@Path("id") String id, @Query("access_token") String access_token) {
        return useDefaultScheduler(service.unlikeAShot(id, access_token));
    }

    @Override
    public Observable<OauthResponse> exchangeAccessToken(@Body OauthRequestParams params) {
        return useDefaultScheduler(service.exchangeAccessToken(params));
    }

    private <T>Observable<T> useDefaultScheduler(Observable<T> observable) {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
