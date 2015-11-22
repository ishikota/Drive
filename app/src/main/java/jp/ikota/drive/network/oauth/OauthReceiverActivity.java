package jp.ikota.drive.network.oauth;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import jp.ikota.drive.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class OauthReceiverActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        ((TextView)findViewById(R.id.text)).setText("Logging in ...");
        Log.i("OauthSampleActivity", "onCreate called with " + getIntent().getData());
        Uri uri = getIntent().getData();
        Log.i("OauthSampleActivity", "onNewIntent called with uri: " + uri);

        if (uri.toString().startsWith("oauthdribble")) {
            String code = uri.getQueryParameter("code");
            if (code != null) {
                Log.i("OauthSampleActivity", "Received code is : " + code);
                OauthUtil.codeToAccessToken(code, new Callback<OauthResponse>() {
                    @Override
                    public void success(OauthResponse oauthResponse, Response response) {
                        //Log.i("OauthSampleActivity", "get accessToken: " + oauthResponse.access_token);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(OauthReceiverActivity.this);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString(OauthUtil.KEY_ACCESS_TOKEN, oauthResponse.access_token);
                        editor.apply();
                        Toast.makeText(OauthReceiverActivity.this, "Authentication Success!!", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("OauthSampleActivity", error.getMessage());
                        Toast.makeText(OauthReceiverActivity.this, "Failed to get AccessToken", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
            } else {
                Log.i("OauthSampleActivity", "Access Denied");
                Toast.makeText(OauthReceiverActivity.this, "Access Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
