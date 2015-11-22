package jp.ikota.drive.network.oauth;

import android.app.Activity;
import android.os.Bundle;

import jp.ikota.drive.R;

public class OauthSampleActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        startActivity(OauthUtil.createOauthAccessIntent());
    }

}
