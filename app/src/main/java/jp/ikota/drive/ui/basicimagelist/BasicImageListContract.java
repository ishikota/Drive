package jp.ikota.drive.ui.basicimagelist;

import android.support.annotation.NonNull;

import java.util.List;

import jp.ikota.drive.data.model.Shot;

public interface BasicImageListContract {

    interface View {

        void finishRefreshIndicator();

        void setProgressIndicator(boolean active);

        void clearShots();

        void addShots(List<Shot> shots);

        void showShotDetail(Shot shot);

        void showNetworkError();

        void showEmptyView(boolean show);
    }

    interface UserActionsListener {

        void refreshShots();

        void loadShots();

        void openShotDetails(@NonNull Shot shot);

        void reachListBottom();

    }

}
