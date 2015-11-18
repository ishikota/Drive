package jp.ikota.drive.ui.imagedetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;

import jp.ikota.drive.BaseActivity;
import jp.ikota.drive.R;
import jp.ikota.drive.data.model.Shot;


public class ImageDetailActivity extends BaseActivity{

    private static final Gson GSON = new Gson();
    private static final String EXTRA_CONTENT = "content";
    public static Intent createIntent(Context context, Shot shot) {
        String json = GSON.toJson(shot);
        Intent intent = new Intent(context, ImageDetailActivity.class);
        intent.putExtra(EXTRA_CONTENT, json);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagedetail);

        if (savedInstanceState == null) {
            String json = getIntent().getStringExtra(EXTRA_CONTENT);
            String tag  = ImageDetailFragment.class.getSimpleName();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ImageDetailFragment.newInstance(json), tag)
                    .commit();
        }
    }

}
