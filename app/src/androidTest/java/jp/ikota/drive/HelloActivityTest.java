package jp.ikota.drive;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class HelloActivityTest extends ActivityInstrumentationTestCase2<HelloActivity> {

    Context mContext;
    Intent mIntent;

    public HelloActivityTest() {
        super(HelloActivity.class);
    }

    @Rule
    public ActivityTestRule<HelloActivity> activityRule = new ActivityTestRule<>(
            HelloActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False so we can customize the intent per test method

    @Before
    public void setup() throws Exception {
        super.setUp();
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        mContext = instrumentation.getTargetContext();
        injectInstrumentation(instrumentation);
    }

    @Test
    public void helloEspresso() {
        activityRule.launchActivity(mIntent);
        onView(withId(R.id.text)).check(matches(withText(R.string.hello_world)));
    }
}
