package jp.ikota.drive.ui.imagedetail;


import android.content.Context;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Like;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.network.DribbbleRxApi;
import jp.ikota.drive.ui.util.PrivateAccessor;
import retrofit.client.Response;
import rx.Observable;
import rx.Subscriber;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImageDetailAdapterPresenterTest {

    private Shot mShot;

    private DribbbleRxApi mApi;

    @Mock
    private Context mContext;

    @Mock
    private ImageDetailAdapterContract.View mView;

    private ImageDetailAdapterPresenter mPresenter;

    @Before
    public void setupPresenter() {
        mShot = createSampleShot();
        MockitoAnnotations.initMocks(this);
        mApi = mock(DribbbleRxApi.class);
        mPresenter = new ImageDetailAdapterPresenter(mApi, mContext, mShot, mView);
    }

    //TODO how to mock picasso method
    //@Test
    public void setCacheData() {
        mPresenter.setCacheData(mShot);
        verify(mView).setShotData(mShot.title, mShot.user);
        verify(mView).setLikeNum(mShot.likes_count);
        verify(mView).setTags(new ArrayList<>(Arrays.asList(mShot.tags)));
    }

    @Test
    public void loadLikeState_like_on() throws Exception {
        when(mView.getAccessToken()).thenReturn("not empty");
        Observable<Like> observable = Observable.create(new Observable.OnSubscribe<Like>() {
            @Override
            public void call(Subscriber<? super Like> subscriber) {
                subscriber.onNext(new Like());
                subscriber.onCompleted();
            }
        });
        when(mApi.getIfLikeAShot(anyString())).thenReturn(observable);
        mPresenter.loadLikeState();
        boolean like_state = (Boolean) PrivateAccessor.getPrivateField(mPresenter, "is_like_on");
        assertTrue(like_state);
    }

    @Test
    public void loadLikeState_like_off() throws Exception {
        when(mView.getAccessToken()).thenReturn("not empty");
        Observable<Like> observable = Observable.create(new Observable.OnSubscribe<Like>() {
            @Override
            public void call(Subscriber<? super Like> subscriber) {
                subscriber.onError(new IllegalStateException("fake"));
            }
        });
        when(mApi.getIfLikeAShot(anyString())).thenReturn(observable);
        mPresenter.loadLikeState();
        boolean like_state = (Boolean) PrivateAccessor.getPrivateField(mPresenter, "is_like_on");
        assertFalse(like_state);
    }

    @Test
    public void toggleLike() {
        when(mApi.likeAShot(anyString(), anyString())).thenReturn(Observable.<Response>empty());
        when(mApi.unlikeAShot(anyString(), anyString())).thenReturn(Observable.<Response>empty());
        when(mView.getAccessToken()).thenReturn("");
        int like = createSampleShot().likes_count;
        mPresenter.toggleLike();
        verify(mView, never()).setLikeNum(anyInt());
        verify(mApi, never()).likeAShot(anyString(), anyString());
        verify(mApi, never()).likeAShot(anyString(), anyString());
        when(mView.getAccessToken()).thenReturn("dummy");
        mPresenter.toggleLike();
        verify(mView).setLikeNum(like + 1);
        verify(mApi).likeAShot(anyString(), anyString());
        mPresenter.toggleLike();
        verify(mView).setLikeNum(like);
        verify(mApi).unlikeAShot(anyString(), anyString());
    }

    @Test
    public void openTagScreen() {
        String tag = "hoge";
        mPresenter.openTagScreen(tag);
        verify(mView).showTagScreen(tag);
    }

    @Test
    public void openUserScreen() {
        Shot.User user = createSampleShot().user;
        mPresenter.openUserScreen(user);
        verify(mView).showUserScreen(user);
    }

    @Test
    public void relatedLoadFinished() {
        mPresenter.relatedLoadFinished(false);
        verify(mView).removeProgress();
        verify(mView, never()).addEmptyView();
        mPresenter.relatedLoadFinished(true);
        verify(mView).addEmptyView();
    }


    private Shot createSampleShot() {
        Gson gson = new Gson();
        String json = SampleResponse.getShot();
        return gson.fromJson(json, Shot.class);
    }

}
