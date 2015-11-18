package jp.ikota.drive.ui.baseimagelist;


import android.support.annotation.NonNull;

import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbleService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BaseImageListPresenter implements BaseImageListContract.UserActionsListener {

    private final DribbleService API;
    private final BaseImageListContract.View mShotsView;
    private final int ITEM_PER_PAGE;

    // state variable
    private int mPage;

    public BaseImageListPresenter(
            @NonNull DribbleService api,
            @NonNull BaseImageListContract.View shotsView,
            int item_per_page) {
        API = api;
        mShotsView = shotsView;
        ITEM_PER_PAGE = item_per_page;
    }

    @Override
    public void refreshShots() {
        mShotsView.setRefreshIndicator(true);
        mPage = 0;
        API.getShots(mPage, ITEM_PER_PAGE, new Callback<Shots>() {
            @Override
            public void success(Shots shots, Response response) {
                mShotsView.showShots(shots.items);
                mPage++;
                mShotsView.setRefreshIndicator(false);
            }

            @Override
            public void failure(RetrofitError error) {
                mShotsView.showNetworkError();
                mShotsView.setRefreshIndicator(false);
            }
        });
    }

    @Override
    public void loadShots() {
        mShotsView.setProgressIndicator(true);
        API.getShots(mPage, ITEM_PER_PAGE, new Callback<Shots>() {
            @Override
            public void success(Shots shots, Response response) {
                mShotsView.showShots(shots.items);
                mPage++;
                mShotsView.setProgressIndicator(false);
            }

            @Override
            public void failure(RetrofitError error) {
                mShotsView.showNetworkError();
                mShotsView.setProgressIndicator(false);
            }
        });
    }

    @Override
    public void openShotDetails(@NonNull Shot shot) {
        mShotsView.showShotDetail(shot);
    }

}
