package jp.ikota.drive.network.oauth;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import jp.ikota.drive.R;

public class OauthSampleActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth_sample);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OauthUtil.showOauthDialog("Sample Action", getSupportFragmentManager());
            }
        });
    }

}
