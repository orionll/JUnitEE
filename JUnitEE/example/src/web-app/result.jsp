<!--
	$Id: result.jsp,v 1.1 2002-09-22 21:46:48 o_rossmueller Exp $
	$Source: C:\Users\Orionll\Desktop\junitee-cvs/JUnitEE/example/src/web-app/result.jsp,v $

	This is a cheesey JSP Model 1 "page centric" example.  A real web
	application should never have code in a JSP file, but this
	project is for demonstrating good test design, not good web design :-)
-->

<%@ page language="java" import="org.infohazard.ejb.einstein.*, javax.naming.*, javax.rmi.*,
                                 org.junitee.ejb.einstein.BadNumberException,
                                 org.junitee.ejb.einstein.Einstein,
                                 org.junitee.ejb.einstein.EinsteinHome" %>
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
