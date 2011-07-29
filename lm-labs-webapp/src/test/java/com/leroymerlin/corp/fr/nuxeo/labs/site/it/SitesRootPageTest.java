package com.leroymerlin.corp.fr.nuxeo.labs.site.it;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.web.Browser;
import org.nuxeo.runtime.test.runner.web.BrowserFamily;
import org.nuxeo.runtime.test.runner.web.HomePage;

import com.google.inject.Inject;

@RunWith(FeaturesRunner.class)
@Features( { LabsWebAppFeature.class })
@Browser(type = BrowserFamily.FIREFOX)
@HomePage(type = LabsSiteWelcomePage.class, url = "http://localhost:8089/labssites")
@Jetty(port = 8089)
public class SitesRootPageTest {

    @Inject SitesRootPage rootPage;
    
    @Test
    public void pageIsReachable() throws Exception {
        assertNotNull(rootPage);
    }
}