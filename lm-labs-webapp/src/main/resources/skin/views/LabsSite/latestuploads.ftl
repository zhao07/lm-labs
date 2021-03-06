<@extends src="/views/TemplatesBase/" + This.page.template.getTemplateName() + "/template.ftl">


<@block name="scripts">

      <script type="text/javascript" src="${contextPath}/wro/labs.common.js"></script>
    <@superBlock/>
  </@block>

  <@block name="css">
      <@superBlock/>
      <link rel="stylesheet" type="text/css" media="all" href="${contextPath}/wro/labs.common.css"/>
  </@block>


<#include "macros/PageClasseur_file_links.ftl" >
<#include "views/common/paging.ftl" />
<#assign nbrElemPerPage = 20 />

  <@block name="title">${Common.siteDoc(Document).getSite().title}-${This.document.title} - ${Context.getMessage('title.LabsSite.latestuploads')}</@block>

  <@block name="docactionsaddpage"></@block>
  <@block name="docactionsonpage"></@block>

  <@block name="content">
  <div class="container-fluid">
    <section>
      <div class="page-header">
        <h1>${Context.getMessage('title.LabsSite.latestuploads')}</h1>
      </div>

  <div class="row-fluid">
    <div class="">
      <#assign pp = latestUploadsPageProvider(Document, nbrElemPerPage, Context.getCoreSession()) />
      <#assign currentPage = Context.request.getParameter('page')?number?long />
      <#assign uploads = pp.setCurrentPage(currentPage) />
      <@resultsStatus pageProvider=pp />
      <@paging pageProvider=pp url=This.path+"/@views/latestuploads?page=" />

      <div class="latestuploads-table">
        <table class="table table-striped table-bordered bs labstable">
          <thead>
            <tr>
              <th class="header">&nbsp;</th>
              <th class="header">Titre</th>
              <th class="header">Modifié le</th>
              <th class="header">${Context.getMessage('label.LabsSite.latestuploads.onpage')} </th>
              <th class="header" style="width: 56px;">&nbsp;</th>
          </thead>
          <tbody>
          <#list uploads as upload >
          <tr>
            <td><img title="${upload.type}" alt="&gt;" src="/nuxeo/${upload.common.icon}" /></td>
            <td>${upload.title}</td>
            <#assign modifDate = upload['dc:modified']?datetime >
            <td>${modifDate?string("EEEE dd MMMM yyyy HH:mm")}</td>
            <#assign sd = Common.siteDoc(upload) />
            <td><a href="${Context.modulePath}/${sd.getParentPagePath()}">${sd.getParentPage().title}</a></td>
            <td>
        <@fileDownloadLink url="${Context.modulePath}/${sd.getResourcePath()}/@blob" tooltip="${Context.getMessage('command.LabsSite.latestuploads.download')}" />
        <@fileDisplayLink url="${Context.modulePath}/${sd.getResourcePath()}/@blob/preview" tooltip="${Context.getMessage('command.LabsSite.latestuploads.display')}" />
              </td>
          </tr>
          </#list>
          </tbody>
        </table>
        </div>
        </div>

      </div>

    </section>
    </div>
  </@block>
  <@block name="pageCommentable"></@block>
</@extends>
