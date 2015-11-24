package jp.ikota.drive;

import com.squareup.otto.Bus;

public class BusHolder {
    static Bus mBus = new Bus();

    public static Bus get() {
        return mBus;
    }
}
