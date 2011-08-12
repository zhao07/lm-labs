package com.leroymerlin.corp.fr.nuxeo.labs.site.pages;

import java.util.List;

import org.nuxeo.runtime.test.runner.web.WebPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.TimeoutException;

public class WelcomePage extends WebPage {

    private static final int WAITING_TIME = 5;

    @Override
    public WebPage ensureLoaded() {
        waitUntilElementFound(By.className("pageBlocs"), WAITING_TIME);
        return super.ensureLoaded();
    }

    public boolean hasSidebar() {
        try {
            this.waitUntilElementFound(By.id("sidebar"), WAITING_TIME);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
    
    public boolean hasBlocs() {
        List<WebElement> blocs = this.findElements(By.className("bloc"));
        return !blocs.isEmpty();
    }
    
//    public PageClasseurPage goToPageClasseur(String siteURL, String name) {
//        this.getDriver().navigate().to(siteURL + "/" + name);
//        return this.getPage(PageClasseurPage.class);
//    }

    public LoginPage getLoginPage() {
        return getPage(LoginPage.class);
    }
    
}
