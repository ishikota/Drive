package jp.ikota.drive.ui.imagedetail;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.R;


public class ImageDetailFragment extends Fragment{

    private AndroidApplication mApp;

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
        //mActionsListener = new BaseImageListPresenter(mApp.api(), this, 30);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_imagedetail, container, false);
    }

}
