<div id="comment">
  <div id="comment_form">
    <div id="commentField" class="formWysiwyg">${This.page.commentaire}</div>

    <#assign form_name="comment_form" />
    <#assign callback_function="save" />
    <#include "views/common/form_utils.ftl" />

    <script type="text/javascript">
    function save() {
      jQuery.ajax({
         type: "POST",
         url: "${This.path}/updateCommentaire",
         data: "commentaire="+jQuery("#commentField").html(),
         success: function(msg){
         },
         error: function(msg){
            alert("erreur " + msg);
         }
      });
    }
    </script>
  </div>
</div>