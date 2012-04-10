package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.common.utils.FileUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.WebException;
import org.nuxeo.ecm.webengine.forms.FormData;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebObject;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;
import com.leroymerlin.corp.fr.nuxeo.labs.site.SiteDocument;
import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;
import com.leroymerlin.corp.fr.nuxeo.labs.site.services.LabsThemeManager;
import com.leroymerlin.corp.fr.nuxeo.labs.site.theme.SiteTheme;
import com.leroymerlin.corp.fr.nuxeo.labs.site.theme.SiteThemeAdapter;
import com.leroymerlin.corp.fr.nuxeo.labs.site.theme.ThemePropertiesManage;
import com.leroymerlin.corp.fr.nuxeo.labs.site.theme.bean.ThemeProperty;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.CommonHelper;
import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteWebAppUtils;

@WebObject(type = "SiteTheme")
@Produces("text/html; charset=UTF-8")
public class SiteThemeResource extends PageResource {

    private static final String THE_THEMES_SHOULD_NOT_BE_EMPTY = "The themes should not be empty !";


    private static final Log LOG = LogFactory.getLog(SiteThemeResource.class);

    private LabsSite site;

    private SiteTheme theme;

    @Override
    public void initialize(Object... args) {
        assert args != null && args.length == 2;
        site = (LabsSite) args[0];
        theme = (SiteTheme) args[1];
        doc = theme.getDocument();
    }

    @Override
    public boolean isVisible() throws ClientException {
        return true;
    }

    @Override
    public Page getPage() throws ClientException {
        return null;
    }

