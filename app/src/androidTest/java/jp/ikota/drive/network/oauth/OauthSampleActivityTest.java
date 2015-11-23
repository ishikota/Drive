package jp.ikota.drive.network.oauth;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.ikota.drive.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
public class OauthSampleActivityTest {

    private Intent mIntent;

    @Rule
    public ActivityTestRule<OauthSampleActivity> activityRule = new ActivityTestRule<>(
            OauthSampleActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False so we can customize the intent per test method

    @Before
    public void setUp(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context context = instrumentation.getTargetContext();
        mIntent = new Intent(context, OauthSampleActivity.class);
    }

    @Test(expected = NoMatchingViewException.class)
    public void cancelDialogAndDismiss() {
        activityRule.launchActivity(mIntent);
        onView(withId(R.id.button)).perform(click());
        SystemClock.sleep(1000);
        onView(withText(R.string.cancel)).perform(click());
        onView(withText(R.string.cancel)).check(matches(withText(R.string.cancel)));
    }

    //@Test
    public void checkIfOauthIntentCreated() {
        activityRule.launchActivity(mIntent);
        onView(withId(R.id.button)).perform(click());
        SystemClock.sleep(1000);
        onView(withText(R.string.login)).perform(click());
        SystemClock.sleep(1000);
        // TODO check if OauthUtil.createOauthAccessIntent is invoked by Mockito
    }

}
