package jp.ikota.drive.ui.basicimagelist;

import android.app.Activity;
import android.content.Intent;
import android.support.test.runner.AndroidJUnit4;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import jp.ikota.cappuchino.Cappuchino;
import jp.ikota.cappuchino.assertion.LaunchActivityAssertion;
import jp.ikota.drive.R;
import jp.ikota.drive.data.SampleResponse;
import jp.ikota.drive.data.model.Shots;
import jp.ikota.drive.network.DribbleURL;
import jp.ikota.drive.ui.imagedetail.ImageDetailActivity;
import jp.ikota.drive.util.TestUtil;

import static jp.ikota.cappuchino.matcher.ViewMatcherWrapper.id;
import static junit.framework.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BasicImageListScreenTest extends Cappuchino<BasicImageListActivity> {

    @Test
    public void preConditions() {
        launchActivity();
        expect(id(R.id.container)).exists();
        expect(id(android.R.id.list)).exists();
        expect(id(android.R.id.empty)).exists();
        expect(id(R.id.progress)).exists();
        expect(id(R.id.swipe_refresh)).exists();
    }

    @Test
    public void checkIfProgressShownAtFirst() {
        TestUtil.setupMockServer(null);
        launchActivity();
        expect(id(R.id.progress)).isVisible();
        listIdlingTarget(android.R.id.list).waitFirstItemLoad();
        expect(id(R.id.progress)).isGone();
    }

    @Test
    public void testEmptyView_show() {
        // Set up mock server to return empty response
        HashMap<String, String> map = new HashMap<>();
        String empty_response = "{\"items\":[]}";
        map.put(DribbleURL.PATH_SHOTS, empty_response);
        TestUtil.setupMockServer(map);

        launchActivity();
        expect(id(android.R.id.empty)).isGone();
        viewIdlingTarget(R.id.progress).waitUntilViewIsGone();
        expect(id(android.R.id.empty)).isVisible();
    }

    @Test
    public void loadItems() {
        TestUtil.setupMockServer(null);
        launchActivity();
        listIdlingTarget(android.R.id.list).waitFirstItemLoad();
        expect(id(android.R.id.list)).listItemCountIs(30);
        perform(id(android.R.id.list)).scrollToPosition(25);
        listIdlingTarget(android.R.id.list).waitUntilItemCountGraterThan(30);
        expect(id(android.R.id.list)).listItemCountIs(45);
    }

    @Test
    public void showShotDetail() {
        TestUtil.setupMockServer(null);
        launchActivity();

        listIdlingTarget(android.R.id.list).waitFirstItemLoad();
        LaunchActivityAssertion
                .launch(ImageDetailActivity.class, new LaunchActivityAssertion.LaunchMethod() {
                    @Override
                    public void launchActivity() {
                        perform(id(android.R.id.list)).clickItemAtPosition(0);
                    }
                })
                .asserts(new LaunchActivityAssertion.ActivityAssertion() {
                    @Override
                    public void assertActivity(Activity activity, Intent intent) {
                        assertEquals("Launched Activity is not ImageDetailActivity",
                                ImageDetailActivity.class, activity.getClass());
                        // assert if clicked shot passed to detail activity
                        Gson gson = new Gson();
                        Shots shots = gson.fromJson("{\"items\":" + SampleResponse.getShots() + "}", Shots.class);
                        String expected = gson.toJson(shots.items.get(0));
                        String json = intent.getStringExtra("content");
                        assertEquals(expected, json);
                    }
                });
    }

    @Test
    public void showNetworkError() {
        //TODO how to test if toast is shown
        //TODO how to simulate offline state
    }

}
