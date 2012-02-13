var ckeditorconfig = {
extraPlugins : 'tableresize,insertlabspagetoc',
filebrowserBrowseUrl : '@assets',
filebrowserImageBrowseUrl : '@assets',
filebrowserFlashBrowseUrl : '@assets',
filebrowserUploadUrl : '@assets',
filebrowserImageUploadUrl : '@assets',
filebrowserFlashUploadUrl : '@assets',
toolbar:
[
['Source'],
['Cut','Copy','Paste','PasteText','PasteFromWord'],
['NumberedList','BulletedList','-','JustifyLeft','JustifyCenter','JustifyRight','JustifyBlock','-','Outdent','Indent','Blockquote'],
'/',
['Undo','Redo','Bold','Italic','Underline','Strike','-','SelectAll','RemoveFormat'],
['Link','Unlink'],
[ 'Image','Flash','Table','HorizontalRule','Smiley','SpecialChar'
<#if This.type.name == "PageClasseur" || This.type.name == "HtmlPage" >
,'-','InsertLabsPageTableOfContents'
</#if>
] ,'/',
['Format','Font','FontSize'],
['TextColor','BGColor'],
['Maximize', 'ShowBlocks','-']
]
};