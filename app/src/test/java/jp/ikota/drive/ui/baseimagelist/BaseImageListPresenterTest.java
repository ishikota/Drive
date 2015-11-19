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
import retrofit.Callback;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class BaseImageListPresenterTest {

    private final Shots SHOTS = createSampleData();

    private DribbleService mApi;

    @Mock
    private BaseImageListContract.View mShotsView;

    @Captor
    private ArgumentCaptor<Callback<Shots>> mLoadShotsCallbackCaptor;

    private BaseImageListPresenter mShotsPresenter;

    @Before
    public void setupShotsPresenter() {
        MockitoAnnotations.initMocks(this);
        mApi = mock(DribbleService.class);
        mShotsPresenter = new BaseImageListPresenter(mApi, mShotsView, 15);
    }

//    @Test
//    public void refreshShots() {
//        mShotsPresenter.loadShots();
//        mShotsPresenter.refreshShots();
//        verify(mShotsView).setProgressIndicator(true);
//        // TODO : assert mShotsPresenter.mPage == 0
//        verify(mApi).getShots(anyInt(), anyInt(), mLoadShotsCallbackCaptor.capture());
//        mLoadShotsCallbackCaptor.getValue().success(SHOTS, null);
//        // TODO : assert mShotsPresenter.mPage == 1
//        verify(mShotsView).setProgressIndicator(false);
//    }

    @Test
    public void loadNotesAndSetIntoView() {
        // TODO : assert mShotsPresenter.mPage == 0
        mShotsPresenter.loadShots();
        // before api call
        verify(mApi).getShots(anyInt(), anyInt(), mLoadShotsCallbackCaptor.capture());

        mLoadShotsCallbackCaptor.getValue().success(SHOTS, null);
        // after api call
        verify(mShotsView).setProgressIndicator(false);
        verify(mShotsView).showShots(SHOTS.items);
        // TODO : assert mShotsPresenter.mPage == 1
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
