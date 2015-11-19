package jp.ikota.drive.ui.imagedetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Arrays;

import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.network.DribbleService;


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
    public void toggleLike() {
        mShot.likes_count = is_like_on ? mShot.likes_count-1 : mShot.likes_count+1;
        is_like_on = !is_like_on;
        mDetailView.setLikeNum(mShot.likes_count);
    }

    @Override
    public void openTagScreen(String tag) {
        mDetailView.showTagScreen(tag);
    }

    @Override
    public void openUserScreen(Shot.User user) {
        mDetailView.showUserScreen(user);
    }

    @Override
    public void relatedLoadFinished(boolean success) {
        mDetailView.removeProgress();
    }

}
