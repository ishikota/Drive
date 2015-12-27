package jp.ikota.drive.di;


import java.util.HashMap;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import jp.ikota.drive.network.DribbbleRxService;
import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.network.DribbleURL;
import jp.ikota.drive.network.MockClient;
import retrofit.RestAdapter;


@Module
public class DummyAPIModule {

    public HashMap<String, String> RESPONSE_MAP;

    public DummyAPIModule(HashMap<String, String> map) {
        this.RESPONSE_MAP = map;
    }

    @Provides
    @Singleton
    public DribbleService provideDribbleService() {
        return new RestAdapter
                .Builder()
                .setEndpoint(DribbleURL.API_END_POINT)
                .setClient(new MockClient(RESPONSE_MAP))
                .build()
                .create(DribbleService.class);
    }

    @Provides @Singleton
    public DribbbleRxService provideDribbbleRxService() {
        return new RestAdapter.Builder()
                .setEndpoint(DribbleURL.API_END_POINT)
                .setClient(new MockClient(RESPONSE_MAP))
                .build()
                .create(DribbbleRxService.class);
    }
}
