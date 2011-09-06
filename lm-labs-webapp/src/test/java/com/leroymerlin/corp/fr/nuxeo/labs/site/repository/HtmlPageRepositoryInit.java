package com.leroymerlin.corp.fr.nuxeo.labs.site.repository;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;

import com.leroymerlin.corp.fr.nuxeo.labs.site.classeur.PageClasseur;
import com.leroymerlin.corp.fr.nuxeo.labs.site.classeur.PageClasseurAdapter;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlPage;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlRow;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlSection;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;

public class HtmlPageRepositoryInit implements RepositoryInit {

    public static final String SITE_URL = "ofm";
    public static final String SITE_TITLE = "OFM";
    
    @Override
    public void populate(CoreSession session) throws ClientException {
        DocumentModel root = LabsSiteUtils.getSitesRoot(session);
        if (!session.exists(new PathRef(root.getPathAsString() + "/" + SITE_TITLE))) {
            DocumentModel ofm = session.createDocumentModel(root.getPathAsString(), SITE_TITLE, LabsSiteConstants.Docs.SITE.type());
            LabsSite site = ofm.getAdapter(LabsSite.class);
            site.setURL(SITE_URL);
            site.setTitle(SITE_TITLE);
            ofm = session.createDocument(ofm);
            
            DocumentModel doc = session.createDocumentModel(ofm.getPathAsString()+"/" + LabsSiteConstants.Docs.TREE.docName(),"htmlTestPage","HtmlPage");
            HtmlPage page = doc.getAdapter(HtmlPage.class);
            
            page.setTitle("HTML Test page");
            page.setDescription("Page HTML de test");
            
            HtmlSection section = page.addSection();
            section.setTitle("Première section");
            section.setDescription("Avec description");
            
			HtmlRow row = section.addRow();
            row.addContent(4, "Marge à gauche");
            row.addContent(12, "Contenu à droite");
            
            row = section.addRow();
            row.addContent(12, "Contenu à gauche");
            row.addContent(4, "Marge à droite");
            
            
            section = page.addSection();
            section.setTitle("Deuxième section");
            section.setDescription("Une deuxième description");
            
			row = section.addRow();
            row.addContent(4, "Marge à gauche");
            row.addContent(12, "Contenu à droite");
            
            row = section.addRow();
            row.addContent(12, "Contenu à gauche");
            row.addContent(4, "Marge à droite");
            
            session.createDocument(page.getDocument());
            
    
            session.save();
        }
    }

}