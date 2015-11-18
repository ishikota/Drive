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
        API.getShots(0, ITEM_PER_PAGE, new Callback<Shots>() {
            @Override
            public void success(Shots shots, Response response) {
                mShotsView.showShots(shots.items);
                mPage++;
                mShotsView.finishRefreshIndicator();
                mShotsView.showEmptyView(false);
                mPage = 0;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                mShotsView.showNetworkError();
                mShotsView.finishRefreshIndicator();
                mShotsView.showEmptyView(mPage == 0);
            }
        });
    }

    @Override
    public void loadShots() {
        mShotsView.setProgressIndicator(true);
        API.getShots(mPage, ITEM_PER_PAGE, new Callback<Shots>() {
            @Override
            public void success(Shots shots, Response response) {
                if(shots.items.size() != 0) mPage++;
                mShotsView.showShots(shots.items);
                mShotsView.setProgressIndicator(false);
                mShotsView.showEmptyView(mPage==0);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                mShotsView.showNetworkError();
                mShotsView.setProgressIndicator(false);
                mShotsView.showEmptyView(mPage==0);
            }
        });
    }

    @Override
    public void openShotDetails(@NonNull Shot shot) {
        mShotsView.showShotDetail(shot);
    }

}
