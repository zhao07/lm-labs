package com.leroymerlin.corp.fr.nuxeo.labs.site.labstemplate;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.model.PropertyException;

import com.leroymerlin.common.core.adapter.SessionAdapterImpl;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.FacetNames;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Schemas;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.Tools;

public class LabsTemplateAdapter extends SessionAdapterImpl implements LabsTemplate {

    private final DocumentModel doc;

    public LabsTemplateAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    public DocumentModel getDocument() {
        return doc;
    }

    @Override
    public String getDocumentTemplateName() throws ClientException {
        if (doc.hasSchema(Schemas.LABSTEMPLATE.getName())) {
            return StringUtils.defaultString(StringUtils.trim((String) doc.getPropertyValue(Schemas.LABSTEMPLATE.prefix() + ":name")));
        }
        return StringUtils.EMPTY;
    }

    @Override
    public String getTemplateName() throws ClientException {
        LabsTemplate siteTemplate = Tools.getAdapter(SiteDocument.class, doc, getSession()).getSite().getTemplate();
        String siteTemplateName = siteTemplate.getDocumentTemplateName();
        if (doc.hasSchema(Schemas.LABSTEMPLATE.getName())) {
            return StringUtils.defaultIfEmpty(StringUtils.trim((String) doc.getPropertyValue(Schemas.LABSTEMPLATE.prefix() + ":name")), siteTemplateName);
        } else {
            return siteTemplateName;
        }
    }

    @Override
    public void setTemplateName(String name) throws ClientException {
        if (doc.hasSchema(Schemas.LABSTEMPLATE.getName())) {
            setPropertyValue(name);   
        } else {
            if (Docs.pageDocs().contains(Docs.fromString(doc.getType()))) {
                if (!StringUtils.isEmpty(StringUtils.trim(name))) {
                    doc.addFacet(FacetNames.LABSTEMPLATE);
                    setPropertyValue(name);   
                }
            }
        }
    }

    private void setPropertyValue(String name) throws PropertyException, ClientException {
        doc.setPropertyValue(Schemas.LABSTEMPLATE.prefix() + ":name", name);
    }

}
