package jp.ikota.drive.di;

import javax.inject.Singleton;

import dagger.Provides;
import jp.ikota.drive.AndroidApplication;
import jp.ikota.drive.network.DribbleService;
import jp.ikota.drive.network.DribbleURL;
import retrofit.RestAdapter;
@dagger.Module(
    injects = AndroidApplication.class,
    library = true
)
public class DribbleApiModule {

    @Provides @Singleton
    public DribbleService provideDribbleService() {
        return new RestAdapter.Builder()
                .setEndpoint(DribbleURL.END_POINT)
                .build()
                .create(DribbleService.class);
    }

}
