package jp.ikota.drive.ui.imagedetail;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import jp.ikota.drive.R;
import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Shot;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ImageDetailScreenTest {

    private Intent mIntent;
    private Shot mTarget = getSampleShot();

    @Rule
    public ActivityTestRule<ImageDetailActivity> activityRule = new ActivityTestRule<>(
            ImageDetailActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False so we can customize the intent per test method

    @Before
    public void setUp(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context context = instrumentation.getTargetContext();
        mIntent = ImageDetailActivity.createIntent(context, mTarget);
    }

    @Test
    public void preConditions() {
        activityRule.launchActivity(mIntent);
        onView(withId(R.id.container)).check(matches(withId(R.id.container)));
        onView(withId(R.id.toolbar_actionbar)).check(matches(withId(R.id.toolbar_actionbar)));
        onView(withId(android.R.id.list)).check(matches(withId(android.R.id.list)));
    }

    private Shot getSampleShot() {
        return new Gson().fromJson(SampleResponse.getShot(), Shot.class);
    }

}
