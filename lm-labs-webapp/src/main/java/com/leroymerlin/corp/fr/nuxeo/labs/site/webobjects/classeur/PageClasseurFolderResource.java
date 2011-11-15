package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.classeur;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.Resource;
import org.nuxeo.ecm.webengine.model.WebObject;

import com.leroymerlin.corp.fr.nuxeo.labs.site.classeur.PageClasseur;
import com.leroymerlin.corp.fr.nuxeo.labs.site.classeur.PageClasseurFolder;
import com.leroymerlin.corp.fr.nuxeo.labs.site.notification.MailNotification;

@WebObject(type = "PageClasseurFolder")
public class PageClasseurFolderResource extends DocumentObject {
    
    private static final Log LOG = LogFactory.getLog(PageClasseurFolderResource.class);
    private PageClasseurFolder folder;
    private PageClasseur parent;

    @Override
    public void initialize(Object... args) {
        super.initialize(args);
        folder = doc.getAdapter(PageClasseurFolder.class);
        if (args.length > 1) {
            parent = ((DocumentModel) args[1]).getAdapter(PageClasseur.class);
        }
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


    @Path(value = "{path}")
    @Override
    public Resource traverse(@PathParam("path") String path) {
        try {
            PathRef pathRef = new PathRef(doc.getPath().append(path).toString());
            DocumentModel file = ctx.getCoreSession().getDocument(pathRef);
            return newObject("ClasseurElement", file, folder);
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }

    }

    @Override
    public Response doDelete() {
        try {
            parent.getDocument().getAdapter(MailNotification.class).setAsToBeNotified();
            doc.getCoreSession().saveDocument(parent.getDocument());
        } catch (ClientException e) {
            LOG.error("Unable to set " + parent.getDocument().getPathAsString() + " as 'to be notified'.");
        }
        return super.doDelete();
    }

}
