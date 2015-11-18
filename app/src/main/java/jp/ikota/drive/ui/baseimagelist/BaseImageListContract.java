package jp.ikota.drive.ui.baseimagelist;

import android.support.annotation.NonNull;

import java.util.List;

import jp.ikota.drive.data.model.Shot;

public interface BaseImageListContract {

    interface View {

        void finishRefreshIndicator();

        void setProgressIndicator(boolean active);

        void showShots(List<Shot> shots);

        void showShotDetail(Shot shot);

        void showNetworkError();

    }

    interface UserActionsListener {

        void refreshShots();

        void loadShots();

        void openShotDetails(@NonNull Shot shot);

    }

}
