package jp.ikota.drive.ui.basicimagelist;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
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

import java.util.HashMap;

import jp.ikota.drive.R;
import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbleURL;
import jp.ikota.drive.ui.imagedetail.ImageDetailActivity;
import jp.ikota.drive.util.IdlingResource.ListCountIdlingResource;
import jp.ikota.drive.util.IdlingResource.VisibilityIdlingResource;
import jp.ikota.drive.util.TestUtil;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class BasicImageListScreenTest {

    private Intent mIntent;

    @Rule
    public ActivityTestRule<BasicImageListActivity> activityRule = new ActivityTestRule<>(
            BasicImageListActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False so we can customize the intent per test method

    @Before
    public void setUp(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context context = instrumentation.getTargetContext();
        mIntent = new Intent(context, BasicImageListActivity.class);
    }

    @Test
    public void preConditions() {
        activityRule.launchActivity(mIntent);
        onView(withId(R.id.container)).check(matches(withId(R.id.container)));
        onView(withId(android.R.id.list)).check(matches(withId(android.R.id.list)));
        onView(withId(android.R.id.empty)).check(matches(withId(android.R.id.empty)));
        onView(withId(R.id.progress)).check(matches(withId(R.id.progress)));
        onView(withId(R.id.swipe_refresh)).check(matches(withId(R.id.swipe_refresh)));
    }

    @Test
    public void checkIfProgressShownAtFirst() {
        TestUtil.setupMockServer(null);
        BasicImageListActivity activity = activityRule.launchActivity(mIntent);
        onView(ViewMatchers.withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        IdlingResource idlingResource = new ListCountIdlingResource(getList(getFragment(activity)), 1);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, scrollTo()));
        Espresso.unregisterIdlingResources(idlingResource);
        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    @Test
    public void testEmptyView_show() {
        HashMap<String, String> map = new HashMap<>();
        String empty_response = "{\"items\":[]}";
        map.put(DribbleURL.PATH_SHOTS, empty_response);
        TestUtil.setupMockServer(map);
        BasicImageListActivity activity = activityRule.launchActivity(mIntent);
        //noinspection ConstantConditions
        View progressBar = getFragment(activity).getView().findViewById(R.id.progress);

        onView(withId(android.R.id.empty)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        VisibilityIdlingResource idlingResource = new VisibilityIdlingResource(progressBar, View.GONE);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.empty)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void loadItems() {
        TestUtil.setupMockServer(null);
        BasicImageListActivity activity = activityRule.launchActivity(mIntent);
        BasicImageListFragment fragment = getFragment(activity);
        @SuppressWarnings("ConstantConditions")
        RecyclerView recyclerView = (RecyclerView)fragment.getView().findViewById(android.R.id.list);

        IdlingResource idlingResource = new ListCountIdlingResource(recyclerView, 1);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.scrollToPosition(25));
        Espresso.unregisterIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).check(matches(withListItemCount(30)));

        IdlingResource idlingResource2 = new ListCountIdlingResource(recyclerView, 31);
        Espresso.registerIdlingResources(idlingResource2);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(10, scrollTo()));
        Espresso.unregisterIdlingResources(idlingResource2);
        onView(withId(android.R.id.list)).check(matches(withListItemCount(45)));
    }

    @Test
    public void showShotDetail() {
        TestUtil.setupMockServer(null);
        BasicImageListActivity activity = activityRule.launchActivity(mIntent);

        // Set up an ActivityMonitor
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor =
                instrumentation.addMonitor(ImageDetailActivity.class.getName(), null, false);

        // wait list load and click first item to go detail screen
        IdlingResource idlingResource = new ListCountIdlingResource(getList(getFragment(activity)), 1);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Espresso.unregisterIdlingResources(idlingResource);

        Activity detail_activity = receiverActivityMonitor.waitForActivityWithTimeout(1000);
        instrumentation.removeMonitor(receiverActivityMonitor);
        assertNotNull("ImageDetailActivity is null", detail_activity);
        assertEquals("Launched Activity is not ImageDetailActivity", ImageDetailActivity.class, detail_activity.getClass());

        // assert if clicked shot passed to detail activity
        Gson gson = new Gson();
        Shots shots = gson.fromJson("{\"items\":"+SampleResponse.getShots()+"}", Shots.class);
        String expected = gson.toJson(shots.items.get(0));
        Intent intent = detail_activity.getIntent();
        String json = intent.getStringExtra("content");
        assertEquals(expected, json);
    }

    @Test
    public void showNetworkError() {
        //TODO how to test if toast is shown
        //TODO how to simulate offline state
    }

    private BasicImageListFragment getFragment(AppCompatActivity activity) {
        return (BasicImageListFragment)activity.getSupportFragmentManager()
                .findFragmentByTag(BasicImageListFragment.class.getSimpleName());
    }

    @SuppressWarnings("ConstantConditions")
    private RecyclerView getList(Fragment fragment) {
        return (RecyclerView)fragment.getView().findViewById(android.R.id.list);
    }

    /**
     * Check if child count of target RecyclerView matches to expected one
     * @param expected_count expected item count in target RecyclerView
     */
    public static Matcher<View> withListItemCount(final int expected_count) {
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
