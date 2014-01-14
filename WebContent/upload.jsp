<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="EUC-KR"%>
<%@ page import="java.io.*,com.oreilly.servlet.*,com.oreilly.servlet.multipart.*" %>
<%request.setCharacterEncoding("EUC-KR"); %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=EUC-KR">
<title>UPLOAD</title>
</head>
<body>
<%
	System.out.println("들어온거니??만거니??");
	String dir = application.getRealPath("/upload");
	System.out.println(dir);
	int max = 20*1024*1024;
	//최대크기 mx바이트, dir 디렉토리에 파일을 업로드하는 MultipartRequest
	//객체를 생성한다.
	System.out.println(request.toString());
	try{
		MultipartRequest m = new MultipartRequest(request, dir, max, "UTF-8", new DefaultFileRenamePolicy());
	}catch(Exception e){
		e.printStackTrace();
	}

%>

</body>
</html>