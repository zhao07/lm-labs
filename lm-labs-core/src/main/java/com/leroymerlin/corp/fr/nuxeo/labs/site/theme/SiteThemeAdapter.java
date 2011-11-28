package com.leroymerlin.corp.fr.nuxeo.labs.site.theme;

import static org.nuxeo.ecm.platform.picture.api.ImagingConvertConstants.OPERATION_RESIZE;
import static org.nuxeo.ecm.platform.picture.api.ImagingConvertConstants.OPTION_RESIZE_HEIGHT;
import static org.nuxeo.ecm.platform.picture.api.ImagingConvertConstants.OPTION_RESIZE_WIDTH;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.blobholder.BlobHolder;
import org.nuxeo.ecm.core.api.blobholder.SimpleBlobHolder;
import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.platform.picture.api.ImageInfo;
import org.nuxeo.ecm.platform.picture.api.ImagingService;
import org.nuxeo.runtime.api.Framework;

import com.leroymerlin.corp.fr.nuxeo.labs.site.utils.LabsSiteConstants.Schemas;

public class SiteThemeAdapter implements SiteTheme {

    private static final Log LOG = LogFactory.getLog(SiteThemeAdapter.class);
    
    private static final String PROPERTY_NAME = "dc:title";
    private static final String PROPERTY_BANNER_BLOB = Schemas.SITETHEME.prefix() + ":banner";
    private static final String PROPERTY_LOGO_BLOB = Schemas.SITETHEME.prefix() + ":logo";
    private static final String PROPERTY_LOGO_POSX = Schemas.SITETHEME.prefix() + ":logo_posx";
    private static final String PROPERTY_LOGO_POSY = Schemas.SITETHEME.prefix() + ":logo_posy";
    private static final String PROPERTY_LOGO_RESIZE_RATIO = Schemas.SITETHEME.prefix() + ":logo_resize_ratio";

    private final DocumentModel doc;

    public SiteThemeAdapter(DocumentModel doc) {
        this.doc = doc;
    }

    public DocumentModel getDocument() {
        return doc;
    }

    @Override
    public String getName() throws ClientException {
        String name = (String) doc.getTitle();
        return name;
    }

    @Override
    public void setName(String name) throws ClientException {
        doc.setPropertyValue(PROPERTY_NAME, name);
    }
    
    @Override
    public Blob getBanner() throws ClientException {
        return (Blob) doc.getPropertyValue(PROPERTY_BANNER_BLOB);
    }

    @Override
    public void setBanner(Blob blob) throws ClientException {
        doc.setPropertyValue(PROPERTY_BANNER_BLOB, (Serializable) blob);
    }

    @Override
    public Blob getLogo() throws ClientException {
        Blob blob = (Blob) doc.getPropertyValue(PROPERTY_LOGO_BLOB);
        return blob;
    }

    @Override
    public void setLogo(Blob blob) throws ClientException {
        doc.setPropertyValue(PROPERTY_LOGO_BLOB, (Serializable) blob);
    }

    @Override
    public int getLogoPosX() throws ClientException {
        return ((Long) doc.getPropertyValue(PROPERTY_LOGO_POSX)).intValue();
    }

    @Override
    public void setLogoPosX(int pos) throws ClientException {
        doc.setPropertyValue(PROPERTY_LOGO_POSX, new Long(pos));
        
    }

    @Override
    public int getLogoPosY() throws ClientException {
        return ((Long) doc.getPropertyValue(PROPERTY_LOGO_POSY)).intValue();
    }

    @Override
    public void setLogoPosY(int pos) throws ClientException {
        doc.setPropertyValue(PROPERTY_LOGO_POSY, new Long(pos));
    }

    @Override
    public int getLogoResizeRatio() throws ClientException {
        return ((Long) doc.getPropertyValue(PROPERTY_LOGO_RESIZE_RATIO)).intValue();
    }

    @Override
    public void setLogoResizeRatio(int pos) throws ClientException {
        doc.setPropertyValue(PROPERTY_LOGO_RESIZE_RATIO, new Long(pos));
    }

    @Override
    public int getLogoWidth() throws ClientException {
        Blob blob = (Blob) doc.getPropertyValue(PROPERTY_LOGO_BLOB);
        int logoResizeRatio = getLogoResizeRatio();
        ImagingService imagingService = null;
        try {
            imagingService = Framework.getService(ImagingService.class);
            if (imagingService == null) {
                return 0;
            }
            final ImageInfo imageInfo = imagingService.getImageInfo(blob);
            if (imageInfo == null) {
                LOG.error("image infos are null");
                return 0;
            }
            return (imageInfo.getWidth() * logoResizeRatio / 100);
        } catch (Exception e1) {
            LOG.error("Unable to get Imaging Service.");
        }
        return 0;
    }

}
