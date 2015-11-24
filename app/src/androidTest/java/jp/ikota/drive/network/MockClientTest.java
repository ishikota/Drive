package jp.ikota.drive.network;


import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dagger.ObjectGraph;
import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.HelloActivity;
import jp.ikota.drive.data.model.Like;
import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.di.DummyAPIModule;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@RunWith(AndroidJUnit4.class)
public class MockClientTest extends ActivityInstrumentationTestCase2<HelloActivity>{

    public MockClientTest() {
        super(HelloActivity.class);
    }

    AndroidApplication app;
    private CountDownLatch lock;

    @Rule
    public ActivityTestRule<HelloActivity> activityRule = new ActivityTestRule<>(
            HelloActivity.class,
            true,     // initialTouchMode
            true);   // launchActivity. False so we can customize the intent per test method

    @Before
    public void setUp() throws Exception {
        super.setUp();
        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        injectInstrumentation(instrumentation);
        app = (AndroidApplication) instrumentation.getTargetContext().getApplicationContext();
        // setup objectGraph to inject Mock API
        List modules = Collections.singletonList(new DummyAPIModule(Util.RESPONSE_MAP));
        ObjectGraph graph = ObjectGraph.create(modules.toArray());
        app.setObjectGraph(graph);
        app.getObjectGraph().inject(app);
        lock = new CountDownLatch(1);
    }

    @Test
    public void getShots() throws Exception {
        app.api().getShots(0, 20, new Callback<Shots>() {
            @Override
            public void success(Shots shots, Response response) {
                assertEquals(shots.items.size(), 15);
                Shot item = shots.items.get(0);
                assertEquals(item.id, "2357773");
                assertEquals(item.title, "Sequoia");
                assertEquals(item.images.hidpi, "https://d13yacurqjgara.cloudfront.net/users/31752/screenshots/2357773/sequoia.png");
                assertEquals(item.user.id, "31752");
                assertEquals(item.user.name, "Nick Slater");
                lock.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("Error on getShots API: message=" + error.getMessage());
            }
        });
        lock.await(10000, TimeUnit.MILLISECONDS);
        assertEquals(0, lock.getCount());
    }

    @Test
    public void getShot() throws Exception {
        app.api().getShot("dummy", new Callback<Shot>() {
            @Override
            public void success(Shot shot, Response response) {
                assertEquals(shot.id, "1109319");
                assertEquals(shot.title, "Caffeinated App Icon");
                assertEquals(shot.images.hidpi, "https://d13yacurqjgara.cloudfront.net/users/25514/screenshots/1109319/caffeinated-mac-icon-ramotion-shot.png");
                assertEquals(shot.tags.length, 30);
                assertEquals(shot.tags[0], "android");
                assertEquals(shot.user.id, "25514");
                lock.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("Error on getShot API: message=" + error.getMessage());
            }
        });
        lock.await(10000, TimeUnit.MILLISECONDS);
        assertEquals(0, lock.getCount());
    }

    @Test
    public void getUserLikes() throws Exception {
        app.api().getUserLikes(0, 20, "25514", new Callback<Likes>() {
            @Override
            public void success(Likes likes, Response response) {
                assertEquals(likes.items.size(), 10);
                Likes.Like item = likes.items.get(0);
                assertEquals(item.id, "46915821");
                assertEquals(item.created_at, "2015-11-13T04:41:31Z");
                assertEquals(item.shot.id, "2121350");
                lock.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("Error on getUserLikes API: message=" + error.getMessage());
            }
        });
        lock.await(10000, TimeUnit.MILLISECONDS);
        assertEquals(0, lock.getCount());
    }

    @Test
    public void getLike() throws Exception {
        app.api().getIfLikeAShot("25514", new Callback<Like>() {
            @Override
            public void success(Like like, Response response) {
                assertEquals(like.id, "47478639");
                assertEquals(like.created_at, "2015-11-24T00:42:06Z");
                lock.countDown();
            }

            @Override
            public void failure(RetrofitError error) {
                fail("Error on getUserLikes API: message=" + error.getMessage());
            }
        });
        lock.await(10000, TimeUnit.MILLISECONDS);
        assertEquals(0, lock.getCount());
    }

}
