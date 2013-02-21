<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<openmrs:globalProperty key="atomfeed.username" var="username"/>
<c:if test="${username == ''}">
	<span class="error"><spring:message code="atomfeed.configureUsername"/></span><br/>
</c:if>

<br/>
<b class="boxHeader"><spring:message code="atomfeed.settings"/></b>
<div class="box">
<openmrs:portlet url="globalProperties" parameters="propertyPrefix=atomfeed|excludePrefix=atomfeed.started;atomfeed.mandatory" />
</div>
<%@ include file="/WEB-INF/template/footer.jsp"%>