package jp.ikota.drive.ui.imagedetail;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import dagger.ObjectGraph;
import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.R;
import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.di.DummyAPIModule;
import jp.ikota.drive.network.Util;
import jp.ikota.drive.util.IdlingResource.ListCountIdlingResource;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.Is.is;

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

    @Test
    public void setShotInfoHeader() {
        // TODO : set shot info into header view
    }

    @Test
    public void loadRelatedItems() {
        setupMockServer(null);
        ImageDetailActivity activity = activityRule.launchActivity(mIntent);
        ImageDetailFragment fragment = getFragment(activity);
        RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(android.R.id.list);
        ListCountIdlingResource idlingResource = new ListCountIdlingResource(recyclerView, 2);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(R.id.container)).check(matches(withId(R.id.container)));  // just wait loading
        Espresso.unregisterIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).check(matches(withChildCount(16)));
    }

    private ImageDetailFragment getFragment(AppCompatActivity activity) {
        return (ImageDetailFragment) activity.getSupportFragmentManager().
                findFragmentByTag(ImageDetailFragment.class.getSimpleName());
    }

    private Shot getSampleShot() {
        return new Gson().fromJson(SampleResponse.getShot(), Shot.class);
    }

    public static void setupMockServer(HashMap<String, String> override_map) {
        HashMap<String, String> map = new HashMap<>(Util.RESPONSE_MAP);

        if(override_map!=null) {
            for (String key : override_map.keySet()) {
                map.put(key, override_map.get(key));
            }
        }

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        AndroidApplication app =
                (AndroidApplication) instrumentation.getTargetContext().getApplicationContext();

        // setup objectGraph to inject Mock API
        List modules = Collections.singletonList(new DummyAPIModule(map));
        ObjectGraph graph = ObjectGraph.create(modules.toArray());
        app.setObjectGraph(graph);
        app.getObjectGraph().inject(app);
    }

    /**
     * Check if child count of target RecyclerView matches to expected one
     * @param expected_count expected item count in target RecyclerView
     */
    public static Matcher<View> withChildCount(final int expected_count) {
        final Matcher<Integer> matcher = is(expected_count);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {

            @Override
            protected boolean matchesSafely(RecyclerView recyclerView) {
                Log.i("withChildCount", "item num is " + (recyclerView.getAdapter().getItemCount()));
                return matcher.matches(recyclerView.getAdapter().getItemCount());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with childCount: ");
                matcher.describeTo(description);
            }
        };
    }

}
