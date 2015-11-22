package jp.ikota.drive.network.oauth;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import jp.ikota.drive.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OauthSampleActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        startActivity(OauthUtil.createOauthAccessIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri uri = intent.getData();
        Log.i("OauthSampleActivity", "onNewIntent called with uri: " + uri);

        String code = uri.getQueryParameter("code");
        if(code!=null) {
            Log.i("OauthSampleActivity", "Received code is : " + code);
            OauthUtil.codeToAccessToken(code, new Callback<OauthResponse>() {
                @Override
                public void success(OauthResponse oauthResponse, Response response) {
                    Log.i("OauthSampleActivity", "get accessToken: "+oauthResponse.access_token);
                    // save this access token to SharedPreferences or somewhere
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e("OauthSampleActivity", error.getMessage());
                    Toast.makeText(OauthSampleActivity.this, "Failed to get AccessToken", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.i("OauthSampleActivity", "Access Denied");
            Toast.makeText(OauthSampleActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
        }
    }

}
