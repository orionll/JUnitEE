<!--
	$Id: result.jsp,v 1.1.1.1 2001-07-23 21:31:02 lhoriman Exp $
	$Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/components/web-war/result.jsp,v $

	This is a cheesey JSP Model 1 "page centric" example.  A real web
	application should never have code in a JSP file, but this
	project is for demonstrating good test design, not good web design :-)
-->

<%@ page language="java" import="org.infohazard.ejb.einstein.*, javax.naming.*, javax.rmi.*" %>
<%!
	protected Einstein ein;

	public void jspInit()
	{
		try
		{
			Context jndiContext = new InitialContext();
			Object ref = jndiContext.lookup("java:comp/env/ejb/EinsteinEJB");
			EinsteinHome home = (EinsteinHome)PortableRemoteObject.narrow(ref, EinsteinHome.class);

			this.ein = home.create();
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex.toString());
		}
	}
%>

<html>

	<head>
		<title> Add Numbers </title>
	</head>
	
	<body>
		<p>
			<%
				String first = request.getParameter("first");
				String second = request.getParameter("second");
			%>

			The result of trying to add <%= first %> and <%= second %> is:

			<%
				boolean failed = false;
				String result = null;

				try
				{
					result = ein.addTwoNumbers(first, second);
				}
				catch (BadNumberException ex)
				{
					failed = true;
			%>
				Hey, you need to give me real numbers!
			<%
				}

				if (!failed)
				{
			%>
				<%= result %>
			<%
				}
			%>

		</p>
	
	</body>

</html>
