package jp.ikota.drive.ui.imagedetail;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.R;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.network.oauth.OauthUtil;

public class ImageDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int TYPE_HEADER = 0;
    static final int TYPE_ITEM = 1;

    private final AndroidApplication APP;
    private final DribbleService API;
    private List<Shot> mShots;
    private OnDetailAdapterClickListener mClickListener;

    ImageDetailAdapterPresenter mPresenter;

    public ImageDetailAdapter(AndroidApplication app, List<Shot> shots, OnDetailAdapterClickListener listener) {
        APP = app;
        API = app.api();
        mShots = shots;
        mClickListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View v;
        switch (viewType) {
            case TYPE_HEADER:
                v = inflater.inflate(R.layout.detail_header, parent, false);
                return new HeaderViewHolder(v);
            case TYPE_ITEM:
                v = inflater.inflate(R.layout.row_baseimagelist, parent, false);
                return new RelatedViewHolder(v, mClickListener);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Shot header_shot = mShots.get(position);
        if(position == 0) {
            HeaderViewHolder vh = (HeaderViewHolder)holder;
            //TODO feels like presenter causes memory leak. Need to unbind presenter with ViewHolder
            mPresenter = new ImageDetailAdapterPresenter(API, vh.itemView.getContext(),header_shot, vh);
            // bind view holder to presenter
            mPresenter.setCacheData(header_shot);
            mPresenter.loadLikeState();
            vh.image_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mPresenter.openUserScreen(header_shot.user);
                }
            });
            for(int i=0; i<vh.tag_parent.getChildCount(); i++) {
                View tag = vh.tag_parent.getChildAt(i);
                tag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String tag = ((TextView)view.findViewById(R.id.content)).getText().toString();
                        mPresenter.openTagScreen(tag);
                    }
                });
            }

        } else {
            RelatedViewHolder vh = (RelatedViewHolder)holder;
            Picasso.with(vh.itemView.getContext()).load(header_shot.images.normal).into(vh.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mShots.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder implements ImageDetailAdapterContract.View {
        public ImageView image_main, image_user;
        public TextView text_title, text_username,text_like_num, text_related_title;
        public LinearLayout tag_parent, tag_line;
        public ProgressBar progress;
        public HeaderViewHolder(View v) {
            super(v);
            image_main = (ImageView)v.findViewById(R.id.image);
            image_user = (ImageView)v.findViewById(R.id.user_icon);
            text_title = (TextView)v.findViewById(R.id.title);
            text_like_num  = (TextView)v.findViewById(R.id.like_num);
            text_username  = (TextView)v.findViewById(R.id.user_name);
            text_related_title = (TextView)v.findViewById(R.id.related_username);
            tag_parent = (LinearLayout)v.findViewById(R.id.tag_parent);
            tag_line   = (LinearLayout)v.findViewById(R.id.tag_line);
            progress   = (ProgressBar)v.findViewById(R.id.progress);
        }

        @Override
        public void setImage(Bitmap loaded_image) {
            // adjust view height to loaded image
            int disp_w = getScreenWidth(APP);
            int img_w  = loaded_image.getWidth();
            int img_h  = loaded_image.getHeight();
            if(img_w == 0 || img_h == 0) return;
            image_main.getLayoutParams().width  = disp_w;
            image_main.getLayoutParams().height = (int) (disp_w * (double)img_h/img_w);
            image_main.setImageBitmap(loaded_image);
        }

        @Override
        public void setShotData(String title, Shot.User user) {
            text_title.setText(title);
            Picasso.with(APP).load(user.avatar_url).into(image_user);
            text_username.setText(user.username);
            text_related_title.setText(user.username);
        }

        //TODO test it
        @Override
        public void setLikeNum(int num) {
            String msg = num == 0 ? "no likes yet" : num == 1 ? "1 like" : num+" likes";
            text_like_num.setText(msg);
        }

        @Override
        public void setTags(List<String> tags) {
            // TODO temporary remove tag from UI to simplify app concept
            tag_line.setVisibility(View.GONE);
//            if(tags.isEmpty()) tag_line.setVisibility(View.GONE);
//            for(String tag : tags) {
//                tag_parent.addView(createTagView(APP, tag));
//            }
        }

        @Override
        public void showTagScreen(String tag) {
            mClickListener.onTagClick(tag);
        }

        @Override
        public void showUserScreen(Shot.User user) {
            mClickListener.onUserClick(user);
        }

        @Override
        public void removeProgress() {
            progress.setVisibility(View.GONE);
        }

        @Override
        public boolean checkIfLoggedIn() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(APP);
            return !prefs.getString(OauthUtil.KEY_ACCESS_TOKEN, "").isEmpty();
        }

        private int getScreenWidth(AndroidApplication app) {
            int display_mode = app.getResources().getConfiguration().orientation;
            if(display_mode == Configuration.ORIENTATION_PORTRAIT) {
                return app.SCREEN_WIDTH;
            } else {
                int statusbar_height = (int) app.getResources().getDimension(R.dimen.status_bar_height);
                return app.SCREEN_HEIGHT + statusbar_height;
            }
        }

//        private View createTagView(Context context, String tag) {
//            View view = LayoutInflater.from(context).inflate(R.layout.tag, null);
//            ((TextView)view.findViewById(R.id.content)).setText(tag);
//            return view;
//        }
    }

    public void notifyRelatedLoadFinish(boolean success) {
        mPresenter.relatedLoadFinished(success);
    }

    public class RelatedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public OnDetailAdapterClickListener clickListener;

        public RelatedViewHolder(View itemView, OnDetailAdapterClickListener listener) {
            super(itemView);
            clickListener = listener;
            imageView = (ImageView) itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Shot shot = mShots.get(position);
            clickListener.onShotClick(shot);
        }
    }

    public interface OnDetailAdapterClickListener {
        void onShotClick(Shot clicked_shot);
        void onUserClick(Shot.User clicked_user);
        void onTagClick(String clicked_tag);
    }

}
