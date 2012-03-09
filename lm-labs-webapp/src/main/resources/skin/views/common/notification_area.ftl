<#if Context.principal.isAnonymous() == false>
	<div id="notification" >
		<#if Common.getNotifiableTypes()?seq_contains(Document.type) >
			<a id="subscribeBt" class="btn btn-primary" <#if This.isSubscribed() >style="display:none;"</#if> href="#" onclick="javascript:return subscribePage(true);"><i class="icon-envelope"></i>
				<#if Document.type == "PageNews">
					 ${Context.getMessage('command.contextmenu.PageNews.subscribe')}
				<#elseif Document.type == "Site">
					${Context.getMessage('command.contextmenu.Site.subscribe')}
                <#else>
                    ${Context.getMessage('command.contextmenu.Page.subscribe')}
				</#if>
			</a>
			<a id="unsubscribeBt" class="btn btn-primary" <#if !This.isSubscribed() >style="display:none;"</#if> href="#" onclick="javascript:return subscribePage(false);"><i class="icon-envelope"></i>
				<#if Document.type == "PageNews">
					${Context.getMessage('command.contextmenu.PageNews.unsubscribe')}
                <#elseif Document.type == "Site">
                    ${Context.getMessage('command.contextmenu.Site.unsubscribe')}
				<#else>
					${Context.getMessage('command.contextmenu.Page.unsubscribe')}
				</#if>
			</a>
			<script type="text/javascript">	
				function subscribePage(subscribe) {
				    <#if Document.type == "Site" >
				    var confirmMsg = '';
				    if (subscribe) {
				        confirmMsg = "${Context.getMessage('command.contextmenu.Site.subscribe.confirm')}";
				    } else {
                        confirmMsg = "${Context.getMessage('command.contextmenu.Site.unsubscribe.confirm')}";
				    }
				    if (!confirm(confirmMsg)) {
				        return;
				    }
				    </#if>
				    jQuery('#waitingPopup').dialog2('open');
					jQuery.ajax({
						type: 'GET',
					    async: false,
					    url: "${This.path}/@" + (subscribe ? 'subscribe' : 'unsubscribe'),
					    success: function(data) {
					    	if (subscribe) {
					    		jQuery('#subscribeBt').hide();
					    		jQuery('#unsubscribeBt').show();
					        }
					        else {
					    		jQuery('#subscribeBt').show();
					    		jQuery('#unsubscribeBt').hide();
					        }
					        jQuery('#waitingPopup').dialog2('close');
					    },
					    error: function() {
				    		<#-- TODO alert
					    	console.log('subscribe failed.');
					        -->
					        jQuery('#waitingPopup').dialog2('close');
					    }
					});
					return false;
				}
			</script>
		</#if>
	</div>
</#if>