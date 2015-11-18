package jp.ikota.drive.ui.baseimagelist;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import jp.ikota.drive.BaseActivity;
import jp.ikota.drive.R;


public class BaseImageListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        if (null == savedInstanceState) {
            initFragment(BaseImageListFragment.newInstance());
        }

    }

    private void initFragment(Fragment notesFragment) {
        String tag = BaseImageListFragment.class.getSimpleName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, notesFragment, tag);
        transaction.commit();
    }

}
