package jp.ikota.drive.ui.imagedetail;


import android.support.annotation.NonNull;

import java.util.List;

import jp.ikota.drive.data.model.Shot;

public interface ImageDetailContract {

    interface View {

        void addShots(List<Shot> shots);

        void showShotDetail(Shot shot);

        void showFab(boolean show);

        void toggleFab(boolean be_like);

        void setToolbarAlpha(int alpha);

        boolean checkIfLoggedIn();

        void showLoginDialog();

    }

    interface UserActionsListener {

        void fabStateMayChange(boolean show);

        void loadRelatedShots();

        void openShotDetails(@NonNull Shot shot);

        void clickFab();

        void updateToolbarAlpha(int dy);

    }

}
