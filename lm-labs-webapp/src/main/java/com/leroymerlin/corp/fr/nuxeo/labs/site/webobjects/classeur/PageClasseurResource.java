package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.classeur;

import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.Resource;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.exceptions.WebResourceNotFoundException;

import com.leroymerlin.corp.fr.nuxeo.labs.site.classeur.PageClasseur;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Docs;
import com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.NotifiablePageResource;

@WebObject(type = "PageClasseur", superType = "LabsPage")
public class PageClasseurResource extends NotifiablePageResource {

    private static final Log LOG = LogFactory.getLog(PageClasseurResource.class);

    private PageClasseur classeur;

    @Override
    public void initialize(Object... args) {
        super.initialize(args);
        classeur = doc.getAdapter(PageClasseur.class);
        ctx.getEngine().getRendering().setSharedVariable("classeur", classeur);
    }

    @GET
    public Object doGet() {
        return getView("index");
    }

    @POST
    @Override
    public Response doPost() {
        FormData form = ctx.getForm();
        String folderTitle = form.getString("folderName");
        if (!StringUtils.isEmpty(folderTitle)) {
            try {
                classeur.addFolder(folderTitle);
                getCoreSession().save();
                return Response.status(Status.OK).build();
            } catch (ClientException e) {
                return Response.serverError().status(Status.FORBIDDEN).entity(
                        e.getMessage()).build();
            }

        } else {
            return Response.serverError().status(Status.FORBIDDEN).entity(
                    "Folder name is empty").build();
        }
    }

    @POST
    @Path("@rename/{id}")
    public Response doRename(@PathParam("id") String idRef) {
        FormData form = ctx.getForm();
        String folderTitle = form.getString("folderName");
        if (!StringUtils.isEmpty(folderTitle)) {
            try {
                classeur.renameFolder(idRef, folderTitle);
                getCoreSession().save();
                return Response.status(Status.OK).build();
            } catch (ClientException e) {
                return Response.serverError().status(Status.FORBIDDEN).entity(
                        e.getMessage()).build();
            }

        } else {
            return Response.serverError().status(Status.FORBIDDEN).entity(
                    "Folder name is empty").build();
        }
    }

    @Path(value = "{path}")
    @Override
    public Resource traverse(@PathParam("path") String path) {
        try {
            PathRef pathRef = new PathRef(doc.getPath().append(path).toString());
            DocumentModel subDoc = ctx.getCoreSession().getDocument(pathRef);
            if (Docs.pageDocs().contains(Docs.fromString(subDoc.getType()))) {
                return (DocumentObject) ctx.newObject(subDoc.getType(), subDoc);
            } else if (Docs.PAGECLASSEURFOLDER.type().equals(subDoc.getType())) {
                return newObject("PageClasseurFolder", subDoc);
            } else {
                throw new WebResourceNotFoundException("Unknow sub-type for a PageClasseur: " + subDoc.getType());
            }
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }

    }

    public BlobHolder getBlobHolder(final DocumentModel document) {
        return document.getAdapter(BlobHolder.class);
    }

    @DELETE
    @Path("bulk")
    public Response doBulkDelete(@QueryParam("id") List<String> ids) {
        final String logPrefix = "<doBulkDelete> ";
        LOG.debug(logPrefix);
        try {
            boolean removed = false;
            for (String id : ids) {
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
