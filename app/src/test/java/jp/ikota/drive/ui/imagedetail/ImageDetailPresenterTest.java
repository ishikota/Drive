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
import jp.ikota.drive.network.DribbbleRxService;
import rx.Observable;
import rx.Subscriber;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
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
    public void loadRelatedShots() {
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
        // TODO : check progress visibility
        mPresenter.loadRelatedShots();
        verify(mView).addShots(expected.items);
        verify(mView).notifyRelatedLoadFinish(false);
        // TODO : assert mShotsPresenter.mPage == 2
    }

    @Test
    public void openShotDetail() {
        Shot shot = SHOTS.items.get(0);
        mPresenter.openShotDetails(shot);
        verify(mView).showShotDetail(shot);
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
    public void initFab() {
        mPresenter.initFab(new ImageDetailPresenter.LikeAvailableEvent(true));
        verify(mView).toggleFab(true, false);
        assertTrue(mPresenter.getIfFabIsOn());
        mPresenter.initFab(new ImageDetailPresenter.LikeAvailableEvent(false));
        verify(mView).toggleFab(false, false);
        assertFalse(mPresenter.getIfFabIsOn());
    }

    @Test
    public void checkIfShowLoginDialog() {
        when(mView.getAccessToken()).thenReturn("");
        mPresenter.clickFab();
        verify(mView).showLoginDialog();
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

    private Shots createSampleData() {
        Gson gson = new Gson();
        String json = SampleResponse.getShots();
        json = "{\"items\":"+json+"}";
        return gson.fromJson(json, Shots.class);
    }

}
