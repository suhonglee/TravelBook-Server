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
	System.out.println("���°Ŵ�??���Ŵ�??");
	String dir = application.getRealPath("/upload");
	System.out.println(dir);
	int max = 20*1024*1024;
	//�ִ�ũ�� mx����Ʈ, dir ���丮�� ������ ���ε��ϴ� MultipartRequest
	//��ü�� �����Ѵ�.
	System.out.println(request.toString());
	try{
		MultipartRequest m = new MultipartRequest(request, dir, max, "UTF-8", new DefaultFileRenamePolicy());
	}catch(Exception e){
		e.printStackTrace();
	}

%>

</body>
</html>