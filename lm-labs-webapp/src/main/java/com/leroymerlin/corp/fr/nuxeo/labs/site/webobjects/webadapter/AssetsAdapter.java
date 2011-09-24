/**
 *
 */
package com.leroymerlin.corp.fr.nuxeo.labs.site.webobjects.webadapter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.webengine.model.Resource;
import org.nuxeo.ecm.webengine.model.Template;
import org.nuxeo.ecm.webengine.model.WebAdapter;
import org.nuxeo.ecm.webengine.model.impl.DefaultAdapter;

import com.leroymerlin.corp.fr.nuxeo.labs.site.labssite.LabsSite;

@WebAdapter(name = "assets", type = "assetsAdapter", targetType = "LabsSite")
public class AssetsAdapter extends DefaultAdapter {


    @GET
    public Template doGet() throws ClientException {
        Resource resource = getAssetResource(getSite());
        return resource.getView("index");
    }

    private Resource getAssetResource(LabsSite site) throws ClientException {
        return ctx.newObject("AssetFolder", site.getAssetsDoc());
    }

    @Path("{path}")
    public Object doTraverse(@PathParam("path") String path) throws ClientException {
        AssetFolderResource res = (AssetFolderResource) getAssetResource(getSite());
        return res.traverse(path);
    }

    private LabsSite getSite() {
        return (LabsSite) ctx.getProperty("site");
    }

    @GET
    @Path("json")
    public Response doGetJson(@QueryParam("root") String root)
            throws ClientException {
        LabsSite site = (LabsSite) ctx.getProperty("site");

        if (site != null) {
            DocumentModel assetsDoc = site.getAssetsDoc();
            SiteDocumentTree tree = new SiteDocumentTree(ctx, assetsDoc);
            String result = "";
            if (root == null || "source".equals(root)) {
                tree.enter(ctx, "");
                result = tree.getTreeAsJSONArray(ctx);
            } else {
                result = tree.enter(ctx, root);
            }
            return Response.ok()
                    .entity(result)
                    .build();
        }
        return null;
    }

}
