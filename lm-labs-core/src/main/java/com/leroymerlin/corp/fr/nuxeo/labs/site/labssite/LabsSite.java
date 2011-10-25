/**
 *
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.labssite;

import java.util.List;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import com.leroymerlin.corp.fr.nuxeo.labs.site.Page;

/**
 * @author fvandaele
 *
 */
public interface LabsSite extends Page {

    /**
     * The last url part to get this site. This MUST be
     * unique amongst all sites
     * @return
     * @throws ClientException
     */
    String getURL() throws ClientException;

    /**
     * Set the url of the site. It SHOULD throw an exception
     * if the URL is not unique
     * @param pURL
     * @throws ClientException
     */
    void setURL(String pURL) throws ClientException;

    /**
     * The underlying Site document
     * @return
     */
    DocumentModel getDocumentModel();

    /**
     * Returns a Blob containing the logo of
     * the site (not the banner : see ThemeManager)
     * @return
     * @throws ClientException
     */
    Blob getLogo() throws ClientException;


    void setLogo(Blob pBlob) throws ClientException;


    DocumentModel getIndexDocument() throws ClientException;


    /**
     * Returns all the page of the site. Pages are
     * document that are renderable with a web
     * view
     * @return
     * @throws ClientException
     */
    List<Page> getAllPages() throws ClientException;

    /**
     * Returns the base document of the tree
     *
     * @return
     * @throws ClientException
     */
    DocumentModel getTree() throws ClientException;

    /**
     * Return the theme manager for this site
     * @return
     * @throws ClientException
     */
    SiteThemeManager getSiteThemeManager() throws ClientException;

    /**
     * Returns the base document for assets management
     * @return
     */
    DocumentModel getAssetsDoc() throws ClientException;

    List<Page> getAllDeletedPages() throws ClientException;

}
