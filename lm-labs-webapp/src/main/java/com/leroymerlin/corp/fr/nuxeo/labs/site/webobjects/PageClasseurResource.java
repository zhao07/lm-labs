package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.Filter;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.platform.filemanager.api.FileManager;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.Resource;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.PageClasseurUtils;

@WebObject(type = "PageClasseur")
public class PageClasseurResource extends Page {

    private class TitleFilter implements Filter {

        private final String title;

        public TitleFilter(String title) {
            this.title = title;
        }

        public boolean accept(DocumentModel docModel) {
            try {
                return StringUtils.equals(docModel.getTitle(), this.title);
            } catch (ClientException e) {
                return false;
            }
        }

    }
    
    private static final Log LOG = LogFactory.getLog(PageClasseurResource.class);
    
    @GET public Object doGet() {
        return getView("index");
    }
    
    public List<DocumentModel> getChildren(DocumentModel parent) {
        return getChildren(parent, "dc:title", "ASC", 0, 0);
    }

    public List<DocumentModel> getChildren(DocumentModel parent, String orderBy, String orderDir, long limit, long offset) {
        try {
            StringBuilder sb = new StringBuilder("SELECT * From Document");
            sb.append(" WHERE ecm:parentId = '").append(parent.getId()).append("'");
            sb.append(" AND ecm:isProxy = 0 AND ecm:isCheckedInVersion = 0");
            sb.append(" AND ecm:currentLifeCycleState <> 'deleted'");
            sb.append(" ORDER BY ").append(orderBy);
            DocumentModelList list = getCoreSession().query(sb.toString(), null, limit, offset, true);
            if ("DESC".equals(orderDir)) {
                Collections.reverse(list);
            }
            return list;
        } catch (ClientException e) {
            return new ArrayList<DocumentModel>();
        }
    }

    @POST
    @Override
    public Response doPost() {
        LOG.debug("POST doPost");
        FormData form = ctx.getForm();
        if (form.isMultipartContent()) {
            String desc = form.getString("description");
            Blob blob = form.getFirstBlob();
            if (blob == null) {
                throw new IllegalArgumentException("Could not find any uploaded file");
            } else {
                blob.setFilename(StringUtils.deleteWhitespace(blob.getFilename()));
                try {
                    blob.persist();
                    CoreSession coreSession = ctx.getCoreSession();
                    DocumentModel fileDoc = Framework.getService(FileManager.class).createDocumentFromBlob(
                            coreSession, blob, doc.getPathAsString(), false,
                            blob.getFilename());
                    if (!StringUtils.isEmpty(desc)) {
                        fileDoc.setPropertyValue("dc:description", desc);
                    }
                    return Response.ok("Upload file ok", MediaType.TEXT_PLAIN).build();
                } catch (Exception e) {
                    return Response.serverError().status(Status.FORBIDDEN).entity(
                            e.getMessage()).build();
                }
            }
        }
        return Response.serverError().status(Status.FORBIDDEN).entity("ERROR").build();
    }
    
    @POST
    @Path("{folderId}/addFile")
    public Response doPostFolder(@PathParam("folderId") String folderId) {
        LOG.debug("POST doPostFolder");
        FormData form = ctx.getForm();
        if (form.isMultipartContent()) {
            String desc = form.getString("description");
            Blob blob = form.getFirstBlob();
            try {
                PageClasseurUtils.importBlobInPageClasseur(doc, folderId, desc, blob);
                return Response.ok("Upload file ok", MediaType.TEXT_PLAIN).build();
//            } catch (IllegalArgumentException e) {
//                return Response.serverError().status(Status.FORBIDDEN).entity(e.getMessage()).build();
//            } catch (IOException e) {
//                return Response.serverError().status(Status.FORBIDDEN).entity(e.getMessage()).build();
//            } catch (ClientException e) {
//                return Response.serverError().status(Status.FORBIDDEN).entity(e.getMessage()).build();
            } catch (Exception e) {
                return Response.serverError().status(Status.FORBIDDEN).entity(e.getMessage()).build();
            }
        }
        return Response.serverError().status(Status.FORBIDDEN).entity("Invalid form").build();
    }

    @POST
    @Path("addFolder")
    public Response doAddFolder(@FormParam("folderName") String folderName) {
        LOG.debug("POST doAddFolder " + folderName);
        if (!StringUtils.isEmpty(folderName)) {
            try {
                DocumentModelList children = getCoreSession().getChildren(doc.getRef(), "Folder", new TitleFilter(folderName), null);
                if (!children.isEmpty()) {
                    return Response.serverError().status(Status.FORBIDDEN).entity("folder '" + folderName + "' already exists.").build();
                }
                DocumentModel folder = getCoreSession().createDocumentModel(doc.getPathAsString(), folderName, "Folder");
                folder.setPropertyValue("dc:title", folderName);
                folder = getCoreSession().createDocument(folder);
                getCoreSession().save();
                return Response.ok("Folder created", MediaType.TEXT_PLAIN).build();
            } catch (ClientException e) {
                LOG.error(e.getMessage());
                return Response.serverError().status(Status.FORBIDDEN).entity(
                        e.getMessage()).build();
            }
        }
        return Response.serverError().status(Status.FORBIDDEN).entity("ERROR").build();
    }
    
    public BlobHolder getBlobHolder(final DocumentModel document) {
        return document.getAdapter(BlobHolder.class);
    }
    
    private DocumentModelList getChild(String docId) throws ClientException {
        StringBuilder sb = new StringBuilder("SELECT * From Document");
        sb.append(" WHERE ecm:path STARTSWITH '").append(doc.getPathAsString()).append("'");
        sb.append(" AND ecm:uuid = '").append(docId).append("'");
        DocumentModelList list = getCoreSession().query(sb .toString());
        return list;
    }
    
    @Path("doc/{docId}")
    public Object doGetChild(@PathParam("docId") String docId) {
        final String logPrefix = "<doGetChild> ";
        LOG.debug(logPrefix + docId);
        DocumentModelList list;
        try {
            list = getChild(docId);
            if (list.isEmpty()) {
                return Response.status(Status.NOT_FOUND).build();
            }
            return newObject("ClasseurElement", list.get(0));
        } catch (ClientException e) {
            LOG.error(e.getMessage());
            return Response.status(Status.FORBIDDEN).build();
        }
    }


    @DELETE
    @Path("bulk")
    public Response doBulkDelete(@QueryParam("id") List<String> ids) {
        final String logPrefix = "<doBulkDelete> ";
        LOG.debug(logPrefix);
        try {
            boolean removed = false;
            for (String id: ids) {
                LOG.debug(id);
                IdRef idRef = new IdRef(id);
                if (getCoreSession().exists(idRef)) {
                    getCoreSession().removeDocument(idRef);
                    removed = true;
                }
            }
            if (removed) {
                getCoreSession().save();
            }
        } catch (ClientException e) {
            LOG.error(e.getMessage());
            return Response.serverError().status(Status.NOT_MODIFIED).entity(
                    e.getMessage()).build();
        }
        return Response.status(Status.NO_CONTENT).build();
    }
    
}
