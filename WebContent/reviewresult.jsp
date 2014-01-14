<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions"  prefix="fn"%>

<reviewcount>${reviewcount}</reviewcount>
 <c:forEach var="i" begin="0" step="1" end="${reviewcount}">
	<email${i}>${email[i]}</email${i}>
	<writername${i}>${writername[i]}</writername${i}>
	<text${i}>${text[i]}</text${i}>
	<time${i}>${time[i]}</time${i}>
	<logintype${i}>${logintype[i]}</logintype${i}>
	<isvideo${i}>${isvideo[i]}</isvideo${i}>
	<like${i}>${like[i]}</like${i}>
	<comment${i}>${comment[i]}</comment${i}>
	<placename${i}>${placename[i]}</placename${i}>
	<filelist${i}>
		<c:set var="file" value="${filelist[i]}"/>
		<c:set var="count" value="0"/>
		<filecount${i}>${fn:length(file)}</filecount${i}>
		<c:forEach var="fileitem" items="${file}">
			<file${count}>${fileitem}</file${count}>
			<c:set var="count" value="${count+1}"/>
		</c:forEach>
	</filelist${i}>
</c:forEach>
