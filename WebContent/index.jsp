<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>Insert title here</title>
<script type="text/javascript">
// alert('${message}');
</script>
</head>
<body>
리턴받은 값<br/>
${message }
<br/><br/>
<form name="test" method="post" action="insert.do">
아하하하하하<input type="text" name="name">
<input type="submit" id="btn" value="insert">
<br>
<br>

<br/><br/>
</form>
<form name="find" method="post" action="find.do">
	<input name="find" type="submit" value="find"/>
</form>

<br/><br/>

<form name="update" method="post" action="update.do">
	<input name="updateText" type="text">
	<input type="submit" value="update">
</form>

</body>
</html>