package com.leroymerlin.corp.fr.nuxeo.labs.site.labssite;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import com.google.inject.Inject;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;

@RunWith(FeaturesRunner.class)
@Features(com.leroymerlin.corp.fr.nuxeo.labs.site.SiteFeatures.class)
@RepositoryConfig(cleanup = Granularity.METHOD)
public class LabsSiteAdapterTest {

    private static final String LABSSITE_TYPE = LabsSiteConstants.Docs.SITE.type();

    @Inject
    private CoreSession session;

    @Test
    public void iCanCreateALabsSiteDocument() throws Exception {
        // Use the session as a factory
        DocumentModel doc = session.createDocumentModel("/", "NameSite1", LABSSITE_TYPE);

        // Modify property
        doc.setPropertyValue("dc:title", "le titre");

        // Persist document in db
        doc = session.createDocument(doc);

        // Commit
        session.save();

        doc = session.getDocument(new PathRef("/NameSite1"));
        assertThat(doc, is(notNullValue()));
        assertThat(doc.getTitle(), is("le titre"));

    }

    @Test
    public void iCanCreateALabsSiteAdapter() throws Exception {
      //Use the session as a factory
        DocumentModel doc = session.createDocumentModel("/", "NameSite1",LABSSITE_TYPE);
        
        LabsSite labssite = doc.getAdapter(LabsSite.class);
        assertThat(labssite,is(notNullValue()));
        labssite.setTitle("Le titre du site");
        
        //Persist document in db
        doc = session.createDocument(doc);
        
        //Commit
        session.save();
        
        doc = session.getDocument(new PathRef("/NameSite1"));
        labssite = doc.getAdapter(LabsSite.class);
        assertThat(labssite,is(notNullValue()));
        assertThat(labssite.getTitle(), is("Le titre du site"));

    }

    @Test
    public void iCanGetPropertiesOnLabsSiteAdapter() throws Exception {
      //Use the session as a factory
        DocumentModel doc = session.createDocumentModel("/", "nameSite1",LABSSITE_TYPE);

        doc.setPropertyValue("dc:creator", "creator");
        
        LabsSite labssite = doc.getAdapter(LabsSite.class);
        assertThat(labssite,is(notNullValue()));
        labssite.setTitle("Le titre du site");
        labssite.SetDescription("Description");
        labssite.setURL("URL");
        
        //Persist document in db
        doc = session.createDocument(doc);
        
        //Commit
        session.save();
        
        doc = session.getDocument(new PathRef("/nameSite1"));
        labssite = doc.getAdapter(LabsSite.class);
        assertThat(labssite,is(notNullValue()));
        assertThat(labssite.getTitle(), is("Le titre du site"));
        assertThat(labssite.getDescription(), is("Description"));
        assertThat(labssite.getURL(), is("URL"));

    }
}
