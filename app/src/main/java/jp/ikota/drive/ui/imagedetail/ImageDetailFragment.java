package jp.ikota.drive.ui.imagedetail;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.R;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.ui.baseimagelist.BaseImageListFragment;


public class ImageDetailFragment extends Fragment implements ImageDetailContract.View {

    private ImageDetailContract.UserActionsListener mActionsListener;

    private AndroidApplication mApp;

    // list elements
    ArrayList<Shot> mItemList = new ArrayList<>();
    ImageDetailAdapter mAdapter;

    // layout elements
    RecyclerView mRecyclerView;

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
        mApp = (AndroidApplication) getActivity().getApplicationContext();
        mActionsListener = new ImageDetailPresenter(mApp.api(), this, 30);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
            mAdapter = new ImageDetailAdapter(mItemList,
                    new BaseImageListFragment.ShotClickListener() {
                        @Override
                        public void onClickShot(Shot clickedShot) {
                            mActionsListener.openShotDetails(clickedShot);
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
        mItemList.addAll(shots);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showShotDetail(Shot shot) {
        Intent intent = ImageDetailActivity.createIntent(mApp.getApplicationContext(), shot);
        startActivity(intent);
    }
}
