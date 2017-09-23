package io.github.aistomin.network.manager;

import static org.junit.Assert.assertEquals;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Test
    public void useAppContext() throws Exception {
        assertEquals(
            "io.github.aistomin.network.manager",
            InstrumentationRegistry.getTargetContext().getPackageName()
        );
    }
}
