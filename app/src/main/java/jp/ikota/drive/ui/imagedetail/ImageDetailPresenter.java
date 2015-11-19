package jp.ikota.drive.ui.imagedetail;


import android.support.annotation.NonNull;

import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbleService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ImageDetailPresenter implements ImageDetailContract.UserActionsListener {

    private final DribbleService API;
    private final ImageDetailContract.View mDetailView;
    private final int ITEM_PER_PAGE;

    // state variable
    int mPage = 0;
    int toolbar_alpha = 0;
    boolean loading = false;
    boolean fab_is_displayed = true;
    boolean fab_is_on = false;

    public ImageDetailPresenter(
            @NonNull DribbleService api,
            @NonNull ImageDetailContract.View detailView,
            int item_per_page) {
        API = api;
        mDetailView = detailView;
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
        API.getShots(mPage, ITEM_PER_PAGE,  new Callback<Shots>() {
            @Override
            public void success(Shots shots, Response response) {
                mDetailView.addShots(shots.items);
                loading = false;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                loading = false;
            }
        });
    }

    @Override
    public void openShotDetails(@NonNull Shot shot) {
        mDetailView.showShotDetail(shot);
    }

    @Override
    public void clickFab() {
        fab_is_on = !fab_is_on;
        mDetailView.toggleFab(fab_is_on);
    }

    @Override
    public void updateToolbarAlpha(int dy) {
        toolbar_alpha += dy;
        toolbar_alpha = Math.max(0, Math.min(200, toolbar_alpha));
        mDetailView.setToolbarAlpha(toolbar_alpha);
    }
}
