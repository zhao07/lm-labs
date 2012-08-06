package com.leroymerlin.corp.fr.nuxeo.labs.site;

import org.nuxeo.ecm.core.api.ClientException;

public interface LabsCommentable {
    
    boolean isCommentable() throws ClientException;
    
    void setCommentable(boolean isCommentable) throws ClientException;

}
