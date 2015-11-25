package jp.ikota.drive.ui.basicimagelist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import jp.ikota.drive.BaseActivity;
import jp.ikota.drive.R;


public class BasicImageListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (null == savedInstanceState) {
            initFragment(BasicImageListFragment.newInstance());
        }

    }

    private void initFragment(Fragment notesFragment) {
        String tag = BasicImageListFragment.class.getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, notesFragment, tag);
        transaction.commit();
    }

}
