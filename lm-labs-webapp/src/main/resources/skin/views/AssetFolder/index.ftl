<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN"
    "http://www.w3.org/TR/html4/strict.dtd">
<html lang="fr">
    <head>
      <title></title>
      <script type="text/javascript" src="${skinPath}/js/jquery/jquery-1.5.1.min.js"></script>
      <script type="text/javascript" src="${skinPath}/js/jquery/jquery.cookie.js"></script>
      <script type="text/javascript" src="${skinPath}/js/jquery/jquery.treeview.js"></script>
      <script type="text/javascript" src="${skinPath}/js/jquery/jquery.treeview.edit.js"></script>
      <script type="text/javascript" src="${skinPath}/js/jquery/jquery.treeview.async.js"></script>
        <script type="text/javascript" src="${skinPath}/js/jquery/jquery.form.js"></script>
        <script type="text/javascript" src="${skinPath}/js/jquery/jquery.controls.js"></script>
        <script type="text/javascript" src="${skinPath}/js/jquery/jquery.dialog2.js"></script>
        <script type="text/javascript" src="${skinPath}/js/labs.js"></script>




      <link rel="stylesheet/less" href="${skinPath}/less/labs.less">
      <script type="text/javascript" src="${skinPath}/js/assets/less/less-1.1.4.min.js"></script>

      <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/jquery/jquery.treeview.css"/>
      <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/browse_tree.css"/>
      <link rel="stylesheet" type="text/css" media="all" href="${skinPath}/css/jquery/jquery.dialog2.css"/>
    </head>
    <body>


    <div class="container-fluid">

      <div class="sidebar">
        <div class="bloc">
          <div class="header">
            Arborescence
          </div>




           <div class="treeroot"></div>
           <ul id="treenav" class="treeview">

           </ul>

        </div> <!-- bloc -->
      </div>


      <div class="content">
        <div class="row">
          <div id="fileContent" class="span14 columns well" style="min-height:300px;">
            <#include "views/AssetFolder/content.ftl"/>
          </div>
          <div class="span12 columns actions">
            <a href="#" rel="addFileDialog" class="open-dialog btn">Ajouter un fichier</a>
            <a href="#" rel="addFolderDialog" class="open-dialog btn">Ajouter un répertoire</a>
          </div>

          <div id="addFolderDialog" style="display:none;">
            <h1>Ajouter un répertoire</h1>
            <form id="addFolderForm" action="${This.path}" onSubmit="this.action=currentPath" method="post">
              <input type="hidden" name="doctype" value="Folder"/>
              <fieldset>
                <div class="clearfix">
                    <label for="title">Nom du répertoire</label>
                      <div class="input">
                        <input name="dublincore:title"/>
                      </div>
                    </div><!-- /clearfix -->
              </fieldset>
              <div class="actions">
                <button type="submit" class="btn primary">Ajouter</button>
              </div>
            </form>
          </div>

          <div id="addFileDialog">
            <h1>Ajouter un Fichier</h1>
            <form id="addFileForm" action="${This.path}" onSubmit="this.action=currentPath" method="post" enctype="multipart/form-data">
              <fieldset>
                <div class="clearfix">
                        <label for="title">Choisir le fichier</label>
                          <div class="input">
                            <input type="file" name="file"/>
                          </div>
                        </div><!-- /clearfix -->

                        <div class="clearfix">
                        <label for="description">Description</label>
                          <div class="input">
                            <textarea name="dublincore:description"></textarea>
                          </div>
                        </div><!-- /clearfix -->

              </fieldset>
              <div class="actions">
                <button type="submit" class="btn primary">Ajouter</button>
              </div>
            </form>
          </div>

        </div>
      </div>
    </div>


   <script>
   var currentPath = "${This.path}";
 $(document).ready(function() {
              $('#treenav').treeview({
                url: "${Context.modulePath}/${site.URL}/@assets/json",
                persist: "cookie",
                control: "#navtreecontrol",
                collapsed: true,
                cookieId: "${site.document.id}-assets-navtree",
                onLoad: function(nodeLoaded) {
                   $("#treenav span").click(function(elt) {
                       currentPath = "${Context.modulePath}/${site.URL}/@assets"+$(this).parent("li").attr("id");
                       updateContent();
                   })
                }


              });
            });

  function updateContent(path) {
     $("#fileContent").load(currentPath+"/@views/content");
  }


    function sendToCKEditor(href) {
      window.opener.CKEDITOR.tools.callFunction('2', href);
      window.close();
    }
    </script>
  </body>
</html>