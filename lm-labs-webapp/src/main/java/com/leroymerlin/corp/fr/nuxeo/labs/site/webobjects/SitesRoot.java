package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects;

import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.rendering.api.RenderingEngine;
import org.nuxeo.ecm.platform.rendering.fm.FreemarkerEngine;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.exceptions.WebResourceNotFoundException;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.common.freemarker.BytesFormatTemplateMethod;
import com.leroymerlin.common.freemarker.DateInWordsMethod;
import com.leroymerlin.common.freemarker.UserFullNameTemplateMethod;
import com.leroymerlin.corp.fr.nuxeo.freemarker.BreadcrumbsArrayTemplateMethod;
import com.leroymerlin.corp.fr.nuxeo.freemarker.LatestUploadsPageProviderTemplateMethod;
import com.leroymerlin.corp.fr.nuxeo.freemarker.PageEndUrlTemplateMethod;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteManagerException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteWebAppUtils;

@WebObject(type = "sitesRoot")
@Produces("text/html; charset=UTF-8")
@Path("/labssites")
public class SitesRoot extends ModuleRoot {

    private static final Log log = LogFactory.getLog(SitesRoot.class);


    /*
     * (non-Javadoc)
     *
     * @see
     * org.nuxeo.ecm.webengine.model.impl.AbstractResource#initialize(java.lang
     * .Object[])
     */
    @Override
    protected void initialize(Object... args) {
        super.initialize(args);
        // Add global fm variables
        // WARNING : these are GLOBAL vars, try to avoid using this trick (DMR)
        RenderingEngine rendering = getContext().getEngine()
                .getRendering();
        if (rendering instanceof FreemarkerEngine) {
            FreemarkerEngine fm = (FreemarkerEngine) rendering;
            fm.setSharedVariable("bytesFormat", new BytesFormatTemplateMethod());
            fm.setSharedVariable("breadcrumbsDocs",
                    new BreadcrumbsArrayTemplateMethod());
            fm.setSharedVariable("pageEndUrl", new PageEndUrlTemplateMethod());
            fm.setSharedVariable("latestUploadsPageProvider",
                    new LatestUploadsPageProviderTemplateMethod());
            fm.setSharedVariable("userFullName",
                    new UserFullNameTemplateMethod());
            fm.setSharedVariable("dateInWords", new DateInWordsMethod());
        }
    }

    @GET
    public Object doGetDefaultView() {
        return getView("index");
    }

    @Path("{url}")
    public Object doGetSite(@PathParam("url") final String pURL) {
        CoreSession session = getContext().getCoreSession();
        SiteManager sm = getSiteManager();
        try {
            LabsSite site = sm.getSite(session, pURL);
            return newObject("LabsSite", site.getDocument());
        } catch (ClientException e) {
            throw WebException.wrap(e);
        } catch (SiteManagerException e) {
            return Response.ok()
                    .status(404)
                    .build();
        }
    }


    public List<LabsSite> getLabsSites() throws ClientException {
        return getSiteManager().getAllSites(ctx.getCoreSession());
    }

    @POST
    public Response doPost(@FormParam("labsSiteTitle") String pTitle,
            @FormParam("labsSiteURL") String pURL,
            @FormParam("labsSiteDescription") String pDescription,
            @FormParam("labssiteId") String pId) {
        CoreSession session = ctx.getCoreSession();

        SiteManager sm = getSiteManager();
        try {
            LabsSite labSite = sm.createSite(session, pTitle, pURL);
            labSite.setURL(pURL);
            session.saveDocument(labSite.getDocument());
            session.save();
            return redirect(getPath() + "/" + labSite.getURL());
        } catch (SiteManagerException e) {
            return redirect(getPath() + "?message_error=" + e.getMessage());
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }

    }

    private SiteManager getSiteManager() {
        try {
            return Framework.getService(SiteManager.class);
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.nuxeo.ecm.webengine.model.impl.ModuleRoot#handleError(javax.ws.rs
     * .WebApplicationException)
     */
    @Override
    public Object handleError(WebApplicationException e) {
        if (e instanceof WebResourceNotFoundException) {
            String fileName = "error/error_404.ftl";
            log.debug(fileName);
            return Response.status(404)
                    .entity(getTemplate(fileName))
                    .build();
        } else {
            log.info("No error handling for class " + e.getClass()
                    .getName());
            log.error(e.getMessage(), e);
            return super.handleError(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.nuxeo.ecm.webengine.model.impl.ModuleRoot#getLink(org.nuxeo.ecm.core
     * .api.DocumentModel)
     */
    @Override
    public String getLink(DocumentModel doc) {
        try {
            SiteDocument sd = doc.getAdapter(SiteDocument.class);
            return new StringBuilder().append(getPath())
                    .append("/")
                    .append(sd.getSite()
                            .getDocument()
                            .getAdapter(LabsSite.class)
                            .getURL())
                    .append(LabsSiteWebAppUtils.buildEndUrl(doc))
                    .toString();
        } catch (UnsupportedOperationException e) {
            throw WebException.wrap(e);
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }
    }
}
