package net.pms.test;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import net.pms.PMS;

public class TestRunListener extends RunListener {
    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
    }
    
    @Override
    public void testRunFinished(Result result) throws Exception {
        PMS.get().shutdown();
        
        super.testRunFinished(result);
    }
}
