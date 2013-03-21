<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<html>
<head>
  <title>New Entry</title>
</head>

<body>
	<h1>New Entry</h1>

    <form:form method="POST" commandName="entry">
		<table>
			<tr>
				<td>Title :</td>
				<td><form:input path="title" /></td>
			</tr>
			<tr>
				<td colspan="3"><input type="submit" /></td>
			</tr>
		</table>
	</form:form>

</body>
</html>