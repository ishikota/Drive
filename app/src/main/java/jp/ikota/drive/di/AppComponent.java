package jp.ikota.drive.di;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DribbleApiModule.class)
public interface AppComponent extends BaseAppComponent {}
