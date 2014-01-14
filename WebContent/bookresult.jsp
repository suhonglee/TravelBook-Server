<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>

<bookcount>${fn:length(latitude) }</bookcount>
<c:forEach var="i" begin="0" step="1" end="${fn:length(latitude)-1}">
	<latitude${i}>${latitude[i]}</latitude${i}>
	<longitude${i}>${longitude[i] }</longitude${i }>
	<address${i}>${address[i] }</address${i }>
</c:forEach>