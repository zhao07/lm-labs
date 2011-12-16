package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.classeur;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringEscapeUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.Resource;
import org.nuxeo.ecm.webengine.model.WebObject;

import com.leroymerlin.corp.fr.nuxeo.labs.site.classeur.PageClasseurFolder;

@WebObject(type = "PageClasseurFolder")
public class PageClasseurFolderResource extends DocumentObject {
    
    private PageClasseurFolder folder;

    @Override
    public void initialize(Object... args) {
        super.initialize(args);
        folder = doc.getAdapter(PageClasseurFolder.class);
    }

    @POST
    @Override
    public Response doPost() {

        FormData form = ctx.getForm();
        if (form.isMultipartContent()) {
            String desc = form.getString("description");
            Blob blob = form.getFirstBlob();
            try {
                blob.persist();
                if (blob.getLength() > 0) {
                    folder.addFile(blob, desc);
                    getCoreSession().save();
                }
                return redirect(prev.getPath());
            } catch (Exception e) {
                return Response.serverError()
                        .status(Status.FORBIDDEN)
                        .entity(e.getMessage())
                        .build();
            }
        }
        return Response.serverError()
                .status(Status.FORBIDDEN)
                .entity("Invalid form")
                .build();
    }

    @Override
    public Object doGet() {
        return redirect(prev.getPath());
    }

    @Override
    public Response getDelete() {
        try {
            if (folder.setAsDeleted()) {
                ctx.getCoreSession().save();
            }
        } catch (ClientException e) {
            throw WebException.wrap("Failed to set as 'deleted' for document " + doc.getPathAsString(), e);
        }
        if (prev != null) { // show parent ? TODO: add getView(method) to be able to change the view method
            return redirect(prev.getPath());
        }
        return redirect(ctx.getBasePath());
    }
    
    @GET
    @Path("@permanentDelete")
    public Response doPermanentDelete() {
        return doDelete();
    }

    @Path(value = "{path}")
    @Override
    public Resource traverse(@PathParam("path") String path) {
        try {
            PathRef pathRef = new PathRef(doc.getPath().append(StringEscapeUtils.escapeHtml(path)).toString());
            DocumentModel file = ctx.getCoreSession().getDocument(pathRef);
            return newObject("ClasseurElement", file);
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }

    }

}
