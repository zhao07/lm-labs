<@extends src="/views/labs-base.ftl">
	
	<@block name="title">${Context.module.name} - ${This.document.type} ${This.document.title}</@block>
    
    <@block name="scripts">
	  	<@superBlock/>
		<script type="text/javascript" src="${skinPath}/js/ckeditor/ckeditor.js"></script>
		<script type="text/javascript" src="${skinPath}/js/ckeditor/init.js"></script>
		<script type="text/javascript" src="${skinPath}/js/ckfinder/ckfinder.js"></script>
	</@block>
    
    <@block name="css">
	  	<@superBlock/>
	    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/page_blocs.css"/>
	    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/wysiwyg_editor.css"/>
		<link rel="stylesheet" type="text/css" href="${skinPath}/css/ckeditor.css"/>
	    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/searchbox.css"/>
	    <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/sidebar.css"/>
	</@block>
	
	
  <@block name="content">
      <#-- SIDEBAR AREA --> 
      <#include "views/common/sidebar_area.ftl" />
    <div id="content" class="pageBlocs welcome">
      <#if Session.hasPermission(This.previous.document.ref, 'Everything') >
      <div style="float:right;">
      	<a href="${Context.baseURL}/nuxeo/nxpath/default/default-domain/sites/${This.previous.title}/tree@view_documents?tabIds=%3A" target="_blank" >${Context.getMessage('command.LabsSite.goToBackOffice')}</a>
      </div>
      </#if>
      <#-- COMMENT AREA --> 
      <#include "views/common/comment_area.ftl" />
      
      <div id="blocs" >
      <#list This.children as root>
        <#if root.name != "welcome">
        <div id="bloc${root_index}" class="bloc">
          <div class="blocTitle welcome">
          <a href="${This.previous.path}/${root.name}">${root.title}</a>
          </div>
          <div class="blocContent">
            <ul>
          <#list Session.getChildren(root.ref) as child>
            <#if child.type == 'LabsNews'>
              <li>${child.title}</li>
            <#else>
              <li><a href="${This.previous.path}/${root.name}/${child.name}">${child.title}</a></li>
            </#if>
          </#list>
            </ul>
          </div>
        </div>
        </#if>
      </#list>
      <#-- FIXME -->
        <div id="bloc_test" class="bloc">
          <div class="blocTitle welcome">
        MANUTEO
          </div>
          <div class="blocContent">
          Catalogue en ligne des engins et supports de manutention
          </div>
      </div>
    </div>
  </@block>
</@extends>	