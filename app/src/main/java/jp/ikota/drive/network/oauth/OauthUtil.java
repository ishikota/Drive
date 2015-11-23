package jp.ikota.drive.network.oauth;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import jp.ikota.drive.R;
import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.network.DribbleURL;
import retrofit.Callback;
import retrofit.RestAdapter;

public class OauthUtil {

    // use this key to retrieve access token from SharedPreferences
    public static final String KEY_ACCESS_TOKEN = "access_token";

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

    public static void showOauthDialog(String action, FragmentManager fm) {
        OauthAlertDialogFragment fragment = OauthAlertDialogFragment.newInstance(action);
        fragment.show(fm, "dialog");
    }

    private static Uri buildAuthorizeUri() {
        return Uri.parse(OAUTH_URL).buildUpon()
                .appendQueryParameter("client_id", CLIENT_ID)
                .build();
    }

    public static class OauthAlertDialogFragment extends DialogFragment {

        public static OauthAlertDialogFragment newInstance(String action) {
            OauthAlertDialogFragment fragment = new OauthAlertDialogFragment();
            Bundle args = new Bundle();
            args.putString("action", action);
            fragment.setArguments(args);
            return fragment;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String action = getArguments().getString("action");

            return new AlertDialog.Builder(getActivity())
                    .setTitle("[ "+action+" ] requires login")
                    .setPositiveButton(R.string.login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = createOauthAccessIntent();
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismiss();
                        }
                    }).create();
        }
    }
}
