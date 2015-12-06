package jp.ikota.drive.network.oauth;

import android.support.test.espresso.NoMatchingViewException;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import jp.ikota.cappuchino.Cappuchino;
import jp.ikota.drive.R;

import static jp.ikota.cappuchino.matcher.ViewMatcherWrapper.id;
import static jp.ikota.cappuchino.matcher.ViewMatcherWrapper.text;


@RunWith(AndroidJUnit4.class)
public class OauthSampleActivityTest extends Cappuchino<OauthSampleActivity>{

    @Test(expected = NoMatchingViewException.class)
    public void cancelDialogAndDismiss() {
        launchActivity(null);
        perform(id(R.id.button)).clickView();
        coffeeBreak(1000);
        perform(text(R.string.cancel)).clickView();
        expect(text(R.string.cancel)).exists();  // TODO doesNotExists();
    }

    @Test
    public void checkIfOauthIntentCreated() {
        launchActivity(null);
        perform(id(R.id.button)).clickView();
        coffeeBreak(1000);
        perform(text(R.string.login)).clickView();
        coffeeBreak(1000);
        // TODO check if OauthUtil.createOauthAccessIntent is invoked by Mockito
    }

}
