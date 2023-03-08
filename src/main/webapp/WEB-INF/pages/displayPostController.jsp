<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Post List</title>
</head>
<body>
  <c:forEach var="item" items="${activePosts}">
    <c:out value="${item.title}"/> <br>
  </c:forEach>
</body>
</html>