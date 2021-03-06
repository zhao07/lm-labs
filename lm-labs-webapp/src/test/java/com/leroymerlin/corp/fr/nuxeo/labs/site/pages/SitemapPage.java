package com.leroymerlin.corp.fr.nuxeo.labs.site.pages;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.runtime.test.runner.web.WebPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SitemapPage extends WebPage {

    private static final int WAITING_TIME = 5;

    @Override
    public WebPage ensureLoaded() {
        waitUntilElementFound(By.id("content"), WAITING_TIME);
        return super.ensureLoaded();
    }

    public Boolean containsTreeview() {
        WebElement element = findElement(By.id("tree"), WAITING_TIME);
        return StringUtils.isNotBlank(element.getText());
    }
    
    public Boolean containsListview() {
        WebElement element = findElement(By.id("sitemapList"), WAITING_TIME);
        return StringUtils.isNotBlank(element.getText());
    }
    
    public void reduceTreeview() {
        findElement(By.id("reduceLink"), WAITING_TIME).click();
    }
    
    public void switchToListview() {
        findElement(By.id("listButton"), WAITING_TIME).click();
    }
    
    public void switchToTreeview() {
        findElement(By.id("treeButton"), WAITING_TIME).click();
    }
}
