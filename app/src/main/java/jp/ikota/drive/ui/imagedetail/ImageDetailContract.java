package jp.ikota.drive.ui.imagedetail;


import android.support.annotation.NonNull;

import java.util.List;

import jp.ikota.drive.data.model.Shot;

public interface ImageDetailContract {

    public interface View {

        void addShots(List<Shot> shots);

        void showShotDetail(Shot shot);

    }

    public interface UserActionsListener {

        void loadRelatedShots();

        void openShotDetails(@NonNull Shot shot);

    }

}
