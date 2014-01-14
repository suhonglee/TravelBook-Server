<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>

<c:forEach var="i" begin="0" step="1" end="${fn:length(comment)-1}">
	<email${i}>${email[i]}</email${i}>
	<name${i}>${name[i]}</name${i}>
	<time${i}>${time[i]}</time${i}>
	<comment${i}>${comment[i]}</comment${i}>
</c:forEach>