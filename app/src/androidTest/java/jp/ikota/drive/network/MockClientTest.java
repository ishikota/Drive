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
                Shots.Item item = shots.items.get(0);
                assertEquals(item.id,"2357773");
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

}
