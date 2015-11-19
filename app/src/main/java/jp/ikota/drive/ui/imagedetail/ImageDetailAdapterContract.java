package jp.ikota.drive.ui.imagedetail;

import android.graphics.Bitmap;

import java.util.List;

import jp.ikota.drive.data.model.Shot;

public interface ImageDetailAdapterContract {

    interface View {

        void setImage(Bitmap loaded_image);

        void setShotData(String title, Shot.User user, String created_at);

        void setLikeState(int num, boolean to_be_like);

        void setTags(List<String> tags);

        void showTagScreen(String tag);

        void showUserScreen(Shot.User user);
    }

    interface UserActionsListener {

        void setCacheData(Shot shot);

        void toggleLike();

        void openTagScreen(String tag);

        void openUserScreen(Shot.User user);
    }

}
