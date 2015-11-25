package jp.ikota.drive.ui.imagedetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;

import jp.ikota.drive.BusHolder;
import jp.ikota.drive.data.model.Like;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.network.DribbleService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class ImageDetailAdapterPresenter implements ImageDetailAdapterContract.UserActionsListener {

    private final DribbleService API;
    private final Context mContext;
    private final ImageDetailAdapterContract.View mDetailView;
    private final Shot mShot;

    // state variable
    boolean is_like_on = false;

    public ImageDetailAdapterPresenter( @NonNull DribbleService api, @NonNull Context context,
            @NonNull Shot shot, @NonNull ImageDetailAdapterContract.View detailView) {
        API = api;
        mContext = context;
        mShot = shot;
        mDetailView = detailView;
    }

    @Override
    public void setCacheData(Shot shot) {
        // TODO how to mock here
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bmp, Picasso.LoadedFrom from) {
                mDetailView.setImage(bmp);
            }

            @Override public void onBitmapFailed(Drawable errorDrawable) {}

            @Override public void onPrepareLoad(Drawable placeHolderDrawable) {}
        };
        Picasso.with(mContext).load(shot.images.normal).into(target);
        mDetailView.setShotData(shot.title, shot.user);
        mDetailView.setLikeNum(shot.likes_count);
        mDetailView.setTags(new ArrayList<>(Arrays.asList(shot.tags)));
    }

    @Override
    public void loadLikeState() {
        // TODO cannot write test. How to verify if BusHolder.get().post called with specified event
        if(!mDetailView.getAccessToken().isEmpty()) {
            API.getIfLikeAShot(mShot.id, new Callback<Like>() {
                @Override
                public void success(Like like, Response response) {
                    is_like_on = true;
                    BusHolder.get().post(new ImageDetailPresenter.LikeAvailableEvent(true));
                }

                @Override
                public void failure(RetrofitError error) {
                    is_like_on = false;
                    BusHolder.get().post(new ImageDetailPresenter.LikeAvailableEvent(false));
                }
            });
        } else {
            BusHolder.get().post(new ImageDetailPresenter.LikeAvailableEvent(false));
        }
    }

    @Override
    public void toggleLike() {
        // Not logged in user also can click fab (it displays login dialog)
        String access_token = mDetailView.getAccessToken();
        if(!access_token.isEmpty()) {
            if(is_like_on) {
                API.unlikeAShot(mShot.id, access_token, createEmptyCallback("UnlikeAShot"));
            } else {
                API.likeAShot(mShot.id, access_token, createEmptyCallback("LikeAShot"));
            }
            mShot.likes_count = is_like_on ? mShot.likes_count - 1 : mShot.likes_count + 1;
            is_like_on = !is_like_on;
            mDetailView.setLikeNum(mShot.likes_count);
        }
    }

    @Override
    public void openTagScreen(String tag) {
        mDetailView.showTagScreen(tag);
    }

    @Override
    public void openUserScreen(Shot.User user) {
        mDetailView.showUserScreen(user);
    }

    // TODO show empty view when success == false (it means load finished but no related item is found)
    @Override
    public void relatedLoadFinished(boolean success) {
        mDetailView.removeProgress();
    }

    private Callback<Response> createEmptyCallback(final String log_tag) {
        return new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i(log_tag, response.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        };
    }

}
