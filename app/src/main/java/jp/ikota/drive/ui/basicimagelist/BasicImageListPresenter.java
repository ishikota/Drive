package jp.ikota.drive.ui.basicimagelist;


import android.support.annotation.NonNull;

import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbleService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BasicImageListPresenter implements BasicImageListContract.UserActionsListener {

    private final DribbleService API;
    private final BasicImageListContract.View mShotsView;
    private final int ITEM_PER_PAGE;

    // state variable
    private int mPage = 1; // page count is 1-index
    private boolean loading = false;

    public BasicImageListPresenter(
            @NonNull DribbleService api,
            @NonNull BasicImageListContract.View shotsView,
            int item_per_page) {
        API = api;
        mShotsView = shotsView;
        ITEM_PER_PAGE = item_per_page;
    }

    @Override
    public void refreshShots() {
        API.getShots(1, ITEM_PER_PAGE, new Callback<Shots>() {
            @Override
            public void success(Shots shots, Response response) {
                mPage = 1;
                mShotsView.clearShots();
                mShotsView.addShots(shots.items);
                if(!shots.items.isEmpty()) mPage++;
                mShotsView.finishRefreshIndicator();
                mShotsView.showEmptyView(mPage == 1);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                mShotsView.showNetworkError();
                mShotsView.finishRefreshIndicator();
                mShotsView.showEmptyView(mPage == 1);
            }
        });
    }

    @Override
    public void loadShots() {
        if(loading) return;
        loading = true;
        API.getShots(mPage, ITEM_PER_PAGE, new Callback<Shots>() {
            @Override
            public void success(Shots shots, Response response) {
                if(shots.items.size() != 0) mPage++;
                mShotsView.addShots(shots.items);
                mShotsView.setProgressIndicator(false);
                mShotsView.showEmptyView(mPage == 1);
                loading = false;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
                mShotsView.showNetworkError();
                mShotsView.setProgressIndicator(false);
                mShotsView.showEmptyView(mPage == 1);
                loading = false;
            }
        });
    }

    @Override
    public void openShotDetails(@NonNull Shot shot) {
        mShotsView.showShotDetail(shot);
    }

    @Override
    public void reachListBottom() {
        if(loading) mShotsView.setProgressIndicator(true);
    }

}