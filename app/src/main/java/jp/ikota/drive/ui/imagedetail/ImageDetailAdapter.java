package jp.ikota.drive.ui.imagedetail;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import jp.ikota.drive.R;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.ui.baseimagelist.BaseImageListFragment;

public class ImageDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int TYPE_HEADER = 0;
    static final int TYPE_ITEM = 1;

    private List<Shot> mShots;
    private BaseImageListFragment.ShotClickListener mClickListener;

    public ImageDetailAdapter(List<Shot> shots, BaseImageListFragment.ShotClickListener listener) {
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
        if(position == 0) {
            HeaderViewHolder vh = (HeaderViewHolder)holder;
            // TODO do something with vh (attach vh with presenter)
        } else {
            RelatedViewHolder vh = (RelatedViewHolder)holder;
            // TODO do something with vh (load image into imageView with Picasso)
        }
    }

    @Override
    public int getItemCount() {
        return mShots.size();
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_main, image_user;
        public TextView text_title, text_username, text_createdat,
                text_like_desc, text_like_num, text_related_title;
        public LinearLayout tag_parent;
        public HeaderViewHolder(View v) {
            super(v);
            image_main = (ImageView)v.findViewById(R.id.image);
            image_user = (ImageView)v.findViewById(R.id.user_icon);
            text_title = (TextView)v.findViewById(R.id.title);
            text_createdat = (TextView)v.findViewById(R.id.created_at);
            text_like_desc = (TextView)v.findViewById(R.id.like_text);
            text_like_num  = (TextView)v.findViewById(R.id.like_num);
            text_username  = (TextView)v.findViewById(R.id.user_name);
            text_related_title = (TextView)v.findViewById(R.id.related_title);
            tag_parent = (LinearLayout)v.findViewById(R.id.tag_parent);
        }
    }

    public class RelatedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageView;
        public BaseImageListFragment.ShotClickListener clickListener;

        public RelatedViewHolder(View itemView, BaseImageListFragment.ShotClickListener listener) {
            super(itemView);
            clickListener = listener;
            imageView = (ImageView) itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Shot shot = mShots.get(position);
            clickListener.onClickShot(shot);
        }
    }

}
