package net.pms.test;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import net.pms.PMS;

public class TestRunListener extends RunListener {
    @Override
    public void testRunStarted(Description description) throws Exception {
        System.setProperty("UMS_PROFILE", "target/test-classes/resources");
        
//      PmsConfiguration conf = new PmsConfiguration();
//      RendererConfiguration.loadRendererConfigurations(conf);
//      PMS.get().setConfiguration(conf);
//      PMS.get().setRegistry(PMS.createSystemUtils());
//      PMS.get().setGlobalRepo(new GlobalIdRepo());
//      PMS.get().refreshLibrary(false);
//      Tables.checkTables();
      
        String[] args = new String[] { "headless" };
        PMS.main(args);

        super.testRunStarted(description);
    }
    
    @Override
    public void testRunFinished(Result result) throws Exception {
        PMS.get().shutdown();
        
        super.testRunFinished(result);
    }
}
