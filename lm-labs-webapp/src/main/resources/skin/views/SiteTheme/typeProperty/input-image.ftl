<input id="valueProperty${cptProperties}" name="valueProperty${cptProperties}" type="hidden" value="<#if (property.value != null)>${property.value?html}</#if>" />
<a href="#" onclick="javascript:openAssets('${Context.modulePath}/${Common.siteDoc(Document).getSite().URL}/@assets?callFunction=setCallFunction&calledRef=${cptProperties}')">Associer un média</a>
<span id="spanTextAsset${cptProperties}">&nbsp;</span>
<div id="actionMedia${cptProperties}" style="float: right;">
	<img class="actionMediaImage" src="<#if (property.value != null)>${property.value}</#if>" style="width: 40px;border:1px dashed black;<#if (property.value = null)> display:none;</#if>"/>
	<span onclick="javascript:deleteElement('${This.path}/propertyTheme/${property.key}', 'hidePropertyImage(${cptProperties})', '${Context.getMessage('label.labssites.appearance.theme.edit.element.delete.confirm')}');" style="cursor: pointer;">
    	<img title="${Context.getMessage('label.delete')}" src="${skinPath}/images/x.gif"<#if (property.value == null)> style="display: none;"</#if> />
  	</span>
</div>
