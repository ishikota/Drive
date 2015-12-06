package jp.ikota.drive.ui.imagedetail;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import jp.ikota.cappuchino.Cappuchino;
import jp.ikota.cappuchino.matcher.custommatcher.CustomMatcher;
import jp.ikota.drive.R;
import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.network.DribbleURL;
import jp.ikota.drive.network.oauth.OauthUtil;
import jp.ikota.drive.util.TestUtil;

import static jp.ikota.cappuchino.matcher.ViewMatcherWrapper.id;
import static jp.ikota.cappuchino.matcher.ViewMatcherWrapper.text;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class ImageDetailScreenTest extends Cappuchino<ImageDetailActivity> {

    private Intent mIntent;
    private Shot mTarget = getSampleShot();

    @Before
    public void setUp(){
        mIntent = ImageDetailActivity.createIntent(getTargetContext(), mTarget);
    }

    @Test
    public void preConditions() {
        launchActivity(mIntent);
        expect(id(R.id.container)).exists();
        expect(id(R.id.toolbar_actionbar)).exists();
        expect(id(android.R.id.list)).exists();
        // Check if passed data is set to layout
        expect(id(R.id.title)).hasText(mTarget.title);
        expect(id(R.id.user_name)).hasText(mTarget.user.name);
        expect(id(R.id.like_num)).hasText(String.valueOf(mTarget.likes_count) + " likes");
        expect(id(R.id.related_username)).hasText(mTarget.user.username);
//        expect(id(R.id.tag_parent)).should(new CustomMatcher.MatcherRule<LinearLayout>() {
//            @Override
//            public boolean matches(LinearLayout linearLayout) {
//                return linearLayout.getChildCount() == mTarget.tags.length;
//            }
//        });
    }

    @Test
    public void showProgressDuringRelatedLoading() {
        TestUtil.setupMockServer(null);
        launchActivity(mIntent);
        expect(id(R.id.progress)).isVisible();
        listIdlingTarget(android.R.id.list).waitUntilItemCountGraterThan(1);
        expect(id(R.id.progress)).isGone();
    }

    @Test
    public void loadRelatedItems() {
        TestUtil.setupMockServer(null);
        launchActivity(mIntent);
        listIdlingTarget(android.R.id.list).waitUntilItemCountGraterThan(1);
    }

    @Test
    public void checkIfLoggedIn() {
        TestUtil.setupMockServer(null);
        launchActivity(mIntent);
        ImageDetailFragment fragment = getFragment(getTargetActivity());
        toggleLoginState(getTargetContext(), true);
        assertEquals(fragment.getAccessToken(), "dummy");
        toggleLoginState(getTargetContext(), false);
        assertEquals(fragment.getAccessToken(), "");
    }

    @Test
    public void showLoginDialogWhenFabClicked() {
        TestUtil.setupMockServer(null);
        launchActivity(mIntent);
        ImageDetailFragment fragment = getFragment(getTargetActivity());
        toggleLoginState(getTargetContext(), true);
        fragment.showLoginDialog();
        coffeeBreak(1000);  // wait until dialog is displayed
        perform(text(R.string.cancel)).clickView();
        toggleLoginState(getTargetContext(), false);
        expect(id(R.id.text)).should(new CustomMatcher.MatcherRule<TextView>() {
            @Override
            public boolean matches(TextView textView) {
                return textView.getText().toString().equals("Cappuchino");
            }
        });
    }

    @Test
    public void checkFabInitialization() {
        TestUtil.setupMockServer(null);
        toggleLoginState(getTargetContext(), true);
        launchActivity(mIntent);
        expect(id(R.id.fab)).not.isDisplayed();
        coffeeBreak(3000);
        expect(id(R.id.fab)).isDisplayed();
        toggleLoginState(getTargetContext(), false);
    }

    @Test
    public void checkFabBehavior() {
        TestUtil.setupMockServer(null);
        launchActivity(mIntent);
        //expect(id(R.id.fab)).isDisplayed();
        listIdlingTarget(android.R.id.list).waitUntilItemCountGraterThan(1);
        perform(id(android.R.id.list)).scrollToPosition(10);
        coffeeBreak(1000);
        expect(id(R.id.fab)).not.isDisplayed();
        perform(id(android.R.id.list)).scrollToPosition(0);
        coffeeBreak(1000);
        expect(id(R.id.fab)).isDisplayed();
    }

    @Test
    public void noTagCase() {
        Shot noTagShot = getSampleShot();
        noTagShot.tags = new String[0];
        Intent intent = ImageDetailActivity.createIntent(getTargetContext(), noTagShot);
        launchActivity(intent);
        expect(id(R.id.tag_parent)).should(new CustomMatcher.MatcherRule<LinearLayout>() {
            @Override
            public boolean matches(LinearLayout linearLayout) {
                return linearLayout.getChildCount() == 0;
            }
        });
        expect(id(R.id.tag_line)).isGone();
    }

    @Test
    public void clickFab() {
        TestUtil.setupMockServer(null);
        toggleLoginState(getTargetContext(), true);
        launchActivity(mIntent);
        coffeeBreak(5000);  // wait initial like state loading
        expect(id(R.id.like_num)).hasText("478 likes");
        expect(id(R.id.fab)).isDisplayed();
        perform(id(R.id.fab)).clickView();
        coffeeBreak(3000);
        expect(id(R.id.like_num)).hasText("477 likes");
        toggleLoginState(getTargetContext(), false);
    }

    @Test
    public void noLikesCase() {
        Shot no_likes_shot = getSampleShot();
        no_likes_shot.likes_count = 0;
        Intent intent = ImageDetailActivity.createIntent(getTargetContext(), no_likes_shot);
        launchActivity(intent);
        expect(id(R.id.like_num)).hasText("no likes yet");
    }

    @Test
    public void noRelatedImagesCase() {
        HashMap<String, String> map = new HashMap<>();
        String empty_response = "{\"items\":[]}";
        map.put(DribbleURL.PATH_USERS + "/", empty_response);
        TestUtil.setupMockServer(map);
        launchActivity(mIntent);
        viewIdlingTarget(R.id.progress).waitUntilViewIsGone();
        expect(text(R.string.no_data)).isVisible();
    }

    @Test
    public void checkIfToolbarAlphaChange() {
        launchActivity(mIntent);
        listIdlingTarget(android.R.id.list).waitUntilItemCountGraterThan(1);
        perform(id(android.R.id.list)).swipeUp();
        expect(id(R.id.toolbar_actionbar)).should(new CustomMatcher.MatcherRule() {
            @Override
            public boolean matches(View view) {
                return view.getBackground().getAlpha() != 0;
            }
        });
    }


    private ImageDetailFragment getFragment(AppCompatActivity activity) {
        return (ImageDetailFragment) activity.getSupportFragmentManager().
                findFragmentByTag(ImageDetailFragment.class.getSimpleName());
    }

    private Shot getSampleShot() {
        return new Gson().fromJson(SampleResponse.getShot(), Shot.class);
    }

    private void toggleLoginState(Context context, boolean be_login) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(OauthUtil.KEY_ACCESS_TOKEN, be_login ? "dummy" : "");
        editor.apply();
    }

}
