package jp.ikota.drive.di;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = DummyAPIModule.class)
public interface TestComponent extends BaseAppComponent {}
