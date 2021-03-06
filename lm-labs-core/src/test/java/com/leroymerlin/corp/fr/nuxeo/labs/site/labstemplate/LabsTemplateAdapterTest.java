package com.leroymerlin.corp.fr.nuxeo.labs.site.labstemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;
import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.test.DefaultRepositoryInit;
import com.leroymerlin.corp.fr.nuxeo.labs.site.test.SiteFeatures;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.FacetNames;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

@RunWith(FeaturesRunner.class)
@Features(SiteFeatures.class)
@RepositoryConfig(cleanup=Granularity.METHOD, init=DefaultRepositoryInit.class)
public class LabsTemplateAdapterTest {
    @Inject
    SiteManager sm;

    @Inject
    CoreSession session;

    private LabsSite site;


    @Before
    public void doBefore() throws Exception {

        site = sm.createSite(session, "Mon titre", "myurl");
        assertThat(site,is(notNullValue()));
        site.setDescription("Un super site");
        session.saveDocument(site.getDocument());
        session.save();
    }

    @Test
    public void iCanGetTemplateOnSite() throws Exception {
        assertNotNull(site.getTemplate());
    }

    @Test
    public void iCanGetTemplateOnPage() throws Exception {
        assertNotNull(Tools.getAdapter(Page.class, site.getIndexDocument(), session).getTemplate());
    }

    @Test
    public void iCanGetTemplateOnPageInheritSite() throws Exception {
        String templateSite = site.getTemplate().getTemplateName();
        String templatePage = Tools.getAdapter(Page.class, site.getIndexDocument(), session).getTemplate().getTemplateName();
        assertThat(templatePage, is((templateSite)));
    }
    
    @Test
    public void pageHasNotFacet() throws Exception {
        DocumentModel indexDocument = site.getIndexDocument();
        assertFalse(indexDocument.hasFacet(FacetNames.LABSTEMPLATE));
    }
    
    @Test
    public void pageHasFacetAfterAddFacetTemplate() throws Exception {
        DocumentModel indexDocument = site.getIndexDocument();
        Page page = Tools.getAdapter(Page.class, indexDocument, session);
        page.addFacetTemplate();
        indexDocument = session.saveDocument(indexDocument);
        session.save();
        
        indexDocument = site.getIndexDocument();
        assertTrue(indexDocument.hasFacet(FacetNames.LABSTEMPLATE));
    }

    @Test
    public void iCanGetTemplateOnPageDifferentOfSite() throws Exception {
        String templateSite = site.getTemplate().getTemplateName();
        DocumentModel indexDocument = site.getIndexDocument();
        Page page = Tools.getAdapter(Page.class, indexDocument, session);
        page.addFacetTemplate();
        
        LabsTemplate templatePage = page.getTemplate();
        templatePage.setTemplateName("name");
        session.saveDocument(indexDocument);
        session.save();
        
        templatePage = Tools.getAdapter(Page.class, site.getIndexDocument(), session).getTemplate();
        String templatePageString = templatePage.getTemplateName();
        assertThat(templatePageString, not((templateSite)));
        assertThat("name", not(templateSite));
    }
}
