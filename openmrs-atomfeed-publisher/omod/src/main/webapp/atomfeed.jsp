<%@ include file="/WEB-INF/template/include.jsp"%>

<openmrs:requireConfiguration propertyList="atomfeed.username, atomfeed.password" configurationPage="/module/atomfeed/config.form" />

<%@ include file="/WEB-INF/template/header.jsp"%>

<%@ include file="template/localHeader.jsp"%>

<br />
<a href="<openmrs:contextPath />/moduleServlet/atomfeed/atomfeed"><spring:message code="atomfeed.viewEntireFeed" /></a>
<br />
<br />
<spring:message code="atomfeed.or" />
<br />
<br />
<form action="<openmrs:contextPath />/moduleServlet/atomfeed/atomfeed">
	<spring:message code="atomfeed.asOfDate" />: 
	<openmrs_tag:dateField startValue="" formFieldName="asOfDate" />
	<input type="submit" value="<spring:message code="general.submit" />" />
</form>

<%@ include file="/WEB-INF/template/footer.jsp"%>