<%@ page language="java" contentType="text/html; charset=UTF-8"
  pageEncoding="UTF-8" errorPage="error.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Result</title>
</head>
<body bgcolor="red">
  <%
  int number1 = Integer.parseInt(request.getParameter("num1"));
  int number2 = Integer.parseInt(request.getParameter("num2"));
  int output = number1 + number2;
  out.println("Output: " + output);
  %>
</body>
</html>
