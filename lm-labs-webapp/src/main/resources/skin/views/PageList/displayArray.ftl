<table id="sortArray" class="zebra-striped">
	<thead>
		<tr>
			<#list bean.headersSet as header>
				<th class="header headerSortDown">${header.name}</th>
			</#list>
		</tr>
	</thead>
	<tbody>
		<#list bean.entriesLines as entriesLine>
			<tr>
				<#list bean.headersSet as header>
					<#if isAuthorized>
						<td style="cursor: pointer;" onclick="javascript:modifyLine('${This.path}/line/${entriesLine.getDocRef().reference()}');">
					<#else>
						<td>
					</#if>
						<#assign entry = entriesLine.getEntryByIdHead(header.idHeader) />
						<#if entry != null>
							<#include "/views/PageList/" + header.type?lower_case + "/display.ftl" />
						</#if>
					</td>
				</#list>
			</tr>
		</#list>
	</tbody>
</table>
<#if 0 < bean.entriesLines?size>
	<script type="text/javascript">
		$("table#sortArray").tablesorter({
        textExtraction: function(node) {
              // extract data from markup and return it
	          var sortValues = jQuery(node).find('span[class=sortValue]');
	          if (sortValues.length > 0) {
	            return sortValues[0].innerHTML;
	          }
	              return node.innerHTML;
	      }
      });
	</script>
</#if>