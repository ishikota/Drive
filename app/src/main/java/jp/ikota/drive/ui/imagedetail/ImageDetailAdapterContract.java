package jp.ikota.drive.ui.imagedetail;

import android.graphics.Bitmap;

import java.util.List;

import jp.ikota.drive.data.model.Shot;

public interface ImageDetailAdapterContract {

    interface View {

        void setImage(Bitmap loaded_image);

        void setShotData(String title, Shot.User user);

        void setLikeNum(int num);

        void setTags(List<String> tags);

        void showTagScreen(String tag);

        void showUserScreen(Shot.User user);

        void removeProgress();

        String getAccessToken();
    }

    interface UserActionsListener {

        void setCacheData(Shot shot);

        void loadLikeState();

        void toggleLike();

        void openTagScreen(String tag);

        void openUserScreen(Shot.User user);

        void relatedLoadFinished(boolean success);
    }

}
