package jp.ikota.drive;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import jp.ikota.cappuchino.Cappuchino;

import static jp.ikota.cappuchino.matcher.ViewMatcherWrapper.id;

@RunWith(AndroidJUnit4.class)
public class HelloActivityTest extends Cappuchino<HelloActivity> {
    @Test
    public void helloCappuchino() {
        launchActivity();
        expect(id(R.id.text)).hasText(R.string.hello_world);
    }
}
