<script type="text/javascript">
<#assign isContributor = This.page?? && This.page.isContributor(Context.principal.name) />
<#if isContributor >
var rowWidgetsAtLoad = [];
function defaultConfigGadgetAjaxFormBeforeSubmit() {
    jQuery('#divConfigGadget').find('div.actions a, input, button').attr('disabled', '');
    return true;
}
function defaultConfigGadgetAjaxFormError() {
    jQuery('#divConfigGadget').dialog2('close');
}
function defaultConfigGadgetAjaxFormSucess() {
    jQuery('#divConfigGadget div.actions a, input, button').removeAttr('disabled');
    jQuery('#waitingPopup').dialog2('open');
    <#--
    jQuery('#divConfigGadget').dialog2('close');
    -->
    window.location.href = '${This.path}#row_s' + jQuery('#divConfigGadget input[name=section]').val() + '_r' + jQuery('#divConfigGadget input[name=row]').val();
    document.location.reload(true);
}

jQuery(document).ready(function() {
    jQuery('#divConfigGadget form').removeAttr('enctype');
    jQuery("#divConfigRowGadgets").dialog2({
        autoOpen : false,
        closeOnOverlayClick : false
    });
    jQuery("#divConfigGadget").dialog2({
        autoOpen : false,
        closeOnOverlayClick : false
    });
    jQuery("#divColumnUrl").dialog2({
        autoOpen : false,
        closeOnOverlayClick : true
    });
    jQuery('#config-row-gadgets-form').ajaxForm({
        data: {ajax: 'true'},
        beforeSubmit:  function () {
		    jQuery('#divConfigRowGadgets div.actions a, input, button').attr('disabled', '');
		    return true;
		},
        error: function () {
		    jQuery('#divConfigRowGadgets').dialog2('close');
		},
        success: function () {
		    jQuery('#divConfigRowGadgets div.actions a, input, button').removeAttr('disabled');
		    jQuery('#waitingPopup').dialog2('open');
		    <#--
		    jQuery('#divConfigRowGadgets').dialog2('close');
		    -->
		    window.location.href = '${This.path}#row_s' + jQuery('#divConfigRowGadgets input[name=section]').val() + '_r' + jQuery('#divConfigRowGadgets input[name=row]').val();
		    document.location.reload(true);
		}
    });
    jQuery('#config-gadget-form').ajaxForm({
        data: {ajax: 'true'},
        beforeSubmit:  defaultConfigGadgetAjaxFormBeforeSubmit,
        error: defaultConfigGadgetAjaxFormError,
        success: defaultConfigGadgetAjaxFormSucess
    });
    jQuery('#config-row-gadgets-form-btn').click(function() {
        rowWidgetsAtSubmit = [];
        jQuery('#divConfigRowGadgets-content option:selected').each(function() {
            rowWidgetsAtSubmit.push(jQuery(this).val());
        });
        var arraysDifferent = false;
        jQuery(rowWidgetsAtLoad).each(function(index, val) {
            if (val !== rowWidgetsAtSubmit[index]) {
                arraysDifferent = true;
                return false;
            }
        });
        if (arraysDifferent) {
            <#-- lists are different -->
            if (confirm('${Context.getMessage('label.HtmlPage.row.widgets.config.save.confirm')}')) {
                jQuery('#waitingPopup').dialog2('open');
                jQuery('#' + jQuery(this).attr('form-id')).submit();
            } else {
                jQuery('#divConfigRowGadgets').dialog2('close');
                return false; <#-- prevents form submit -->
            }
        }
        jQuery('#divConfigRowGadgets').dialog2('close');
        return false; <#-- prevents form submit -->
    });
    jQuery('#config-gadget-form-btn').click(function() {
        var inputs = jQuery("#divConfigGadget form > fieldset").find("input[type=text],input[type=file],select,checkbox,textarea");
        if (inputs.length == 0) {
            jQuery('#divConfigGadget').dialog2('close');
            return false; <#-- prevents form submit -->
        }
        jQuery('#waitingPopup').dialog2('open');
        jQuery('#' + jQuery(this).attr('form-id')).submit();
    });
    jQuery('a[rel=divConfigRowGadgets]').click(function() {
        var sectionIndex = jQuery(this).closest('li').find('input.section-index-value').val();
        var rowIndex = jQuery(this).closest('li').find('input.row-index-value').val();
        jQuery('#divConfigRowGadgets input[name=section]').val(sectionIndex);
        jQuery('#divConfigRowGadgets input[name=row]').val(rowIndex);
        jQuery('#divConfigRowGadgets form').attr('action', '${This.path}/s/' + sectionIndex + '/r/' + rowIndex + '/@manage-widgets');
        jQuery.ajax({
            type : "GET",
            url : '${This.path}/s/' + sectionIndex + '/r/' + rowIndex + '/@views/manage-widgets',
            success : function(msg) {
                jQuery("#divConfigRowGadgets-content").html(msg);
                rowWidgetsAtLoad = [];
                jQuery('#divConfigRowGadgets-content option:selected').each(function() {
                    rowWidgetsAtLoad.push(jQuery(this).val());
                });
            },
            error : function(msg) {
                jQuery("#divConfigRowGadgets-content").html(msg.responseText);
                //alert('ERROR' + msg.responseText);
            }
        });
    });
    jQuery('a[rel=divConfigGadget]').click(function() {
        var sectionIndex = jQuery(this).closest('div.editblock').find('input.section-index-value').val();
        var rowIndex = jQuery(this).closest('div.editblock').find('input.row-index-value').val();
        var contentIndex = jQuery(this).closest('div.editblock').find('input.content-index-value').val();
        var widgetType = jQuery(this).closest('div.editblock').find('input.widget-type').val();
        var widgetName = jQuery(this).closest('div.editblock').find('input.widget-name').val();
        jQuery('#divConfigGadget input[name=section]').val(sectionIndex);
        jQuery('#divConfigGadget input[name=row]').val(rowIndex);
        jQuery('#divConfigGadget input[name=content]').val(contentIndex);
        jQuery('#divConfigGadget input[name=widget-type]').val(widgetType);
        jQuery('#divConfigGadget input[name=widget-name]').val(widgetName);
        jQuery('#divConfigGadget form').attr('action', '${This.path}/s/' + sectionIndex + '/r/' + rowIndex + '/c/' + contentIndex + '/w/@put');
        jQuery.ajax({
            type : "GET",
            url : '${This.path}/s/' + sectionIndex + '/r/' + rowIndex + '/c/' + contentIndex + '/w/@views/edit',
            success : function(msg) {
                jQuery("#divConfigGadget-content").html(msg);
            },
            error : function(msg) {
                jQuery("#divConfigGadget-content").html(msg.responseText);
                //alert('ERROR' + msg.responseText);
            }
        });
    });
    jQuery('.row-ckeditor .col-link a').click(function() {
    	var inputObj = jQuery('#' + jQuery(this).attr('rel')).find('input');
    	jQuery(inputObj).val(jQuery(this).siblings('input[type=hidden]').val());
    	jQuery(inputObj).select();
    });
<#-- A GARDER
    <#if This.page.isDisplayable("pg:collapseType") && This.page.collapseType == "collapse_all" >
    jQuery.each(jQuery('img.openCloseBt'), function(i, imgObj) {
	    slideSection(imgObj, 'open');
    });
    </#if>
-->
})
</#if>

