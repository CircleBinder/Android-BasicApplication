package circlebinder.common;

import android.app.Application;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import circlebinder.common.app.CircleBinderApplication;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ApplicationTest {
    
    @Before
    public void config() {
        ShadowLog.stream = System.out;
    }
    
    @Test
    public void test() {
        Application application = new CircleBinderApplication();
        application.onCreate();
        application.onTerminate();
        Assert.assertTrue(true);
        
    }
}