    /**
     * @throws Exception
     */
    private LabsThemeManager getThemeService() {
        try {
            return Framework.getService(LabsThemeManager.class);
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getThemes() {
        LabsThemeManager themeService = getThemeService();
        List<String> entriesTheme = new ArrayList<String>();
        if (themeService != null) {
            entriesTheme = themeService.getThemeList(getModule().getRoot().getAbsolutePath());
        }
        if (entriesTheme.isEmpty()) {
            LOG.error(THE_THEMES_SHOULD_NOT_BE_EMPTY);
            LOG.error("Verify the package " + LabsSiteWebAppUtils.DIRECTORY_THEME);
        }
        return entriesTheme;
    }

    @GET
    public Template doGet() {
        return getView("index");
    }

    @DELETE
    @Path(value = "banner")
    public Response doDeleteBanner() {
        try {
            site.setBanner(null);
            CoreSession session = ctx.getCoreSession();
            session.save();
            return Response.ok(
                    "?message_success=label.labssites.banner.deleted",
                    MediaType.TEXT_PLAIN).status(Status.CREATED).build();
        } catch (ClientException e) {
            LOG.error(e);
            return Response.ok(
                    "?message_warning=label.labssites.banner.notDeleted",
                    MediaType.TEXT_PLAIN).status(Status.CREATED).build();
        }
    }
    
    @GET
    @Path("banner")
    public Response getImgBanner() throws ClientException {
        Blob blob = doc.getAdapter(SiteDocument.class).getSite().getThemeManager().getTheme().getBanner();
        if (blob != null) {
            return Response.ok().entity(blob).type(blob.getMimeType()).build();
        }
        return Response.noContent().build();
    }

    @POST
    @Path(value = "appearance")
    public Response doPostThemeTemplate() {
        FormData form = ctx.getForm();
        try {
            CoreSession session = ctx.getCoreSession();
            site.getTemplate().setTemplateName(form.getString("template"));
            String themeName = form.getString("theme");
            site.getThemeManager().setTheme(themeName);
            session.saveDocument(site.getDocument());
            session.save();
            return redirect(getPrevious().getPath() + "/@theme/" + themeName
                    + "?message_success=label.labssites.appearance.updated");
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }
    }

    @DELETE
    @Path(value = "propertyTheme/{key}")
    public Response doDeletePropertyTheme(@PathParam(value = "key") String key) {
        try {
            Map<String, ThemeProperty> properties = theme.getProperties();
            ThemeProperty prop = properties.get(key);
            if (prop != null) {
                prop.setValue(null);
                properties.put(key, prop);
                theme.setProperties(properties);
                CoreSession session = ctx.getCoreSession();
                session.saveDocument(theme.getDocument());
                session.save();
                return Response.ok().status(Status.OK).build();
            }
        } catch (Exception e) {
            return Response.notModified().build();
        }
        return Response.notModified().build();
    }
    
    @GET
    @Path(value = "resetCurrentTheme")
    public Response doResetCurrentTheme() {
        try {
            theme.setProperties(SiteThemeAdapter.EMPTY_PROPERTIES);
            CoreSession session = ctx.getCoreSession();
            session.saveDocument(theme.getDocument());
//            session.save();
            return Response.ok().status(Status.OK).build();
        } catch (Exception e) {
            return Response.notModified().build();
        }
    }

    @POST
    @Path(value = "parameters")
    public Response doPostParameters() {
        FormData form = ctx.getForm();
        String posx = form.getString("logo_posx");
        String logoAreaHeight = form.getString("logo_area_height");
        String posy = form.getString("logo_posy");
        String ratio = form.getString("resize_ratio");
        String style = form.getString("style");
        try {
            if (form.isMultipartContent()) {
                Blob logo = form.getBlob("logo");
                Blob banner = form.getBlob("banner");
                if (logo != null && !StringUtils.isEmpty(logo.getFilename())) {
                    theme.setLogo(logo);
                }
                if (banner != null
                        && !StringUtils.isEmpty(banner.getFilename())) {
                    theme.setBanner(banner);
                }
                if (StringUtils.isNotBlank(logoAreaHeight) && StringUtils.isNumeric(logoAreaHeight)) {
                    theme.setLogoAreaHeight(Integer.parseInt(logoAreaHeight));
                }
                if (StringUtils.isNotBlank(posx) && StringUtils.isNumeric(posx)) {
                    theme.setLogoPosX(Integer.parseInt(posx));
                }
                if (StringUtils.isNotBlank(posy) && StringUtils.isNumeric(posy)) {
                    theme.setLogoPosY(Integer.parseInt(posy));
                }
                if (StringUtils.isNotBlank(ratio)
                        && StringUtils.isNumeric(ratio)) {
                    theme.setLogoResizeRatio(Integer.parseInt(ratio));
                }
                theme.setStyle(style);
                theme.setProperties(extractProperties(form));
                CoreSession session = ctx.getCoreSession();
                session.saveDocument(theme.getDocument());
                session.save();
                return redirect(getPath()
                        + "?message_success=label.labssites.appearance.theme.edit.updated");
            }
        } catch (Exception e) {
            throw WebException.wrap(e);
        }
        return redirect(getPath()
                + "?message_warning=label.labssites.appearance.theme.edit.not_updated");
    }

    private Map<String, ThemeProperty> extractProperties(FormData form)
            throws Exception {
        Map<String, ThemeProperty> properties = new HashMap<String, ThemeProperty>();
        String baseFiledName = "Property";
        int cpt = 0;
        String sCpt = form.getString("cptProperties");
        if (StringUtils.isNotBlank(sCpt) && StringUtils.isNumeric(sCpt)) {
            cpt = Integer.parseInt(sCpt);
        }
        ThemeProperty prop = null;
        String value = null;
        for (int i = 0; i < cpt; i++) {
            prop = new ThemeProperty();
            prop.setKey(form.getString("key" + baseFiledName + i));
            prop.setLabel(form.getString("label" + baseFiledName + i));
            prop.setDescription(form.getString("description" + baseFiledName
                    + i));
            prop.setType(form.getString("type" + baseFiledName + i));
            value = form.getString("value" + baseFiledName + i);
            if (StringUtils.isNotBlank(value)) {
                prop.setValue(value);
            } else {
                prop.setValue(null);
            }
            prop.setOrderNumber(i);
            properties.put(prop.getKey(), prop);
        }

        return properties;
    }

    @POST
    @Path(value = "logoXY")
    public Response doPostLogoXY(@FormParam("posX") String posX,
            @FormParam("posY") String posY) {
        try {
            if (StringUtils.isBlank(posX) || StringUtils.isBlank(posY)) {
                return redirect(getPath()
                        + "?message_warning=label.labssites.appearance.theme.edit.logo.not_updated");
            }

            theme.setLogoPosX(Integer.parseInt(posX));
            theme.setLogoPosY(Integer.parseInt(posY));
            CoreSession session = ctx.getCoreSession();
            session.saveDocument(theme.getDocument());
            session.save();
            return redirect(getPath()
                    + "?message_success=label.labssites.appearance.theme.edit.logo.updated");
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }
    }

    @DELETE
    @Path(value = "logo")
    public Response doDeleteLogo() {
        try {
            theme.setLogo(null);
            CoreSession session = ctx.getCoreSession();
            session.saveDocument(theme.getDocument());
            session.save();
            return redirect(getPath()
                    + "?message_success=label.labssites.logo.updated");
        } catch (ClientException e) {
            throw WebException.wrap(e);
        }
    }

    @GET
    @Path(value = "logo")
    public Response getImgLogo() throws ClientException {
        Blob blob = theme.getLogo();
        if (blob == null) {
            return null;
        }
        return Response.ok().entity(blob).type(blob.getMimeType()).build();
    }

    public List<ThemeProperty> getThemeProperties() {
        List<ThemeProperty> properties = new ArrayList<ThemeProperty>();
        try {
            ThemePropertiesManage tpm = new ThemePropertiesManage(
                    theme.getProperties());
            String path = getModule().getRoot().getAbsolutePath()
                    + LabsSiteWebAppUtils.DIRECTORY_THEME + "/"
                    + theme.getName() + "/properties";
            File f = new File(path);
            if (!f.exists()) {
                path = getModule().getRoot().getAbsolutePath()
                        + LabsSiteWebAppUtils.DIRECTORY_THEME + "/"
                        + "/properties";
                f = new File(path);
            }
            if (!tpm.isLoaded(f.getAbsolutePath(), theme.getLastRead())) {
                if (f.exists()) {
                    tpm.loadProperties(new FileInputStream(f));
                    theme.setProperties(tpm.getProperties());
                    theme.setLastRead(Calendar.getInstance().getTimeInMillis());
                    getCoreSession().saveDocument(theme.getDocument());
                    getCoreSession().save();
                }
            }
            properties = CommonHelper.getThemeProperties(tpm);
        } catch (Exception e) {
            throw WebException.wrap(e);
        }
        return properties;
    }

}
