package jp.ikota.drive.network.oauth;

import android.content.Intent;
import android.net.Uri;

import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.network.DribbleURL;
import retrofit.Callback;
import retrofit.RestAdapter;

public class OauthUtil {

    private static final String OAUTH_URL = "https://dribbble.com/oauth/authorize";
    private static final String CLIENT_ID = "119011ca89c3d6ed96a16a7521888f48b76aa1950615a17f05c37f48fc21f176";
    private static final String CLIENT_SECRET = "Replace with real client secret"; //TODO Replace this value

    private static final DribbleService OAUTH_SERVICE = new RestAdapter.Builder()
            .setEndpoint(DribbleURL.DRIBBLE_END_POINT)
            .build()
            .create(DribbleService.class);

    public static Intent createOauthAccessIntent(){
        Uri uri = buildAuthorizeUri();
        return new Intent(Intent.ACTION_VIEW,uri);
    }

    public static void codeToAccessToken(String code, Callback<OauthResponse> callback) {
        OauthRequestParams params = new OauthRequestParams(CLIENT_ID, CLIENT_SECRET, code);
        OAUTH_SERVICE.exchangeAccessToken(params, callback);
    }

    private static Uri buildAuthorizeUri() {
        return Uri.parse(OAUTH_URL).buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .build();
    }
}
