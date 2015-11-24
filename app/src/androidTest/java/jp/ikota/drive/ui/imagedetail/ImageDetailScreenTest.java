package jp.ikota.drive.ui.imagedetail;

import android.annotation.TargetApi;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
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
import jp.ikota.drive.network.oauth.OauthUtil;
import jp.ikota.drive.util.IdlingResource.ListCountIdlingResource;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

@RunWith(AndroidJUnit4.class)
public class ImageDetailScreenTest extends ActivityInstrumentationTestCase2<ImageDetailActivity>{

    private Context mContext;
    private Intent mIntent;
    private Shot mTarget = getSampleShot();

    public ImageDetailScreenTest() {
        super(ImageDetailActivity.class);
    }

    @Rule
    public ActivityTestRule<ImageDetailActivity> activityRule = new ActivityTestRule<>(
            ImageDetailActivity.class,
            true,     // initialTouchMode
            false);   // launchActivity. False so we can customize the intent per test method

    @Before
    public void setUp(){
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        mContext = instrumentation.getTargetContext();
        mIntent = ImageDetailActivity.createIntent(mContext, mTarget);
    }

    @Test
    public void preConditions() {
        activityRule.launchActivity(mIntent);
        onView(withId(R.id.container)).check(matches(withId(R.id.container)));
        onView(withId(R.id.toolbar_actionbar)).check(matches(withId(R.id.toolbar_actionbar)));
        onView(withId(android.R.id.list)).check(matches(withId(android.R.id.list)));
    }

    @Test
    public void setShotInfoIntoHeader() {
        setupMockServer(null);
        activityRule.launchActivity(mIntent);
        onView(withId(R.id.title)).check(matches(withText(mTarget.title)));
        onView(withId(R.id.user_name)).check(matches(withText(mTarget.user.username)));
        onView(withId(R.id.like_num)).check(matches(withText(String.valueOf(mTarget.likes_count) + " likes")));
        onView(withId(R.id.related_username)).check(matches(withText(mTarget.user.username)));
        //onView(withId(R.id.tag_parent)).check(matches(withChildNum(mTarget.tags.length)));
    }

    @Test
    public void showProgressDuringRelatedLoading() {
        setupMockServer(null);
        ImageDetailActivity activity = activityRule.launchActivity(mIntent);
        ImageDetailFragment fragment = getFragment(activity);
        RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(android.R.id.list);
        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
        ListCountIdlingResource idlingResource = new ListCountIdlingResource(recyclerView, 2);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(15, scrollTo()));
        Espresso.unregisterIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, scrollTo()));
        onView(withId(R.id.progress)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
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
    }

    @Test
    public void checkIfLoggedIn() {
        setupMockServer(null);
        ImageDetailActivity activity = activityRule.launchActivity(mIntent);
        ImageDetailFragment fragment = getFragment(activity);
        toggleLoginState(mContext, true);
        assertEquals(fragment.getAccessToken(), "dummy");
        toggleLoginState(mContext, false);
        assertEquals(fragment.getAccessToken(), "");
    }

    @Test
    public void showLoginDialogWhenFabClicked() {
        setupMockServer(null);
        ImageDetailActivity activity = activityRule.launchActivity(mIntent);
        ImageDetailFragment fragment = getFragment(activity);
        toggleLoginState(mContext, true);
        fragment.showLoginDialog();
        SystemClock.sleep(1000);
        onView(withText(R.string.cancel)).perform(click());
        toggleLoginState(mContext, false);
    }

    @Test
    public void checkFabInitialization() {
        setupMockServer(null);
        toggleLoginState(mContext, true);
        activityRule.launchActivity(mIntent);
        onView(withId(R.id.fab)).check(matches(not(isDisplayed())));
        SystemClock.sleep(3000);
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
        toggleLoginState(mContext, false);
    }

    // TODO cannot scroll
    // android.support.test.espresso.PerformException:
    // Error performing 'android.support.test.espresso.contrib.RecyclerViewActions$ActionOnItemAtPositionViewAction@3f1aee29'
    // on view 'with id: android:id/list'.
    //@Test
    public void checkFabBehavior() {
        setupMockServer(null);
        ImageDetailActivity activity = activityRule.launchActivity(mIntent);
        ImageDetailFragment fragment = getFragment(activity);
        RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(android.R.id.list);
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
        ListCountIdlingResource idlingResource = new ListCountIdlingResource(recyclerView, 2);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(8, scrollTo()));
        Espresso.unregisterIdlingResources(idlingResource);
        SystemClock.sleep(3000);
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
    }

    @Test
    public void noTagCase() {
        Shot noTagShot = getSampleShot();
        noTagShot.tags = new String[0];
        Intent intent = ImageDetailActivity.createIntent(mContext, noTagShot);
        activityRule.launchActivity(intent);
        onView(withId(R.id.tag_parent)).check(matches(withChildNum(0)));
        onView(withId(R.id.tag_line)).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)));
    }

    //TODO not implemented this feature because it needs communication between two presenter
    //@Test
    public void clickFab() {
        activityRule.launchActivity(mIntent);
        onView(withId(R.id.like_num)).check(matches(withText("478 likes")));
        onView(withId(R.id.fab)).check(matches(isDisplayed()));
        onView(withId(R.id.fab)).perform(click());
        onView(withId(R.id.like_num)).check(matches(withText("479 likes")));
        SystemClock.sleep(1000);
        onView(withId(R.id.fab)).check(matches(not(isDisplayed())));
        onView(withId(R.id.like_num)).check(matches(withText("478 likes")));
    }

    // TODO RecyclerViewAction's scroll always gives dy=0 ?
    //@Test
    public void checkIfToolbarAlphaChange() {
        ImageDetailActivity activity = activityRule.launchActivity(mIntent);
        ImageDetailFragment fragment = getFragment(activity);
        RecyclerView recyclerView = (RecyclerView) fragment.getView().findViewById(android.R.id.list);
        onView(withId(R.id.toolbar_actionbar)).check(matches(withAlpha(0)));
        ListCountIdlingResource idlingResource = new ListCountIdlingResource(recyclerView, 2);
        Espresso.registerIdlingResources(idlingResource);
        onView(withId(android.R.id.list)).perform(RecyclerViewActions.actionOnItemAtPosition(5, scrollTo()));
        Espresso.unregisterIdlingResources(idlingResource);
        SystemClock.sleep(5000);
        onView(withId(R.id.toolbar_actionbar)).check(matches(not(withAlpha(0))));
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

    // check if child count of target LienarLayout matches to expected one
    public static Matcher<View> withChildNum(int expected_count) {
        final Matcher<Integer> matcher = is(expected_count);
        return new BoundedMatcher<View, LinearLayout>(LinearLayout.class) {

            @Override
            protected boolean matchesSafely(LinearLayout parent) {
                return matcher.matches(parent.getChildCount());
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with childCount: ");
                matcher.describeTo(description);
            }
        };
    }

    private static Matcher<View> withAlpha(final int expected_alpha) {
        final Matcher<Integer> alphaMatcher = is(expected_alpha);
        checkNotNull(alphaMatcher);
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("with alpha: ");
                alphaMatcher.describeTo(description);
            }

            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            protected boolean matchesSafely(View view) {
                return alphaMatcher.matches(view.getBackground().getAlpha());
            }
        };
    }

}
