package jp.ikota.drive.ui.imagedetail;


import android.support.annotation.NonNull;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;

import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.ApiSubscriber;
import jp.ikota.drive.network.DribbbleRxApi;

public class ImageDetailPresenter implements ImageDetailContract.UserActionsListener {

    private final DribbbleRxApi API;
    private final ImageDetailContract.View mDetailView;
    private final Shot mShot;
    private final int ITEM_PER_PAGE;

    // state variable
    int mPage = 1;  // page count is 1-index
    int toolbar_alpha = 0;
    boolean loading = false;
    boolean fab_is_displayed = false;
    boolean fab_is_on = false;

    public ImageDetailPresenter(
            @NonNull DribbbleRxApi api,
            @NonNull ImageDetailContract.View detailView,
            Shot shot,
            int item_per_page) {
        API = api;
        mDetailView = detailView;
        mShot = shot;
        ITEM_PER_PAGE = item_per_page;
    }

    @Override
    public void fabStateMayChange(boolean show) {
        if(fab_is_displayed != show) {
            fab_is_displayed = show;
            mDetailView.showFab(show);
        }
    }

    @Override
    public void loadRelatedShots() {
        if(loading) return;
        loading = true;
        API.getUserLikes(mPage, ITEM_PER_PAGE, mShot.user.id)
                .subscribe(new ApiSubscriber<Likes>() {

                    @Override
                    public void onNext(Likes likes) {
                        Shots shots = new Shots();
                        shots.items = new ArrayList<>();
                        for (Likes.Like like : likes.items) {
                            shots.items.add(like.shot);
                        }
                        mDetailView.addShots(shots.items);
                        loading = false;
                        if(!shots.items.isEmpty()) mPage++;
                        mDetailView.notifyRelatedLoadFinish(mPage == 1);
                    }

                    @Override
                    public void onError(Throwable error) {
                        error.printStackTrace();
                        mDetailView.showNetworkError();
                        mDetailView.notifyRelatedLoadFinish(mPage==1);
                    }
                });
    }

    @Override
    public void openShotDetails(@NonNull Shot shot) {
        mDetailView.showShotDetail(shot);
    }

    @Override
    public void clickFab() {
        if(!mDetailView.getAccessToken().isEmpty()) {
            fab_is_on = !fab_is_on;
            mDetailView.toggleFab(fab_is_on, true);  // hide fab after click
        } else {
            mDetailView.showLoginDialog();
        }
    }

    @Override
    public void updateToolbarAlpha(int dy) {
        toolbar_alpha += dy;
        toolbar_alpha = Math.max(0, Math.min(200, toolbar_alpha));
        mDetailView.setToolbarAlpha(toolbar_alpha);
    }

    @Subscribe
    public void initFab(LikeAvailableEvent event) {
        fab_is_on = event.status;
        mDetailView.toggleFab(fab_is_on, false);
        // Don't know why but ImageDetailAdapterPresenter.loadLikeState
        // is called twice which invoke this method.
        // So only invoke fab.show() when fab is not displayed
        fabStateMayChange(true);
    }

    // Receive this event when like state has loaded
    public static class LikeAvailableEvent {
        public final boolean status;
        public LikeAvailableEvent(boolean status) {
            this.status = status;
        }
    }

    // helper method for test
    public boolean getIfFabIsOn() {
        return fab_is_on;
    }
}
