package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.security.SecurityConstants;
import org.nuxeo.ecm.core.rest.DocumentObject;
import org.nuxeo.ecm.platform.rendering.api.RenderingEngine;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.ecm.webengine.model.exceptions.WebResourceNotFoundException;
import org.nuxeo.ecm.webengine.model.exceptions.WebSecurityException;
import org.nuxeo.ecm.webengine.model.impl.ModuleRoot;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.common.freemarker.BytesFormatTemplateMethod;
import com.leroymerlin.common.freemarker.DateInWordsMethod;
import com.leroymerlin.common.freemarker.UserFullNameTemplateMethod;
import com.leroymerlin.corp.fr.nuxeo.freemarker.LatestUploadsPageProviderTemplateMethod;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.exception.SiteManagerException;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.CommonHelper;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteUtils;
import com.leroymerlin.corp.fr.nuxeo.portal.usermanager.LMNuxeoPrincipal;

@WebObject(type = "sitesRoot")
@Produces("text/html; charset=UTF-8")
@Path("/labssites")
public class SitesRoot extends ModuleRoot {

    private static final Log log = LogFactory.getLog(SitesRoot.class);

    private static final String[] MESSAGES_TYPE = new String[] { "error", "info", "success", "warning" };

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
        rendering.setSharedVariable("bytesFormat", new BytesFormatTemplateMethod());
        rendering.setSharedVariable("latestUploadsPageProvider", new LatestUploadsPageProviderTemplateMethod());
        rendering.setSharedVariable("userFullName", new UserFullNameTemplateMethod());
        rendering.setSharedVariable("dateInWords", new DateInWordsMethod());
        rendering.setSharedVariable("site", null);
        rendering.setSharedVariable("Common", new CommonHelper());
        List<LabsSite> undeletedLabsSites = new ArrayList<LabsSite>();
        List<LabsSite> deletedLabsSites = new ArrayList<LabsSite>();
        try {
            List<LabsSite> labsSites = getLabsSites();
            undeletedLabsSites = getLabsSitesUndeleted(labsSites);
            deletedLabsSites = getLabsSitesDeleted(labsSites);
        } catch (ClientException e) {
            log.error("Impossible to get the sites!", e);
        }
        rendering.setSharedVariable("undeletedLabsSites", undeletedLabsSites);
        rendering.setSharedVariable("deletedLabsSites", deletedLabsSites);
    }
    
    private List<LabsSite> getLabsSitesUndeleted(List<LabsSite> pOrigin) throws ClientException{
        List<LabsSite> result = new ArrayList<LabsSite>();
        for(LabsSite labsSite:pOrigin){
            if(!labsSite.isDeleted()){
                result.add(labsSite);
            }
        }
        return result;
    }
    
    private List<LabsSite> getLabsSitesDeleted(List<LabsSite> pOrigin) throws ClientException{
        List<LabsSite> result = new ArrayList<LabsSite>();
        for(LabsSite labsSite:pOrigin){
            if(labsSite.isDeleted()){
                result.add(labsSite);
            }
        }
        return result;
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
            throw new WebResourceNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * Needed by OpenSocial gadgets.
     * 
     * @param id
     * @return
     * @throws URIException
     * @throws URISyntaxException
     */
    @Path("id/{id}")
    public Object doGetPageById(@PathParam("id") String id) {
        CoreSession session = getContext().getCoreSession();
        DocumentModel document;
        try {
            document = session.getDocument(new IdRef(id));
            return (DocumentObject) ctx.newObject(document.getType(), document);
        } catch (ClientException e) {
            throw new WebResourceNotFoundException(e.getMessage(), e);
        }
    }

    private List<LabsSite> getLabsSites() throws ClientException {
        String user = null;
        if (((LMNuxeoPrincipal) ctx.getPrincipal()).isAnonymous()) {
            user = SecurityConstants.EVERYONE;
        } else {
            user = getContext().getPrincipal()
                    .getName();
        }
        CoreSession coreSession = ctx.getCoreSession();
        List<LabsSite> newAllSites = new ArrayList<LabsSite>();
        for (LabsSite site : getSiteManager().getAllSites(coreSession)) {
            if (!(LabsSiteUtils.isOnlyRead(site.getDocument(), user) && !site.isVisible())) {
                newAllSites.add(site);
            }
        }
        return newAllSites;
    }

    @POST
    public Response doPost(@FormParam("labsSiteTitle") String pTitle, @FormParam("labsSiteURL") String pURL,
            @FormParam("labsSiteDescription") String pDescription, @FormParam("labssiteId") String pId) {
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
        } else if (e instanceof WebSecurityException) {
            return Response.status(401)
                    .entity(getTemplate("error/error_401.ftl"))
                    .type("text/html")
                    .build();
        } else {

            return Response.status(500)
                    .entity(getTemplate("error/labserror_500.ftl").arg("trace", getStackTrace(e)))
                    .type("text/html")
                    .build();
        }
    }

    private static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
    }

    /**
     * Returns a Map containing all "flash" messages
     * 
     * @return
     */
    public Map<String, String> getMessages() {
        Map<String, String> messages = new HashMap<String, String>();
        FormData form = ctx.getForm();
        for (String type : MESSAGES_TYPE) {
            String message = form.getString("message_" + type);
            if (StringUtils.isNotBlank(message)) {
                messages.put(type, message);
            }
        }
        return messages;

    }

    @Override
    public String getLink(DocumentModel document) {
        SiteDocument siteDocument = document.getAdapter(SiteDocument.class);
        try {
            return new StringBuilder().append(getPath())
                    .append("/")
                    .append(siteDocument.getResourcePath())
                    .toString();
        } catch (ClientException e) {
            return getPath();
        }
    }
}
