function loadEditParameters (url) {
	jQuery("#div-editTheme-container").html('<div id="div-editTheme" style="display:none;"></div>');
	jQuery('#waitingPopup').dialog2('open');
	jQuery.ajax({
		type: "GET",
		url: url,
		data: '',
		success: function(msg){
			jQuery("#div-editTheme").html(msg);
			openEditParameters();
			jQuery('#waitingPopup').dialog2('close');
		},
		error: function(msg){
			alert( msg.responseText );
			jQuery('#waitingPopup').dialog2('close');
		}
	});
}

function openEditParameters(){
	jQuery("#div-editTheme").dialog2({
		width : '570px',
		height : '394px',
		overflowy : 'auto',
		overflowx : 'hidden',
	    autoOpen : false,
		closeOnOverlayClick : false,
		removeOnClose : true,
		showCloseHandle : true
	  });
    jQuery("#div-editTheme").dialog2('open');
}

function deleteBanner(url, path, msgConfirm){
	if (confirm(msgConfirm)){
		jQuery('#waitingPopup').dialog2('open');
		jQuery.ajax({
			type: "DELETE",
			url: url,
			data: '',
			success: function(msg){
				document.location.href=path + msg;
			},
			error: function(msg){
				alert( msg.responseText );
				jQuery('#waitingPopup').dialog2('close');
			}
		});
	}
}

function manageDisplayModifyParameters(value){
	if (jQuery("#theme").val() == value){
		jQuery("#modifyThemeParameters").show();
	}
	else{
		jQuery("#modifyThemeParameters").hide();
	}
}

var oldTemplate = null;

$(document).ready(function() {
	oldTemplate = jQuery('#template option:selected').val();
});

function linkThemeTemplate(){
	if(jQuery("#theme").val() == "supplyChain"){
		oldTemplate = jQuery('#template option:selected').val();
		jQuery('#template option[value="supplyChain"]').attr('selected', 'selected');
	}
	else if(oldTemplate != null && jQuery("#template").val() == "supplyChain"){
		jQuery('#template option[value="' + oldTemplate + '"]').attr('selected', 'selected');
	}
	
}

function setCallFunction(calledRef, value){
	jQuery("#valueProperty" + calledRef).val(value);
	var picObj = jQuery('#actionMedia' + calledRef + ' img.actionMediaImage');
	jQuery(picObj).attr('src', value);
	jQuery(picObj).show();
	jQuery('#actionMedia' + calledRef + ' span > img').show();
}

function setBannerInput(ref, href, docid) {
	setSiteThemeConfigInput('Banner', ref, href, docid);
}

function setLogoInput(ref, href, docid) {
	setSiteThemeConfigInput('Logo', ref, href, docid);
	jQuery('div.edit-logo-properties').show();
}

function setSiteThemeConfigInput(type, ref, href, docid) {
	updateSiteThemeResourceDocId(type, docid);
	setCallFunction(type, href);
}

function hideConfigBanner() {
	hidePropertyImage('Banner');
}

function hideConfigLogo() {
	hidePropertyImage('Logo');
	jQuery('div.edit-logo-properties').hide();
}

function hideSiteThemeConfigInput(type) {
	hidePropertyImage(type);
}

function hideBanner(){
	jQuery("#actionMediaBanner").hide();
	if (jQuery("#bannerImgId")){
		jQuery("#bannerImgId").attr("src", jQuery("#bannerImgId").attr("src") + '?' + new Date());
	}
}

function hideLogo(){
	jQuery("#actionMediaLogo").hide();
}

function hidePropertyImage(propertyName){
	jQuery("#actionMedia" + propertyName + ' > span > img').hide();
	var picObj = jQuery('#actionMedia' + propertyName + ' img.actionMediaImage');
	jQuery(picObj).hide();
	jQuery(picObj).attr('src', '');
	jQuery('#valueProperty' + propertyName).val('');
	jQuery('#waitingPopup').dialog2('close');
}

function deleteElement(url, callFunction, msgConfirm){
	if (confirm(msgConfirm)){
		jQuery('#waitingPopup').dialog2('open');
		jQuery.ajax({
			type: "DELETE",
			url: url,
			data: '',
			success: function(msg){
				//document.location.href=path + msg;
				eval(callFunction);
				jQuery('#waitingPopup').dialog2('close');
			},
			error: function(msg){
				alert( msg.responseText );
				jQuery('#waitingPopup').dialog2('close');
			}
		});
	}
}

function deleteSiteThemeBlob(blobName, sitethemeId, serverUrl, callFunction, msgConfirm) {
	if (confirm(msgConfirm)) {
		jQuery('#waitingPopup').dialog2('open');
	    var operation = 'Blob.Remove';
	    var d = {"params":{"xpath":"sitetheme:" + blobName},"input":"doc:"+sitethemeId,"context":{}};
		jQuery.ajax({
			type: "POST",
			url: serverUrl + '/automation/' + operation,
	        async: false,
	        contentType: 'application/json+nxrequest',
	        data: JSON.stringify(d),
	        success: function(data, textStatus, jqXHR) {
				eval(callFunction);
				jQuery('#waitingPopup').dialog2('close');
			},
			error: function(jqXHR, textStatus, errorThrown) {
				alert( textStatus + ':' + errorThrown );
				jQuery('#waitingPopup').dialog2('close');
			}
			/*
	        ,complete: function(jqXHR, textStatus) {alert('complete');}
			 */
		});
	}
}

function openAssets(url){
	popupCenter(url, (screen.width)*2/3, (screen.height)*2/3, "menubar=no,scrollbars=yes,statusbar=no");
}