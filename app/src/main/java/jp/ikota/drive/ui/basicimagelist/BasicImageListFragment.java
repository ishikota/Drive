package jp.ikota.drive.ui.basicimagelist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.R;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.ui.imagedetail.ImageDetailActivity;


public class BasicImageListFragment extends Fragment implements BasicImageListContract.View{

    private BasicImageListContract.UserActionsListener mActionsListener;

    private AndroidApplication mApp;

    // list elements
    ArrayList<Shot> mItemList;
    BaseImageListAdapter mAdapter;

    // layout elements
    RecyclerView mRecyclerView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ProgressBar mProgressbar;
    View mEmptyView;

    public static BasicImageListFragment newInstance() {
        return new BasicImageListFragment();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mApp = (AndroidApplication) getActivity().getApplicationContext();
        mActionsListener = new BasicImageListPresenter(mApp.api(), this, 30);
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

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mApp, 2));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // load next related images page
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount();
                int visibleItemCount = layoutManager.getChildCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (totalItemCount - firstVisibleItem <= 30) {  // TODO hard coding item page page
                    mActionsListener.loadShots();
                }

                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    mActionsListener.reachListBottom();
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.swipe_color_1),
                getResources().getColor(R.color.swipe_color_2),
                getResources().getColor(R.color.swipe_color_3),
                getResources().getColor(R.color.swipe_color_4)
        );

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mActionsListener.refreshShots();
            }
        });

        if (mItemList != null && !mItemList.isEmpty()) {
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mItemList = new ArrayList<>();
            mAdapter = new BaseImageListAdapter(mItemList, new ShotClickListener() {
                @Override
                public void onClickShot(Shot clickedShot) {
                    mActionsListener.openShotDetails(clickedShot);
                }
            });
            mRecyclerView.setAdapter(mAdapter);
            mActionsListener.loadShots();
            mActionsListener.reachListBottom();
        }

        return root;
    }

    @Override
    public void finishRefreshIndicator() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setProgressIndicator(boolean active) {
        mProgressbar.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    @Override
    public void clearShots() {
        mItemList.clear();
        mAdapter.replaceData(mItemList);
    }

    @Override
    public void addShots(List<Shot> shots) {
        mAdapter.replaceData(shots);
    }

    @Override
    public void showShotDetail(Shot shot) {
        Intent intent = ImageDetailActivity.createIntent(mApp.getApplicationContext(), shot);
        startActivity(intent);
    }

    @Override
    public void showNetworkError() {
        Toast.makeText(
                mApp,
                getResources().getString(R.string.network_problem_message),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmptyView(boolean show) {
        mEmptyView.setVisibility(show ? View.VISIBLE : View.GONE);
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
            Picasso.with(holder.imageView.getContext())
                    .load(shot.images.normal)
                    .into(holder.imageView);
            addPadding(holder.itemView, position%2==0);
        }

        @Override
        public int getItemCount() {
            return mShots.size();
        }

        public void replaceData(List<Shot> shots) {
            mShots.addAll(shots);
            notifyDataSetChanged();
        }

        private void addPadding(View v, boolean toRight) {
            int padding = (int)v.getContext().getResources().getDimension(R.dimen.list_row_padding);
            v.setPadding(toRight ? 0 : padding, 0, toRight ? padding : 0, padding * 2);
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
