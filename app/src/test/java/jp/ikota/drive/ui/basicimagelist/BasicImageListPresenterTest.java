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
import retrofit.Callback;
import rx.Observable;
import rx.Subscriber;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
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
    public void refreshShots() {
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
    public void loadNotesAndSetIntoView() {
        // TODO : assert mShotsPresenter.mPage == 1 (how to assert private variable state)
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
        // TODO : assert mShotsPresenter.mPage == 2 (how to assert private variable state)
    }

    @Test
    public void openShotDetail() {
        Shot shot = SHOTS.items.get(0);
        mShotsPresenter.openShotDetails(shot);
        verify(mShotsView).showShotDetail(shot);
    }

    private Shots createSampleData() {
        Gson gson = new Gson();
        String json = SampleResponse.getShots();
        json = "{\"items\":"+json+"}";
        return gson.fromJson(json, Shots.class);
    }


}
