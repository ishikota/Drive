package jp.ikota.drive.ui.baseimagelist;


import android.content.Context;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;

import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Shot;
import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.ui.imagedetail.ImageDetailAdapterContract;
import jp.ikota.drive.ui.imagedetail.ImageDetailAdapterPresenter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ImageDetailAdapterPresenterTest {

    private Shot mShot;

    private DribbleService mApi;

    @Mock
    private Context mContext;

    @Mock
    private ImageDetailAdapterContract.View mView;

    private ImageDetailAdapterPresenter mPresenter;

    @Before
    public void setupPresenter() {
        mShot = createSampleShot();
        MockitoAnnotations.initMocks(this);
        mApi = mock(DribbleService.class);
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
    public void toggleLike() {
        int like = createSampleShot().likes_count;
        mPresenter.toggleLike();
        verify(mView).setLikeNum(like + 1);
        mPresenter.toggleLike();
        verify(mView).setLikeNum(like);
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


    private Shot createSampleShot() {
        Gson gson = new Gson();
        String json = SampleResponse.getShot();
        return gson.fromJson(json, Shot.class);
    }

}
