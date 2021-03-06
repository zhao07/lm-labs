<@extends src="/views/TemplatesBase/" + This.page.template.getTemplateName() + "/template.ftl">
  <#assign mySite=Common.siteDoc(Document).getSite() />
  <@block name="title">${mySite.title}-${This.document.title}</@block>

  <@block name="css">
      <@superBlock/>
      <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/wysiwyg_editor.css"/>
  </@block>


  <@block name="content">
      <#-- SIDEBAR AREA -->
      <div class="container-fluid">

      <div id="sidebar" class="sidebar">
        <#include "views/common/sidebar_area.ftl" />
      </div>


      <div class="content">

        <div class="row">
          <div  class="span12 columns">
            <#--assign Document = mySite.getIndexDocument() /-->
            <#include "views/common/description_area.ftl">

      <#assign Document = mySite.document />

        <#------------------------------------maxNbLabsNews------------------------>
        <#assign maxNbLabsNews = 5 />

          <#list Session.getChildren(mySite.getTree().ref) as root>
            <#if root.name != "welcome" && This.isAuthorizedToDisplay(root)>
	            <div id="bloc${root_index}" class="bloc welcome span5 column">
	              <div class="header">
	                <a href="${This.path}/${root.name}">${root.title}</a>
	              </div>
	
	              <ul class="unstyled">
	                <#if root.type == 'PageNews'>
	                  <#assign nbNews = 0 />
	                  <#list This.getNews(root.ref) as child>
	                    <#if nbNews < maxNbLabsNews >
	                      <li>${child.title}</li>
	                    </#if>
	                    <#assign nbNews = nbNews + 1 />
	                  </#list>
	                <#else>
	                  <#list Session.getChildren(root.ref) as child>
	                    <#if child.type == 'Folder'>
	                      <li>${child.title}</li>
	                    <#else>
	                      	<li><a href="${This.path}/${root.name}/${child.name}">${child.title}</a></li>
	                    </#if>
	                  </#list>
	                </#if>
	              </ul>
	            </div> <!-- bloc -->
            </#if>
          </#list>
          </div>
        </div> <!-- row -->
      </div> <!-- content -->
    </div> <!-- container-fluid -->
  </@block>
</@extends>