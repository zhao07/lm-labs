<#function shortDateTime calendar >
	<#assign todayDate = Common.getNow().time?datetime?date>
	<#assign calendarDate = calendar?date>
	<#if calendarDate?date?string.short == todayDate?date?string.short >
		<#assign aTime = calendar?time >
		<#return aTime?string.short>
	<#else>
		<#assign aDate = calendar?date>
		<#return aDate?string.short>
	</#if>
</#function>
