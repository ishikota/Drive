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

    public ImageDetailPresenter(
            @NonNull DribbleService api,
            @NonNull ImageDetailContract.View detailView,
            int item_per_page) {
        API = api;
        mDetailView = detailView;
        ITEM_PER_PAGE = item_per_page;
    }

    @Override
    public void loadRelatedShots() {
        API.getShots(mPage, ITEM_PER_PAGE, new Callback<Shots>() {
            @Override
            public void success(Shots shots, Response response) {
                mDetailView.addShots(shots.items);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void openShotDetails(@NonNull Shot shot) {
        mDetailView.showShotDetail(shot);
    }
}
