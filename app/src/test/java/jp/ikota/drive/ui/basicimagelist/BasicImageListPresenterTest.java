package jp.ikota.drive.ui.basicimagelist;


import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbbleRxApi;
import jp.ikota.drive.ui.util.PrivateAccessor;
import retrofit.Callback;
import rx.Observable;
import rx.Subscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BasicImageListPresenterTest {

    private final Shots SHOTS = createSampleData();

    private DribbbleRxApi mApi;

    @Mock
    private BasicImageListContract.View mShotsView;

    @Captor
    private ArgumentCaptor<Callback<Shots>> mLoadShotsCallbackCaptor;

    private BasicImageListPresenter mShotsPresenter;

    @Before
    public void setupShotsPresenter() {
        MockitoAnnotations.initMocks(this);
        mApi = mock(DribbbleRxApi.class);
        mShotsPresenter = new BasicImageListPresenter(mApi, mShotsView, 15);
    }

    @Test
    public void refreshShots_success() {
        Observable<Shots> observable = Observable.create(new Observable.OnSubscribe<Shots>() {
            @Override
            public void call(Subscriber<? super Shots> subscriber) {
                subscriber.onNext(SHOTS);
                subscriber.onCompleted();
            }
        });
        when(mApi.getShots(anyInt(), anyInt())).thenReturn(observable);
        mShotsPresenter.refreshShots();

        verify(mShotsView).clearShots();
        verify(mShotsView).addShots(SHOTS.items);
        verify(mShotsView).finishRefreshIndicator();
        verify(mShotsView).showEmptyView(false);
        // TODO : assert mShotsPresenter.mPage == 1 (how to assert private variable state)
    }

    @Test
    public void refreshShots_error() {
        Observable<Shots> observable = Observable.create(new Observable.OnSubscribe<Shots>() {
            @Override
            public void call(Subscriber<? super Shots> subscriber) {
                subscriber.onError(new IllegalStateException("fake"));
            }
        });
        when(mApi.getShots(anyInt(), anyInt())).thenReturn(observable);
        mShotsPresenter.refreshShots();
        verify(mShotsView).showNetworkError();
        verify(mShotsView).finishRefreshIndicator();
        verify(mShotsView).showEmptyView(true);
    }

    @Test
    public void loadShots_success() throws Exception {
        Observable<Shots> observable = Observable.create(new Observable.OnSubscribe<Shots>() {
            @Override
            public void call(Subscriber<? super Shots> subscriber) {
                subscriber.onNext(SHOTS);
                subscriber.onCompleted();
            }
        });
        when(mApi.getShots(anyInt(), anyInt())).thenReturn(observable);
        mShotsPresenter.loadShots();

        // after api call
        verify(mShotsView).setProgressIndicator(false);
        verify(mShotsView).addShots(SHOTS.items);
        verify(mShotsView).showEmptyView(false);
        assertFalse((Boolean) PrivateAccessor.getPrivateField(mShotsPresenter, "loading"));
        assertEquals(2, PrivateAccessor.getPrivateField(mShotsPresenter, "mPage"));
    }

    @Test
    public void loadShots_error() throws Exception {
        Observable<Shots> observable = Observable.create(new Observable.OnSubscribe<Shots>() {
            @Override
            public void call(Subscriber<? super Shots> subscriber) {
                subscriber.onError(new IllegalStateException("fake"));
            }
        });
        when(mApi.getShots(anyInt(), anyInt())).thenReturn(observable);
        mShotsPresenter.loadShots();

        verify(mShotsView).showNetworkError();
        verify(mShotsView).setProgressIndicator(false);
        verify(mShotsView).showEmptyView(true);
        assertFalse((Boolean) PrivateAccessor.getPrivateField(mShotsPresenter, "loading"));
    }

    @Test
    public void openShotDetail() {
        Shot shot = SHOTS.items.get(0);
        mShotsPresenter.openShotDetails(shot);
        verify(mShotsView).showShotDetail(shot);
    }

    @Test
    public void reachListBottom() throws Exception {
        PrivateAccessor.setPrivateField(mShotsPresenter, "loading", false);
        mShotsPresenter.reachListBottom();
        verify(mShotsView, never()).setProgressIndicator(true);
        PrivateAccessor.setPrivateField(mShotsPresenter, "loading", true);
        mShotsPresenter.reachListBottom();
        verify(mShotsView).setProgressIndicator(true);
    }

    private Shots createSampleData() {
        Gson gson = new Gson();
        String json = SampleResponse.getShots();
        json = "{\"items\":"+json+"}";
        return gson.fromJson(json, Shots.class);
    }


}
