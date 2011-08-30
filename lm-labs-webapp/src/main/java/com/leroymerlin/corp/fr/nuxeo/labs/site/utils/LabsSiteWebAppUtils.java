package com.leroymerlin.corp.fr.nuxeo.labs.site.utils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderService;
import org.nuxeo.ecm.webengine.model.Resource;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.corp.fr.nuxeo.labs.site.providers.LatestUploadsPageProvider;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;

public final class LabsSiteWebAppUtils {

    private static final String LATEST_UPLOADS_PAGEPROVIDER = "latest_uploads";

    private LabsSiteWebAppUtils() {}
    
    public static String getTreeview(final DocumentModel parent, Resource site) throws ClientException, IllegalArgumentException {
        if (parent == null) {
            throw new IllegalArgumentException("document model can not be null.");
        }
        CoreSession session = parent.getCoreSession();
        DocumentModelList children = session.getChildren(parent.getRef());
        StringBuilder result = null;
        if (children != null) {
            result = new StringBuilder();

            result.append("[");
            int i = 0;
            for (DocumentModel doc : children) {
                if (i > 0) {
                    result.append(",");
                }
                result.append("{").append("\"text\":").append("\"");
                if (doc.hasSchema("page")) {
                    StringBuilder url = new StringBuilder();
                    if (site != null) { // TODO
                        url.append(site.getPath());
                    }
                    url.append(buildEndUrl(doc));
                    result.append(getHref(url.toString(), doc.getName()));
                } else if (canGetPreview(doc)) {
                    StringBuilder url = new StringBuilder();
                    if (site != null) { // TODO
                        url.append(site.getPath());
                    }
                    url.append(buildEndUrl(doc));
                    result.append(getHref(url.toString(), doc.getName()));
                } else if (Docs.EXTERNAL_URL.type().equals(doc.getType())) {
                    StringBuilder url = new StringBuilder();
                    url.append(buildEndUrl(doc));
                    result.append(getHref(url.toString(), doc.getName()));
                } else {
                    result.append(doc.getName());
                }
                result.append("\"");
                if (session.hasChildren(doc.getRef())) {
                    result.append(",\"expanded\": true,\"children\":");
                    result.append(getTreeview(doc, site));
                }
                result.append("}");
                i++;
            }
            result.append("]");

        }
        
        return result.toString();
    }
    
    /**
     * TODO uni tests
     * @param doc
     * @return
     * @throws ClientException
     */
    public static String buildEndUrl(DocumentModel doc) throws ClientException {
        StringBuilder url = new StringBuilder();
        CoreSession session = doc.getCoreSession();
        DocumentModel parent = session.getParentDocument(doc.getRef());
        if (doc.hasSchema("page")) {
            // TODO improve
            url.append("/id/").append(doc.getId());
        } else if (Docs.EXTERNAL_URL.type().equals(doc.getType())) {
            url.append((String) doc.getPropertyValue("exturl:url"));
        } else if (canGetPreview(doc)) {
            DocumentModel pageDoc = parent.getCoreSession().getDocument(parent.getParentRef());
            url.append("/id/").append(pageDoc.getId());
            url.append("/doc/").append(doc.getId());
            url.append("/@blob/preview");
        } else {
            throw new UnsupportedOperationException("Unable to generate URL for document '" + doc.getPathAsString() + "' of type " + doc.getType());
        }
        return url.toString();
    }

    public static boolean canGetPreview(final DocumentModel doc) throws ClientException {
        DocumentModel parent = doc.getCoreSession().getParentDocument(doc.getRef());
        return "Folder".equals(parent.getType())
                && parent.getCoreSession().getDocument(parent.getParentRef()).hasSchema("page")
                && doc.getAdapter(BlobHolder.class) != null;
    }
    
    private static String getHref(String url, String text) {
        StringBuilder result = new StringBuilder();
        result.append("<a href='");
        result.append(url);
        result.append("'>").append(text).append("</a>");
        return result.toString();
    }

    public static PageProvider<DocumentModel> getLatestUploadsPageProvider(DocumentModel doc, long pageSize) throws Exception {
        PageProviderService ppService = Framework.getService(PageProviderService.class);
        List<SortInfo> sortInfos = null;
        Map<String, Serializable> props = new HashMap<String, Serializable>();
        props.put(LatestUploadsPageProvider.PARENT_DOCUMENT_PROPERTY,
                (Serializable) LabsSiteUtils.getSiteTree(LabsSiteUtils.getParentSite(doc)));
        @SuppressWarnings("unchecked")
        PageProvider<DocumentModel> pp = (PageProvider<DocumentModel>) ppService.getPageProvider(
                LATEST_UPLOADS_PAGEPROVIDER, sortInfos, new Long (pageSize), null, props, "");
        return pp;
    }

}