function isValidExternalContent(content) {
	return (content.indexOf('dontbeevil') !== -1);
}

function isValidExternalContentUrl(url) {
	var modulePath = '${Context.modulePath}';
	var elems = modulePath.split('/');
	return (url.length > 0 && url.indexOf(elems[elems.length-1]) !== -1);
}

function updateSectionLabels(bt, title) {
    jQuery(bt).attr("title", title);
}

function changeSectionBt(obj, newStatus) {
    if (newStatus === 'open') {
        jQuery(obj).removeClass("icon-minus-sign");
        jQuery(obj).addClass("icon-plus-sign");
    } else {
        jQuery(obj).removeClass("icon-plus-sign");
        jQuery(obj).addClass("icon-minus-sign");
    }
}

function slideSection(obj, action) {
    var collapsables = jQuery(obj).closest('section').find("div[class*='section-collapsable']");
    if (action === '') {
        if (collapsables.is(":visible")) {
            action = 'open';
        } else {
            action = 'close';
        }
    }
    if (action === "open") {
        collapsables.slideUp("fast");
        changeSectionBt(obj, 'open');
        updateSectionLabels(obj, "${Context.getMessage('label.HtmlPage.open')}");
    } else {
        changeSectionBt(obj, 'close');
        updateSectionLabels(obj, "${Context.getMessage('label.HtmlPage.collapse')}");
        collapsables.slideDown("fast");
        refreshDisplayMode(collapsables);
    }
}

function refreshConfigGadgetDialog() {
	var waitingPic = '<img src="${skinPath}/images/loading.gif" />';
	jQuery("#divConfigGadget-content").html(waitingPic);
    var sectionIndex = jQuery('#divConfigGadget input[name=section]').val();
    var rowIndex = jQuery('#divConfigGadget input[name=row]').val();
    var contentIndex = jQuery('#divConfigGadget input[name=content]').val();
    var widgetType = jQuery('#divConfigGadget input[name=widget-type]').val();
    var widgetName = jQuery('#divConfigGadget input[name=widget-name]').val();
    jQuery('#divConfigGadget form').attr('action', '${This.path}/s/' + sectionIndex + '/r/' + rowIndex + '/c/' + contentIndex + '/w/@put');
    jQuery.ajax({
        type : "GET",
        url : '${This.path}/s/' + sectionIndex + '/r/' + rowIndex + '/c/' + contentIndex + '/w/@views/edit',
        success : function(msg) {
            jQuery("#divConfigGadget-content").html(msg);
        },
        error : function(msg) {
            jQuery("#divConfigGadget-content").html(msg.responseText);
            //alert('ERROR' + msg.responseText);
        }
    });
}
</script>
