package jp.ikota.drive.ui.baseimagelist;


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
import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.ui.imagedetail.ImageDetailContract;
import jp.ikota.drive.ui.imagedetail.ImageDetailPresenter;
import retrofit.Callback;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ImageDetailPresenterTest {

    private final Shots SHOTS = createSampleData();

    private DribbleService mApi;

    @Mock
    private ImageDetailContract.View mView;

    @Captor
    private ArgumentCaptor<Callback<Shots>> mLoadShotsCallbackCaptor;

    private ImageDetailPresenter mPresenter;

    @Before
    public void setupShotsPresenter() {
        MockitoAnnotations.initMocks(this);
        mApi = mock(DribbleService.class);
        mPresenter = new ImageDetailPresenter(mApi, mView, 15);
    }

    @Test
    public void loadNotesAndSetIntoView() {
        // TODO : check progress visibility
        mPresenter.loadRelatedShots();
        verify(mApi).getShots(anyInt(), anyInt(), mLoadShotsCallbackCaptor.capture());
        mLoadShotsCallbackCaptor.getValue().success(SHOTS, null);
        verify(mView).addShots(SHOTS.items);
        // TODO : assert mShotsPresenter.mPage == 1
    }

    @Test
    public void openShotDetail() {
        Shot shot = SHOTS.items.get(0);
        mPresenter.openShotDetails(shot);
        verify(mView).showShotDetail(shot);
    }

    private Shots createSampleData() {
        Gson gson = new Gson();
        String json = SampleResponse.getShots();
        json = "{\"items\":"+json+"}";
        return gson.fromJson(json, Shots.class);
    }

}
