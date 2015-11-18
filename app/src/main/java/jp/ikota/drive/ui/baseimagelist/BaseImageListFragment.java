package jp.ikota.drive.ui.baseimagelist;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.List;

import jp.ikota.drive.R;
import jp.ikota.drive.data.model.Shot;


public class BaseImageListFragment extends Fragment implements BaseImageListContract.View{

    private BaseImageListContract.UserActionsListener mActionListener;

    private Context mAppContext;
    private BaseImageListAdapter mAdapter;

    // layout elements
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar mProgressbar;
    View mEmptyView;

    public static BaseImageListFragment newInstance() {
        return new BaseImageListFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAppContext = getActivity().getApplicationContext();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_baseimagelist, container, false);
        mRecyclerView = (RecyclerView) root.findViewById(android.R.id.list);
        mEmptyView = root.findViewById(android.R.id.empty);
        mProgressbar = (ProgressBar) root.findViewById(R.id.progress);
        mSwipeRefreshLayout = (SwipeRefreshLayout) root.findViewById(R.id.swipe_refresh);
        return root;
    }

    @Override
    public void setRefreshIndicator(boolean active) {

    }

    @Override
    public void setProgressIndicator(boolean active) {

    }

    @Override
    public void showShots(List<Shot> shots) {

    }

    @Override
    public void showShotDetail(Shot shot) {

    }

    @Override
    public void showNetworkError() {

    }

    public static class BaseImageListAdapter extends RecyclerView.Adapter<BaseImageListAdapter.ViewHolder> {

        private List<Shot> mShots;
        private ShotClickListener mClickListener;

        public BaseImageListAdapter(List<Shot> shots, ShotClickListener listener) {
            mShots = shots;
            mClickListener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View noteView = inflater.inflate(R.layout.row_baseimagelist, parent, false);
            return new ViewHolder(noteView, mClickListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Shot shot = mShots.get(position);
            holder.imageView.setImageResource(R.mipmap.ic_launcher);
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView imageView;
            public ShotClickListener clickListener;

            public ViewHolder(View itemView, ShotClickListener listener) {
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

    public interface ShotClickListener {

        void onClickShot(Shot clickedShot);
    }

}
