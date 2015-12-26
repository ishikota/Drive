package jp.ikota.drive;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.ObjectGraph;
import jp.ikota.drive.di.DribbleApiModule;
import jp.ikota.drive.network.DribbbleRxService;
import jp.ikota.drive.network.DribbleService;


public class AndroidApplication extends Application {

    public int SCREEN_WIDTH;
    public int SCREEN_HEIGHT;

    private ObjectGraph objectGraph = null;

    @Inject
    DribbleService dribbleService;

    @Inject
    DribbbleRxService dribbbleRxService;

    @Override
    public void onCreate() {
        super.onCreate();
        if(objectGraph == null) {
            List modules = Collections.singletonList(new DribbleApiModule());
            objectGraph = ObjectGraph.create(modules.toArray());
        }

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        SCREEN_WIDTH = size.x;
        SCREEN_HEIGHT = size.y;
        Log.i("AndroidApplication",
                String.format("Screen size: w = %d, h = %d", SCREEN_WIDTH, SCREEN_HEIGHT));
    }

    // used to set ObjectGraph for test
    public void setObjectGraph(ObjectGraph graph) {
        objectGraph = graph;
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    public DribbleService api() {
        return dribbleService;
    }

    public DribbbleRxService rxapi() {
        return dribbbleRxService;
    }

}
