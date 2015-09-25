<%@include file="/WEB-INF/html/init.jsp"%>

<%
	ResultRow row = (ResultRow) request
			.getAttribute(WebKeys.SEARCH_CONTAINER_RESULT_ROW);
	Part part = (Part) row.getObject();

	long groupId = part.getGroupId();
	String name = Part.class.getName();
	long partId = part.getPartId();

	String redirect = PortalUtil.getCurrentURL(renderRequest);
%>

<liferay-ui:icon-menu>
	<c:if
		test="<%= PartPermission.contains(permissionChecker, partId, ActionKeys.UPDATE) %>">
			<portlet:renderURL  var="editIconURL">
				<portlet:param name="view" value="editPart"/>
				<portlet:param name="partId" value="<%= String.valueOf(partId) %>" /> 
		 		<portlet:param name="redirect" value="<%=redirect%>" />  
			</portlet:renderURL>
		
		<liferay-ui:icon image="edit" url="<%=editIconURL.toString() %>" />
	</c:if>

 	<c:if test="<%= PartPermission.contains(permissionChecker, partId, ActionKeys.DELETE) %>"> 
		<portlet:actionURL var="deleteURL">
			<portlet:param name="action" value="deletePart" />
			<portlet:param name="partId" value="<%= String.valueOf(partId) %>" /> 
			<portlet:param name="redirect" value="<%= PortalUtil.getCurrentURL(renderRequest) %>" /> 
		</portlet:actionURL>

		<liferay-ui:icon-delete url="<%=deleteURL.toString() %>" />
	 </c:if> 

	<c:if
		test="<%= PartPermission.contains(permissionChecker, partId, ActionKeys.PERMISSIONS) %>">
		<liferay-security:permissionsURL
			modelResource="<%= Part.class.getName() %>"
			modelResourceDescription="<%= part.getName(locale) %>"
			resourcePrimKey="<%= String.valueOf(partId) %>" var="permissionsURL" />

		<liferay-ui:icon image="permissions" url="<%= permissionsURL %>" />
	</c:if>
</liferay-ui:icon-menu>