package jp.ikota.drive.util;


import android.app.Instrumentation;
import android.support.test.InstrumentationRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import dagger.ObjectGraph;
import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.di.DummyAPIModule;
import jp.ikota.drive.network.Util;

public class TestUtil {

    public static void setupMockServer(HashMap<String, String> override_map) {
        HashMap<String, String> map = new HashMap<>(Util.RESPONSE_MAP);

        if(override_map!=null) {
            for (String key : override_map.keySet()) {
                map.put(key, override_map.get(key));
            }
        }

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        AndroidApplication app =
                (AndroidApplication) instrumentation.getTargetContext().getApplicationContext();

        // setup objectGraph to inject Mock API
        List modules = Collections.singletonList(new DummyAPIModule(map));
        ObjectGraph graph = ObjectGraph.create(modules.toArray());
        app.setObjectGraph(graph);
        app.getObjectGraph().inject(app);
    }
}
