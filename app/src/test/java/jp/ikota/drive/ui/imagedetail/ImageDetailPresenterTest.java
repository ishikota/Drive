package jp.ikota.drive.ui.imagedetail;


import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbbleRxApi;
import jp.ikota.drive.ui.util.PrivateAccessor;
import rx.Observable;
import rx.Subscriber;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImageDetailPresenterTest {

    private final Shots SHOTS = createSampleData();
    private final Shot SHOT = SHOTS.items.get(0);

    private DribbbleRxApi mApi;

    @Mock
    private ImageDetailContract.View mView;

    private ImageDetailPresenter mPresenter;

    @Before
    public void setupShotsPresenter() {
        MockitoAnnotations.initMocks(this);
        mApi = mock(DribbbleRxApi.class);
        mPresenter = new ImageDetailPresenter(mApi, mView, SHOT, 15);
    }

    @Test
    public void loadRelatedShots_loding_off_success() throws Exception {
        // create data
        String json = SampleResponse.getUserLikes();
        String wrapped_json = "{\"items\":"+json+"}";
        final Likes likes = new Gson().fromJson(wrapped_json, Likes.class);
        Shots expected = new Shots();
        expected.items = new ArrayList<>();
        for(Likes.Like like: likes.items) expected.items.add(like.shot);

        Observable<Likes> observable = Observable.create(new Observable.OnSubscribe<Likes>() {
            @Override
            public void call(Subscriber<? super Likes> subscriber) {
                subscriber.onNext(likes);
                subscriber.onCompleted();
            }
        });
        when(mApi.getUserLikes(anyInt(), anyInt(), anyString())).thenReturn(observable);

        // start verification
        mPresenter.loadRelatedShots();
        verify(mView).addShots(expected.items);
        assertFalse((Boolean) PrivateAccessor.getPrivateField(mPresenter, "loading"));
        assertEquals(2, PrivateAccessor.getPrivateField(mPresenter, "mPage"));
        verify(mView).notifyRelatedLoadFinish(false);
    }

    @Test
    public void loadRelatedShots_loding_off_error() throws Exception {
        // create data

        Observable<Likes> observable = Observable.create(new Observable.OnSubscribe<Likes>() {
            @Override
            public void call(Subscriber<? super Likes> subscriber) {
                subscriber.onError(new IllegalStateException("fake"));
            }
        });
        when(mApi.getUserLikes(anyInt(), anyInt(), anyString())).thenReturn(observable);

        // start verification
        mPresenter.loadRelatedShots();
        verify(mView).showNetworkError();
        verify(mView).notifyRelatedLoadFinish(true);
    }

    @Test
    public void loadRelatedShots_loding_on() throws Exception {
        PrivateAccessor.setPrivateField(mPresenter, "loading", true);
        mPresenter.loadRelatedShots();
        verify(mApi, never()).getUserLikes(anyInt(), anyInt(), anyString());
    }

    @Test
    public void openShotDetail() {
        Shot shot = SHOTS.items.get(0);
        mPresenter.openShotDetails(shot);
        verify(mView).showShotDetail(shot);
    }

    @Test
    public void clickFab() throws Exception {
        when(mView.getAccessToken()).thenReturn("");
        mPresenter.clickFab();
        verify(mView).showLoginDialog();
        when(mView.getAccessToken()).thenReturn("not empty");
        mPresenter.clickFab();
        assertTrue((Boolean) PrivateAccessor.getPrivateField(mPresenter, "fab_is_on"));
        verify(mView).toggleFab(true, true);
    }

    @Test
    public void toggleFab() {
        when(mView.getAccessToken()).thenReturn("dummy");
        mPresenter.clickFab();
        verify(mView).toggleFab(true, true);
        assertTrue(mPresenter.getIfFabIsOn());
        mPresenter.clickFab();
        verify(mView).toggleFab(false, true);
        assertFalse(mPresenter.getIfFabIsOn());
    }

    @Test
    public void updateToolbarAlpha() {
        mPresenter.updateToolbarAlpha(100);
        verify(mView).setToolbarAlpha(100);
        mPresenter.updateToolbarAlpha(500);
        verify(mView).setToolbarAlpha(200);
        mPresenter.updateToolbarAlpha(-700);
        verify(mView).setToolbarAlpha(0);
    }

    @Test
    public void initFab() {
        mPresenter.initFab(new ImageDetailPresenter.LikeAvailableEvent(true));
        verify(mView).toggleFab(true, false);
        assertTrue(mPresenter.getIfFabIsOn());
        mPresenter.initFab(new ImageDetailPresenter.LikeAvailableEvent(false));
        verify(mView).toggleFab(false, false);
        assertFalse(mPresenter.getIfFabIsOn());
    }

    private Shots createSampleData() {
        Gson gson = new Gson();
        String json = SampleResponse.getShots();
        json = "{\"items\":"+json+"}";
        return gson.fromJson(json, Shots.class);
    }

}
