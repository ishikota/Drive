package jp.ikota.drive.ui.imagedetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.BusHolder;
import jp.ikota.drive.R;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.network.oauth.OauthUtil;


public class ImageDetailFragment extends Fragment implements ImageDetailContract.View {

    private ImageDetailContract.UserActionsListener mActionsListener;
    private ImageDetailActivity.OnToolbarAlphaListener mToolbarListener;

    private AndroidApplication mApp;

    // list elements
    ArrayList<Shot> mItemList = new ArrayList<>();
    ImageDetailAdapter mAdapter;

    // layout elements
    Toolbar mToolbar;
    RecyclerView mRecyclerView;
    FloatingActionButton mFab;

    private static final String EXTRA_CONTENT = "content";
    public static ImageDetailFragment newInstance(String json) {
        Bundle args = new Bundle();
        args.putString(EXTRA_CONTENT, json);
        ImageDetailFragment fragment = new ImageDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // TODO refactor here
        Gson gson = new Gson();
        String json = getArguments().getString(EXTRA_CONTENT);
        Shot shot = gson.fromJson(json, Shot.class);
        mApp = (AndroidApplication) getActivity().getApplicationContext();
        mActionsListener = new ImageDetailPresenter(mApp.api(), this, shot, 30);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        BusHolder.get().register(mActionsListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusHolder.get().unregister(mActionsListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_imagedetail, container, false);

        // setup content
        Gson gson = new Gson();
        String json = getArguments().getString(EXTRA_CONTENT);
        Shot shot = gson.fromJson(json, Shot.class);

        if(mItemList.isEmpty()) {
            mItemList.add(shot);
            mActionsListener.loadRelatedShots();  // TODO this method here is correct?
        }

        // list related
        if(mAdapter == null) {
            mAdapter = new ImageDetailAdapter(mApp, mItemList,
                    new ImageDetailAdapter.OnDetailAdapterClickListener() {
                        @Override
                        public void onShotClick(Shot clicked_shot) {
                            Intent intent = ImageDetailActivity.createIntent(mApp, clicked_shot);
                            startActivity(intent);
                        }

                        @Override
                        public void onUserClick(Shot.User clicked_user) {
                            Toast.makeText(mApp, "should go User page",Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onTagClick(String clicked_tag) {
                            Toast.makeText(mApp, "should go Tag page",Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        // TODO change column num by checking device orientation
        // add span size lookup to change column num dynamically
        GridLayoutManager manager = new GridLayoutManager(mApp, 2);
        manager = addSpanSizeLookup(mApp, manager,mAdapter);

        mRecyclerView = (RecyclerView)root.findViewById(android.R.id.list);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // load next related images page
                GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
                int totalItemCount = layoutManager.getItemCount() - 1;  //TODO do not need -1 (used in Flickr client)
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                if (totalItemCount - firstVisibleItem <= 30 && isAdded()) {  // TODO hard coding item page page
                    mActionsListener.loadRelatedShots();
                }

                mActionsListener.fabStateMayChange(firstVisibleItem == 0);
                mActionsListener.updateToolbarAlpha(dy);
            }
        });

        mToolbar = (Toolbar)root.findViewById(R.id.toolbar_actionbar);

        setToolbarAlpha(0); // first make Toolbar invisible
        mFab = (FloatingActionButton)root.findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActionsListener.clickFab();
            }
        });
        mFab.hide();

        return root;
    }

    private GridLayoutManager addSpanSizeLookup(
            Context context, GridLayoutManager original, final RecyclerView.Adapter adapter) {

        GridLayoutManager manager = new GridLayoutManager(context, original.getSpanCount());
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapter.getItemViewType(position)) {
                    case ImageDetailAdapter.TYPE_HEADER:
                        return 2;
                    case ImageDetailAdapter.TYPE_ITEM:
                        return 1;
                    default:
                        return -1;
                }
            }
        });
        return manager;
    }

    @Override
    public void addShots(List<Shot> shots) {
        if(!isAdded()) return;  // This method is called after async task
        if(mItemList.size()==1) {
            mAdapter.notifyRelatedLoadFinish(!shots.isEmpty());  //TODO test it
        }
        mItemList.addAll(shots);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showShotDetail(Shot shot) {
        Intent intent = ImageDetailActivity.createIntent(mApp.getApplicationContext(), shot);
        startActivity(intent);
    }

    //TODO test it
    @Override
    public void showFab(boolean show) {
        if(show) {
            mFab.show();
        } else {
            mFab.hide();
        }
    }

    //TODO test it (how to check imageView's resource)
    @Override
    public void toggleFab(boolean be_like, boolean hide) {
        mFab.setImageResource(be_like ?
                R.drawable.ic_favorite_white_24dp :
                R.drawable.ic_favorite_border_white_24dp);
        if(hide) {
            mFab.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mFab.hide();
                }
            }, 300);
        }
    }

    @Override
    public void setToolbarAlpha(int alpha) {
        mToolbarListener.updateToolbarAlpha(alpha);
    }

    @Override
    public void showLoginDialog() {
        OauthUtil.showOauthDialog("Like a shot", getChildFragmentManager());
    }

    @Override
    public String getAccessToken() {
        if(!isAdded()) return "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return prefs.getString(OauthUtil.KEY_ACCESS_TOKEN, "");
    }

    public void setToolbarListener(ImageDetailActivity.OnToolbarAlphaListener listener) {
        mToolbarListener = listener;
    }
}
