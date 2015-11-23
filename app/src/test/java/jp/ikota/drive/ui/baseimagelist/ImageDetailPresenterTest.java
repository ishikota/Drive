package jp.ikota.drive.ui.baseimagelist;


import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Likes;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.ui.imagedetail.ImageDetailContract;
import jp.ikota.drive.ui.imagedetail.ImageDetailPresenter;
import retrofit.Callback;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ImageDetailPresenterTest {

    private final Shots SHOTS = createSampleData();
    private final Shot SHOT = SHOTS.items.get(0);

    private DribbleService mApi;

    @Mock
    private ImageDetailContract.View mView;

    @Captor
    private ArgumentCaptor<Callback<Likes>> mLoadLikesCallbackCaptor;

    private ImageDetailPresenter mPresenter;

    @Before
    public void setupShotsPresenter() {
        MockitoAnnotations.initMocks(this);
        mApi = mock(DribbleService.class);
        mPresenter = new ImageDetailPresenter(mApi, mView, SHOT, 15);
    }

    @Test
    public void loadNotesAndSetIntoView() {
        // create data
        String json = SampleResponse.getUserLikes();
        String wrapped_json = "{\"items\":"+json+"}";
        Likes likes = new Gson().fromJson(wrapped_json, Likes.class);
        Shots expected = new Shots();
        expected.items = new ArrayList<>();
        for(Likes.Like like: likes.items) expected.items.add(like.shot);

        // start verification
        // TODO : check progress visibility
        mPresenter.loadRelatedShots();
        verify(mApi).getUserLikes(anyInt(), anyInt(), anyString(), mLoadLikesCallbackCaptor.capture());
        mLoadLikesCallbackCaptor.getValue().success(likes, null);
        verify(mView).addShots(expected.items);
        // TODO : assert mShotsPresenter.mPage == 1
    }

    @Test
    public void openShotDetail() {
        Shot shot = SHOTS.items.get(0);
        mPresenter.openShotDetails(shot);
        verify(mView).showShotDetail(shot);
    }

    @Test
    public void toggleFab() {
        when(mView.checkIfLoggedIn()).thenReturn(true);
        mPresenter.clickFab();
        verify(mView).toggleFab(true);
        mPresenter.clickFab();
        verify(mView).toggleFab(false);
    }

    @Test
    public void checkIfShowLoginDialog() {
        when(mView.checkIfLoggedIn()).thenReturn(false);
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
