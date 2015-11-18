package jp.ikota.drive.ui.baseimagelist;

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
import android.support.v4.widget.SwipeRefreshLayout;
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
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.di.DummyAPIModule;
import jp.ikota.drive.network.DribbleURL;
import jp.ikota.drive.network.Util;
import jp.ikota.drive.ui.imagedetail.ImageDetailActivity;
import jp.ikota.drive.util.IdlingResource.ListCountIdlingResource;
import jp.ikota.drive.util.IdlingResource.VisibilityIdlingResource;

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
public class BaseImageListScreenTest {

    private Intent mIntent;

    @Rule
    public ActivityTestRule<BaseImageListActivity> activityRule = new ActivityTestRule<>(
            BaseImageListActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False so we can customize the intent per test method

    @Before
    public void setUp(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Context context = instrumentation.getTargetContext();
        mIntent = new Intent(context, BaseImageListActivity.class);
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
        setupMockServer(null);
        BaseImageListActivity activity = activityRule.launchActivity(mIntent);
        BaseImageListFragment fragment = getFragment(activity);
        @SuppressWarnings("ConstantConditions")
        RecyclerView recyclerView = (RecyclerView)fragment.getView().findViewById(android.R.id.list);
        onView(ViewMatchers.withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        IdlingResource idlingResource = new ListCountIdlingResource(recyclerView, 1);
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
        setupMockServer(map);
        BaseImageListActivity activity = activityRule.launchActivity(mIntent);
        BaseImageListFragment fragment = getFragment(activity);
        @SuppressWarnings("ConstantConditions")
        View progressBar = fragment.getView().findViewById(R.id.progress);

        onView(withId(android.R.id.empty)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
        VisibilityIdlingResource idlingResource = new VisibilityIdlingResource(progressBar, View.GONE);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.empty)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        Espresso.unregisterIdlingResources(idlingResource);
    }

    @Test
    public void setProgressIndicator() {
        // TODO calling fragment.setProgressIndicator(false) violates Only-Main-thread-touch-view policy
//        BaseImageListActivity activity = activityRule.launchActivity(mIntent);
//        BaseImageListFragment fragment = getFragment(activity);
//        fragment.setProgressIndicator(false);
//        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
//        fragment.setProgressIndicator(true);
//        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
//        fragment.setProgressIndicator(false);
    }

    @Test
    public void finishRefreshIndicator() {
        // TODO Add custom action to swipe down and invoke swipe refresh layout
//        BaseImageListActivity activity = activityRule.launchActivity(mIntent);
//        BaseImageListFragment fragment = getFragment(activity);
//        onView(withId(R.id.swipe_refresh)).check(matches(withSwipeRefreshState(true)));
//        fragment.finishRefreshIndicator();
//        onView(withId(R.id.swipe_refresh)).check(matches(withSwipeRefreshState(false)));
    }

    @Test
    public void loadFirstItems() {
        activityRule.launchActivity(mIntent);
    }

    @Test
    public void showShotDetail() {
        setupMockServer(null);
        BaseImageListActivity activity = activityRule.launchActivity(mIntent);
        BaseImageListFragment fragment = getFragment(activity);
        @SuppressWarnings("ConstantConditions")
        RecyclerView recyclerView = (RecyclerView)fragment.getView().findViewById(android.R.id.list);

        // Set up an ActivityMonitor
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        Instrumentation.ActivityMonitor receiverActivityMonitor =
                instrumentation.addMonitor(ImageDetailActivity.class.getName(), null, false);

        // wait list load and click first item to go detail screen
        IdlingResource idlingResource = new ListCountIdlingResource(recyclerView, 1);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        Espresso.unregisterIdlingResources(idlingResource);

        Activity detail_activity = receiverActivityMonitor.waitForActivityWithTimeout(1000);
        // Remove the ActivityMonitor
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
        //TODO : how to test if toast is shown
    }

    private BaseImageListFragment getFragment(AppCompatActivity activity) {
        return (BaseImageListFragment)activity.getSupportFragmentManager()
                .findFragmentByTag(BaseImageListFragment.class.getSimpleName());
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

    public static Matcher<View> withSwipeRefreshState(final boolean refreshing) {
        final Matcher<Boolean> matcher = is(refreshing);
        return new BoundedMatcher<View, SwipeRefreshLayout>(SwipeRefreshLayout.class) {

            @Override
            protected boolean matchesSafely(SwipeRefreshLayout swipeRefreshLayout) {
                return matcher.matches(swipeRefreshLayout.isRefreshing());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with swipeRefresh state: ");
                matcher.describeTo(description);
            }
        };
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
