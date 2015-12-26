package jp.ikota.drive.network;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dagger.ObjectGraph;
import jp.ikota.cappuchino.Cappuchino;
import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.HelloActivity;
import jp.ikota.drive.data.model.Like;
import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.di.DummyAPIModule;
import rx.Subscriber;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class MockRxClientTest extends Cappuchino<HelloActivity> {

    private AndroidApplication app;
    private CountDownLatch lock;

    @Before
    public void setUp() {
        app = (AndroidApplication) getTargetApplication();
        // setup objectGraph to inject Mock API
        List modules = Collections.singletonList(new DummyAPIModule(Util.RESPONSE_MAP));
        ObjectGraph graph = ObjectGraph.create(modules.toArray());
        app.setObjectGraph(graph);
        app.getObjectGraph().inject(app);
        lock = new CountDownLatch(1);
    }

    @Test
    public void getShots() throws Exception {
        app.rxapi().getShots(0, 20)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new TestSubscriber<Shots>() {
                    @Override
                    public void onNext(Shots shots) {
                        assertEquals(shots.items.size(), 15);
                        Shot item = shots.items.get(0);
                        assertEquals(item.id, "2357773");
                        assertEquals(item.title, "Sequoia");
                        assertEquals(item.images.hidpi, "https://d13yacurqjgara.cloudfront.net/users/31752/screenshots/2357773/sequoia.png");
                        assertEquals(item.user.id, "31752");
                        assertEquals(item.user.name, "Nick Slater");
                    }
                });
        lock.await(10000, TimeUnit.MILLISECONDS);
        assertEquals(0, lock.getCount());
    }

    @Test
    public void getShot() throws Exception {
        app.rxapi().getShot("dummy")
                .subscribeOn(Schedulers.newThread())
                .subscribe(new TestSubscriber<Shot>() {
                    @Override
                    public void onNext(Shot shot) {
                        assertEquals(shot.id, "1109319");
                        assertEquals(shot.title, "Caffeinated App Icon");
                        assertEquals(shot.images.hidpi, "https://d13yacurqjgara.cloudfront.net/users/25514/screenshots/1109319/caffeinated-mac-icon-ramotion-shot.png");
                        assertEquals(shot.tags.length, 30);
                        assertEquals(shot.tags[0], "android");
                        assertEquals(shot.user.id, "25514");
                    }
                });
        lock.await(10000, TimeUnit.MILLISECONDS);
        assertEquals(0, lock.getCount());
    }

    @Test
    public void getUserLikes() throws Exception {
        app.rxapi().getUserLikes(0, 20, "25514")
                .subscribeOn(Schedulers.newThread())
                .subscribe(new TestSubscriber<Likes>() {
                    @Override
                    public void onNext(Likes likes) {
                        assertEquals(likes.items.size(), 10);
                        Likes.Like item = likes.items.get(0);
                        assertEquals(item.id, "46915821");
                        assertEquals(item.created_at, "2015-11-13T04:41:31Z");
                        assertEquals(item.shot.id, "2121350");
                    }
                });
        lock.await(10000, TimeUnit.MILLISECONDS);
        assertEquals(0, lock.getCount());
    }

    @Test
    public void getLike() throws Exception {
        app.rxapi().getIfLikeAShot("25514")
                .subscribeOn(Schedulers.newThread())
                .subscribe(new TestSubscriber<Like>() {
                    @Override
                    public void onNext(Like like) {
                        assertEquals(like.id, "47478639");
                        assertEquals(like.created_at, "2015-11-24T00:42:06Z");
                    }
                });
        lock.await(10000, TimeUnit.MILLISECONDS);
        assertEquals(0, lock.getCount());
    }

    private abstract class TestSubscriber<T> extends Subscriber<T> {
        @Override
        public void onCompleted() {
            lock.countDown();
        }
        @Override
        public void onError(Throwable error) {
            fail("Error on getShots API: message=" + error.getMessage());
        }
    }
}
