package jp.ikota.drive.network;

import rx.Subscriber;


public abstract class ApiSubscriber<T> extends Subscriber<T>{
    @Override
    public void onCompleted() {
        //Log.d("ApiSubscriber", "Api call is completed.");
    }
}
