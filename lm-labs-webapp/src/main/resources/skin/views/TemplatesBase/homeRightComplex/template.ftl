<@extends src="/views/TemplatesBase/templateCommon.ftl">

	<@block name="FKtopContent">
		<@superBlock/>
		
		<#--  masthead  -->
		<div id="masthead">
			<#--  Logo  -->
			<#include "views/common/logo.ftl" />
			<#--  Banner  -->
			<#include "views/common/banner.ftl" />
		</div>
		
		<#--  content -->
		<div class="container-fluid">
			<div class="row-fluid">
				<#--  central content -->
		        <div class="central span10">

				    <#--  horizontal Navigation  -->
			      	<#include "views/common/topnavigation_area.ftl" />
			      	
					<#--  breadcrumbs  -->
					<#include "views/common/breadcrumbs.ftl" >
			
					<#--  action-message -->
					<#include "views/common/action_message.ftl" >
					
		        	<#--  Content  -->
				    <@block name="content" />
				    
				    <#--  Commentaires  -->
				    <@block name="pageCommentable">
						<#include "/views/LabsComments/displayCommentsPage.ftl" />
					</@block>
		        </div>
				
				<#--  sidebar -->
				<div class="sidebar span2"> 
					<@block name="sidebar">
					    	<@block name="topPage"/>
					    	<#include "views/common/sidebar_area.ftl" />
				    </@block>
				    
				</div>
			
			    <div style="clear:both;"></div>
			    
			</div><#--  /row-fluid -->
		</div><#-- /container-fluid -->
	</@block>
</@extends>