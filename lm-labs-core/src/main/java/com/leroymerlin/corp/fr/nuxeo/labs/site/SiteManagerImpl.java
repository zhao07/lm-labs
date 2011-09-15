package com.leroymerlin.corp.fr.nuxeo.labs.site;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.runtime.model.DefaultComponent;

import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.portal.security.SecurityData;
import com.leroymerlin.corp.fr.nuxeo.portal.security.SecurityDataHelper;

public class SiteManagerImpl extends DefaultComponent implements SiteManager {

    private static final PathRef SITES_ROOT_REF = new PathRef(
            "/default-domain/sites");

    @Override
    public LabsSite createSite(CoreSession session, String title, String url)
            throws ClientException, SiteManagerException {


        validateSiteCreationRequest(session, title, url);


        DocumentModel sitesRoot = getSiteRoot(session);
        DocumentModel docLabsSite = session.createDocumentModel(
                sitesRoot.getPathAsString(), title,
                LabsSiteConstants.Docs.SITE.type());

        LabsSite labSite = docLabsSite.getAdapter(LabsSite.class);
        labSite.setTitle(title);
        labSite.setURL(url);
        docLabsSite = session.createDocument(docLabsSite);
        return docLabsSite.getAdapter(LabsSite.class);
    }


    private void validateSiteCreationRequest(CoreSession session, String title, String url)
            throws ClientException, SiteManagerException {

        if(StringUtils.isEmpty(title.trim())) {
            throw new SiteManagerException(
                    "label.sitemanager.error.title_cant_be_empty");
        }

        if(StringUtils.isEmpty(url.trim())) {
            throw new SiteManagerException(
                    "label.sitemanager.error.url_cant_be_empty");
        }

        if (siteExists(session, url)) {
            throw new SiteManagerException(
                    "label.sitemanager.error.site_already_exists");
        }
    }

    private DocumentModel getSiteRoot(CoreSession session)
            throws ClientException {
        DocumentRef sitesRootRef = SITES_ROOT_REF;
        if (!session.exists(sitesRootRef)) {
            return createSitesRoot(session);
        } else {
            return session.getDocument(sitesRootRef);
        }

    }

    private DocumentModel createSitesRoot(CoreSession session)
            throws ClientException {
        UnrestrictedSessionRunner runner = new UnrestrictedSessionRunner(
                session) {

            @Override
            public void run() throws ClientException {
                DocumentModel sitesRoot = session.createDocumentModel(
                        "/default-domain/", "sites", "SitesRoot");
                sitesRoot.setPropertyValue("dc:title", "Default root of sites");
                sitesRoot = session.createDocument(sitesRoot);

                SecurityData data = SecurityDataHelper.buildSecurityData(sitesRoot);
                data.addModifiablePrivilege(SecurityConstants.EVERYONE,
                        SecurityConstants.READ, true);
                data.addModifiablePrivilege(SecurityConstants.EVERYONE,
                        SecurityConstants.ADD_CHILDREN, true);
                SecurityDataHelper.updateSecurityOnDocument(sitesRoot, data);
                session.saveDocument(sitesRoot);
            }
        };

        runner.runUnrestricted();
        return session.getDocument(SITES_ROOT_REF);
    }

    @Override
    public LabsSite getSite(CoreSession session, String url)
            throws ClientException, SiteManagerException {
        String query = String.format("SELECT * FROM %s WHERE webc:url = '%s'",
                LabsSiteConstants.Docs.SITE.type(), url);
        DocumentModelList sites = session.query(query);
        assert sites.size() <= 1;
        if (sites.size() == 1) {
            return sites.get(0)
                    .getAdapter(LabsSite.class);
        } else {
            throw new SiteManagerException(
                    "label.sitemanager.error.site_does_not_exists");
        }

    }

    @Override
    public void removeSite(CoreSession session, LabsSite site)
            throws ClientException {
        session.removeDocument(site.getDocument().getRef());

    }

    @Override
    public Boolean siteExists(CoreSession session, String url)
            throws ClientException {
        try {
            getSite(session, url);
            return true;
        } catch (SiteManagerException e) {
            return false;
        }
    }

    @Override
    public List<LabsSite> getAllSites(CoreSession session) throws ClientException {
        String query = String.format("SELECT * FROM %s", LabsSiteConstants.Docs.SITE.type());
        DocumentModelList docs = session.query(query);
        List<LabsSite> sites = new ArrayList<LabsSite>();
        for(DocumentModel doc : docs) {
            LabsSite site = doc.getAdapter(LabsSite.class);
            if(site!= null) {
                sites.add(site);
            }

        }
        return sites;

    }

}