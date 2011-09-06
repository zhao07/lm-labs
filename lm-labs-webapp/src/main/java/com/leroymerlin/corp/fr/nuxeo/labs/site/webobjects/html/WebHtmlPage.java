package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.html;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.platform.rendering.api.RenderingEngine;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;

import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlPage;
import com.leroymerlin.corp.fr.nuxeo.labs.site.html.HtmlSection;

@WebObject(type = "HtmlPage")
public class WebHtmlPage extends DocumentObject {

    @Override
    public void initialize(Object... args) {
        super.initialize(args);

        RenderingEngine rendering = ctx.getEngine()
                .getRendering();
        rendering.setSharedVariable("page", getPage());
    }

    @POST
    @Override
    public Response doPost() {

        try {
            FormData form = ctx.getForm();
            String title = form.getString("title");
            String description = form.getString("description");

            HtmlSection section = getPage().addSection();
            section.setTitle(title);
            section.setDescription(description);

            saveDocument();

            return redirect(getPath() + "/@views/edit");
        } catch (ClientException e) {
            return Response.serverError()
                    .build();
        }

    }

    @Path("s/{index}")
    public Object getSection(@PathParam("index") int sectionIndex) {
        try {
            HtmlSection section = getPage().section(sectionIndex);
            return newObject("HtmlSection", doc, section);
        } catch (ClientException e) {
            return Response.serverError()
                    .build();
        }
    }

    private void saveDocument() throws ClientException {
        CoreSession session = ctx.getCoreSession();
        session.saveDocument(doc);
        session.save();
    }

    private HtmlPage getPage() {
        return doc.getAdapter(HtmlPage.class);
    }

}
