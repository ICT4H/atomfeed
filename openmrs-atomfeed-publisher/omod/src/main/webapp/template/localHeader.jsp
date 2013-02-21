<spring:htmlEscape defaultHtmlEscape="true" />
<ul id="menu">
	<li class="first"><a
		href="${pageContext.request.contextPath}/admin"><spring:message
				code="admin.title.short" /></a></li>

	<li
		<c:if test='<%= request.getRequestURI().contains("/atomfeed.jsp") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/atomfeed/atomfeed.htm"><spring:message
				code="atomfeed.viewfeed" /></a>
	</li>
	
	<li
		<c:if test='<%= request.getRequestURI().contains("/config") %>'>class="active"</c:if>>
		<a
		href="${pageContext.request.contextPath}/module/atomfeed/config.form"><spring:message
				code="atomfeed.config" /></a>
	</li>
	
	<!-- Add further links here -->
</ul>
<h2>
	<spring:message code="atomfeed.title" />
</h2>
