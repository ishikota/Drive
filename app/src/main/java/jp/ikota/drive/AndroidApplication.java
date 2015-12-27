package jp.ikota.drive;

import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import javax.inject.Inject;

import jp.ikota.drive.di.BaseAppComponent;
import jp.ikota.drive.di.DaggerAppComponent;
import jp.ikota.drive.di.DribbleApiModule;
import jp.ikota.drive.network.DribbbleRxService;
import jp.ikota.drive.network.DribbleService;


public class AndroidApplication extends Application {

    public int SCREEN_WIDTH;
    public int SCREEN_HEIGHT;

    private BaseAppComponent appComponent = null;

    @Inject
    DribbleService dribbleService;

    @Inject
    DribbbleRxService dribbbleRxService;

    @Override
    public void onCreate() {
        super.onCreate();
        if(appComponent == null) {
            appComponent = DaggerAppComponent
                    .builder()
                    .dribbleApiModule(new DribbleApiModule())
                    .build();
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

    public void setAppComponent(BaseAppComponent appComponent) {
        this.appComponent = appComponent;
    }

    public BaseAppComponent getAppComponent() {
        return appComponent;
    }

    public DribbleService api() {
        return dribbleService;
    }

    public DribbbleRxService rxapi() {
        return dribbbleRxService;
    }

}
