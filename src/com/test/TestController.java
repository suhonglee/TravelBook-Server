package com.test;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

@Controller
public class TestController {

	String message;
	Mongo m ;
	DB db;
	public TestController()
	{
		try
		{
			m = new Mongo("localhost",9000);
			db = m.getDB("test");
		}
		catch(Exception e)
		{
			System.out.println("contructor exception mongo..");
		}
	}
	
	@RequestMapping("/findLatLng.do")
	public ModelAndView findLatLng(HttpServletRequest request)
	{
		String email = request.getParameter("email");
		String time = request.getParameter("time");
		System.out.println("email :: " + email);
		System.out.println("time :: " + time);
		
		DBCollection colls = db.getCollection("review");
		
		BasicDBObject object = new BasicDBObject();
		object.put("email",email);
		object.put("time",time);
		
		DBCursor cursor = colls.find(object);
		while(cursor.hasNext())
		{
			DBObject obj = cursor.next();
			request.setAttribute("latitude", obj.get("latitude"));
			request.setAttribute("longitude",obj.get("longitude"));
		}
		return new ModelAndView("findLatLngResult.jsp");
	}
	@RequestMapping("/userLogin.do")
	public ModelAndView userLogin (HttpServletRequest request)
	{
		try {
			System.out.println("컨트롤러 :: 로그인 하러 옴...");
			request.setCharacterEncoding("UTF-8");
			String email = request.getParameter("email");
			String password = request.getParameter("password");
			
			DBCollection colls = db.getCollection("user");
			
			BasicDBObject object = new BasicDBObject("email",email);
			DBCursor cursor = colls.find(object);
			if(cursor.count()==0)
			{
				request.setAttribute("result", "failed_email");
			}
			else
			{
				DBObject result = cursor.next();
				String resultPassword = (String) result.get("password");
				if(password.equals(resultPassword))
				{
					request.setAttribute("result", "success");
					request.setAttribute("name", result.get("name"));
				}
				else
				{
					request.setAttribute("result", "failed_password");
				}
			}
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return new ModelAndView("loginresult.jsp");
	}
	@RequestMapping("/userAccount.do")
	public ModelAndView userAccount(HttpServletRequest request){
		try{
			
			request.setCharacterEncoding("UTF-8");
			String name = request.getParameter("name");
			String email = request.getParameter("email");
			String password = request.getParameter("password");

			DBCollection colls = db.getCollection("user");
			
			BasicDBObject object = new BasicDBObject("email", email); 
			DBCursor cursor = colls.find(object);
			if(cursor.count()==0)
			{
				request.setAttribute("result", "success");
				BasicDBObject doc = new BasicDBObject();
				doc.put("name", name);
				doc.put("email", email);
				doc.put("password", password);
				
				colls.insert(doc);
			}
			else
			{
				request.setAttribute("result", "failed_email");
			}
		}catch(Exception e){
			System.out.println("Unknown Host!!!");
		}
		return new ModelAndView("loginresult.jsp");
	}
	
	@RequestMapping("/writeComment.do")
	public ModelAndView writeComment(HttpServletRequest request)
	{
			try {
				System.out.println("코멘트 등록");
				request.setCharacterEncoding("UTF-8");
				String email = request.getParameter("email");
				String name = request.getParameter("name");
				String comment = request.getParameter("comment");
				String time = request.getParameter("time");
				String writerEmail = request.getParameter("writeremail");
				String writerTime = request.getParameter("writertime");
				
				DBCollection colls = db.getCollection("comment");
				
				BasicDBObject object = new BasicDBObject();
				object.put("email", email);
				object.put("name",name);
				object.put("comment",comment);
				object.put("time",time);
				object.put("writeremail",writerEmail);
				object.put("writertime",writerTime);
				
				colls.insert(object);
				request.setAttribute("result", "success");
			} catch (UnsupportedEncodingException e) {
				request.setAttribute("result","failed");
				e.printStackTrace();
			}
		return new ModelAndView("/commentresult.jsp");
	}
	
	@RequestMapping("/readComment.do")
	public ModelAndView readComment(HttpServletRequest request)
	{
		try {
			request.setCharacterEncoding("UTF-8");
			String time = request.getParameter("time");
			String email = request.getParameter("email");
			
			DBCollection colls = db.getCollection("comment");
			
			BasicDBObject obj = new BasicDBObject();
			obj.put("writertime", time);
			obj.put("writeremail", email);
			
			DBCursor cursor = colls.find(obj);
			
			ArrayList<String> commentEmail = new ArrayList<String>();
			ArrayList<String> commentName = new ArrayList<String>();
			ArrayList<String> commentTime = new ArrayList<String>();
			ArrayList<String> commentComment = new ArrayList<String>();
			
			while(cursor.hasNext())
			{
				DBObject object = cursor.next();
				commentEmail.add((String) object.get("email"));
				commentName.add((String)object.get("name"));
				commentTime.add((String)object.get("time"));
				commentComment.add((String) object.get("comment"));
			}
			request.setAttribute("email", commentEmail);
			request.setAttribute("name",commentName);
			request.setAttribute("time",commentTime);
			request.setAttribute("comment", commentComment);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new ModelAndView("/readcomment.jsp");
	}
	
	@RequestMapping("/deleteComment.do")
	public ModelAndView deleteComment(HttpServletRequest request)
	{
		String email = request.getParameter("email");
		String time = request.getParameter("time");
		String writeremail = request.getParameter("writeremail");
		String writertime = request.getParameter("writertime");
		
		System.out.println("delete Comment ::::: ");
		System.out.println("email ::: " + email);
		System.out.println("time ::: " + time);
		System.out.println("writerEmail :::: " + writeremail);
		System.out.println("writerTime ;::: " + writertime);
					DBCollection colls = db.getCollection("comment");
					
					BasicDBObject obj = new BasicDBObject();
					obj.put("email", email);
					obj.put("time",time);
					obj.put("writeremail",writeremail);
					obj.put("writertime",writertime);
					
					DBCursor cursor = colls.find(obj);
					while(cursor.hasNext())
					{
						DBObject ob = cursor.next();
						System.out.println("OB GET EMAIL ;:: " + ob.get("email"));
						System.out.println("OB GET COMMENT ::: " + ob.get("comment"));
						System.out.println("OB GET writeremail ::: " + ob.get("writeremail"));
						System.out.println("OB GET writerTime ::: " + ob.get("writertime"));
					}
					colls.remove(obj);
					request.setAttribute("result","success");
		
		return new ModelAndView("/writeresult.jsp");
	}
	
	@RequestMapping("/readMyReview.do")
	public ModelAndView readMyReview(HttpServletRequest request)
	{
		try {
			request.setCharacterEncoding("UTF-8");
			String email = request.getParameter("email");
//			m = new Mongo("192.168.0.20",9000);
//			DB db = m.getDB("test");
			DBCollection colls = db.getCollection("review");
			
			BasicDBObject object = new BasicDBObject();
			object.put("email", email);
			
			BasicDBObject order = new BasicDBObject();
			order.put("time", -1);
			DBCursor cursor = colls.find(object).sort(order);
			ArrayList<String> userNameList = new ArrayList<String>();
			ArrayList<String> emailList = new ArrayList<String>();
			ArrayList<String> bodyList = new ArrayList<String>();
			ArrayList<String> timeList = new ArrayList<String>();
			ArrayList<String> logintypeList = new ArrayList<String>();
			ArrayList<ArrayList> fileList = new ArrayList<ArrayList>();
			ArrayList<Integer> filesize = new ArrayList<Integer>();
			ArrayList<String> isvideoList = new ArrayList<String>();
			ArrayList<Integer> likeList = new ArrayList<Integer>();
			ArrayList<Integer> commentList = new ArrayList<Integer>();
			ArrayList<String> writerList = new ArrayList<String>();
			ArrayList<String> placenameList = new ArrayList<String>();
			while(cursor.hasNext())
			{
				DBObject obj = cursor.next();
				userNameList.add((String) obj.get("username"));
				emailList.add((String) obj.get("email"));
				writerList.add((String)obj.get("writername"));
				bodyList.add((String) obj.get("body"));
				timeList.add((String) obj.get("time"));
				logintypeList.add((String) obj.get("logintype"));
				isvideoList.add((String) obj.get("isvideo"));
				placenameList.add((String)obj.get("placename"));
				int like = (Integer)obj.get("like");
				likeList.add(like);
				
				ArrayList files = new ArrayList();
				for(int z=0;z< (int)obj.get("size");z++)
				{
					files.add(obj.get("filename"+z));
				}
				filesize.add(files.size());
				fileList.add(files); 
				
				object = new BasicDBObject();
				object.put("writeremail",obj.get("email"));
				object.put("writertime", obj.get("time"));
				DBCursor c = colls.find(object);
				int commentcount=0;
				while(c.hasNext())
				{
					c.next();
					commentcount++;
				}
				commentList.add(commentcount);
			}
			request.setAttribute("reviewcount", emailList.size()-1);
			request.setAttribute("email",emailList);
			request.setAttribute("writername", writerList);
			request.setAttribute("text",bodyList);
			request.setAttribute("time",timeList);
			request.setAttribute("logintype",logintypeList);
			request.setAttribute("filelist",fileList);
			request.setAttribute("filesize", filesize);
			request.setAttribute("isvideo",isvideoList);
			request.setAttribute("like",likeList);
			request.setAttribute("comment",commentList);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		finally
//		{
//			if(m!=null)
//				m.close();
//		}
		return new ModelAndView("/reviewresult.jsp");
		
	}
	
	@RequestMapping("/readMyScrap.do")
	public ModelAndView readMyScrap(HttpServletRequest request)
	{
		try {
			request.setCharacterEncoding("UTF-8");
			String email = request.getParameter("email");
//			m = new Mongo("192.168.0.20",9000);
//			DB db = m.getDB("test");
			DBCollection colls = db.getCollection("scrap");
			
			BasicDBObject object = new BasicDBObject();
			object.put("email", email);
			
			BasicDBObject order = new BasicDBObject();
			order.put("time", -1);
			DBCursor cursor = colls.find(object).sort(order);
			ArrayList<String> userNameList = new ArrayList<String>();
			ArrayList<String> emailList = new ArrayList<String>();
			ArrayList<String> bodyList = new ArrayList<String>();
			ArrayList<String> timeList = new ArrayList<String>();
			ArrayList<String> logintypeList = new ArrayList<String>();
			ArrayList<ArrayList> fileList = new ArrayList<ArrayList>();
			ArrayList<Integer> filesize = new ArrayList<Integer>();
			ArrayList<String> isvideoList = new ArrayList<String>();
			ArrayList<Integer> likeList = new ArrayList<Integer>();
			ArrayList<Integer> commentList = new ArrayList<Integer>();
			ArrayList<String> writerList = new ArrayList<String>();
			ArrayList<String> placenameList = new ArrayList<String>();
			while(cursor.hasNext())
			{
				DBObject obj = cursor.next();
				BasicDBObject dbObj = new BasicDBObject();
				dbObj.put("email", obj.get("writeremail"));
				dbObj.put("time", obj.get("writetime"));
				
				DBCollection collection = db.getCollection("review");
				DBCursor cs = collection.find(dbObj);
				
				if(cs.hasNext())
				{
					obj = cs.next();
					userNameList.add((String) obj.get("username"));
					emailList.add((String) obj.get("email"));
					writerList.add((String) obj.get("writername"));
					bodyList.add((String) obj.get("body"));
					timeList.add((String) obj.get("time"));
					logintypeList.add((String) obj.get("logintype"));
					isvideoList.add((String) obj.get("isvideo"));
					placenameList.add((String)obj.get("placename"));
					int like = (Integer)obj.get("like");
					if(like<1)
						like=0;
					likeList.add(like);
				}
				
				ArrayList files = new ArrayList();
				
				Object sizeObj = obj.get("size");
				
				int forFileSize=0;
				if(sizeObj!=null)
					forFileSize=(int)sizeObj;
				for(int z=0;z< forFileSize;z++)
				{
					files.add(obj.get("filename"+z));
				}
				filesize.add(files.size());
				fileList.add(files); 
				
				object = new BasicDBObject();
				object.put("writeremail",obj.get("email"));
				object.put("writertime", obj.get("time"));
				DBCursor c = colls.find(object);
				int commentcount=0;
				while(c.hasNext())
				{
					c.next();
					commentcount++;
				}
				commentList.add(commentcount);
			}
			request.setAttribute("reviewcount", -1);
			if(emailList.size()!=0)
				request.setAttribute("reviewcount", emailList.size()-1);
			request.setAttribute("email",emailList);
			request.setAttribute("writername", writerList);
			request.setAttribute("text",bodyList);
			request.setAttribute("time",timeList);
			request.setAttribute("logintype",logintypeList);
			request.setAttribute("filelist",fileList);
			request.setAttribute("filesize", filesize);
			request.setAttribute("isvideo",isvideoList);
			request.setAttribute("like",likeList);
			request.setAttribute("comment",commentList);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		finally
//		{
//			if(m!=null)
//				m.close();
//		}
		return new ModelAndView("/reviewresult.jsp");
		
	}
	@RequestMapping("/readReview.do")
	public ModelAndView readReview(HttpServletRequest request)
	{
		try {
			 
			request.setCharacterEncoding("UTF-8");
			String latitude = request.getParameter("latitude");
			String longitude = request.getParameter("longitude");
//			m = new Mongo("192.168.0.20",9000);
//			DB db = m.getDB("test");
			DBCollection colls = db.getCollection("review");
			 
			BasicDBObject object = new BasicDBObject();
			object.put("latitude", Double.parseDouble(latitude));
			object.put("longitude",Double.parseDouble(longitude));
			
			BasicDBObject order = new BasicDBObject();
			order.put("time", -1);
			DBCursor cursor = colls.find(object).sort(order);
			 
			ArrayList<String> emailList = new ArrayList<String>();
			ArrayList<String> bodyList = new ArrayList<String>();
			ArrayList<String> timeList = new ArrayList<String>();
			ArrayList<String> logintypeList = new ArrayList<String>();
			ArrayList<ArrayList> fileList = new ArrayList<ArrayList>();
			ArrayList<Integer> filesize = new ArrayList<Integer>();
			ArrayList<String> isvideoList = new ArrayList<String>();
			ArrayList<Integer> likeList = new ArrayList<Integer>();
			ArrayList<Integer> commentList = new ArrayList<Integer>();
			ArrayList<String> writerList = new ArrayList<String>();
			ArrayList<String> placenameList = new ArrayList<String>();
			
			colls = db.getCollection("comment");
			 
			while(cursor.hasNext())
			{
				DBObject obj = cursor.next();
				emailList.add((String) obj.get("email"));
				writerList.add((String)obj.get("writername"));
				bodyList.add((String) obj.get("body"));
				timeList.add((String) obj.get("time"));
				placenameList.add((String)obj.get("placename"));
				logintypeList.add((String) obj.get("logintype"));
				isvideoList.add((String) obj.get("isvideo"));
				int like = (Integer)obj.get("like");
				 
				if(like<1)
					like=0;
				likeList.add(like);
				 
				
				
				ArrayList files = new ArrayList();
				for(int z=0;z< (int)obj.get("size");z++)
				{
					files.add(obj.get("filename"+z));
				}
				 
				filesize.add(files.size());
				fileList.add(files); 
				
				
				object = new BasicDBObject();
				object.put("writeremail",obj.get("email"));
				object.put("writertime", obj.get("time"));
				 
				DBCursor c = colls.find(object);
				 
				int commentcount=0;
				while(c.hasNext())
				{
					 
					c.next();
					commentcount++;
				}
				commentList.add(commentcount);
				 
			}
			request.setAttribute("reviewcount", emailList.size()-1);
			request.setAttribute("email",emailList);
			request.setAttribute("writername", writerList);
			request.setAttribute("text",bodyList);
			request.setAttribute("time",timeList);
			request.setAttribute("logintype",logintypeList);
			request.setAttribute("filelist",fileList);
			request.setAttribute("filesize", filesize);
			request.setAttribute("isvideo",isvideoList);
			request.setAttribute("like",likeList);
			request.setAttribute("comment",commentList);
			request.setAttribute("placename",placenameList);
			
			System.out.println("Controller readReview end...");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		finally
//		{
//			if(m!=null)
//				m.close();
//		}
		return new ModelAndView("/reviewresult.jsp");
		
	}
	
	@RequestMapping("/readNearReview.do")
	public ModelAndView readNearReview(HttpServletRequest request)
	{
		try {
			 
			request.setCharacterEncoding("UTF-8");
			String latitude = request.getParameter("latitude");
			String longitude = request.getParameter("longitude");
			
			System.out.println("latitude ::: " + latitude);
			System.out.println("longitude ::: " + longitude);
//			m = new Mongo("192.168.0.20",9000);
//			DB db = m.getDB("test");
			DBCollection colls = db.getCollection("review");
			 
			BasicDBObject object = new BasicDBObject();
			object.put("latitude", new BasicDBObject("$gte",Double.parseDouble(latitude)-0.005).append("$lte", Double.parseDouble(latitude)+0.005));
			object.put("longitude", new BasicDBObject("$gte",Double.parseDouble(longitude)-0.005).append("$lte", Double.parseDouble(longitude)+0.005));
			
			BasicDBObject order = new BasicDBObject();
			order.put("time", -1);
			DBCursor cursor = colls.find(object).sort(order);
			 
			ArrayList<String> emailList = new ArrayList<String>();
			ArrayList<String> bodyList = new ArrayList<String>();
			ArrayList<String> timeList = new ArrayList<String>();
			ArrayList<String> logintypeList = new ArrayList<String>();
			ArrayList<ArrayList> fileList = new ArrayList<ArrayList>();
			ArrayList<Integer> filesize = new ArrayList<Integer>();
			ArrayList<String> isvideoList = new ArrayList<String>();
			ArrayList<Integer> likeList = new ArrayList<Integer>();
			ArrayList<Integer> commentList = new ArrayList<Integer>();
			ArrayList<String> writerList = new ArrayList<String>();
			ArrayList<String> placenameList = new ArrayList<String>();
			colls = db.getCollection("comment");
			 
			while(cursor.hasNext())
			{
				DBObject obj = cursor.next();
				emailList.add((String) obj.get("email"));
				writerList.add((String)obj.get("writername"));
				bodyList.add((String) obj.get("body"));
				timeList.add((String) obj.get("time"));
				placenameList.add((String)obj.get("placename"));
				logintypeList.add((String) obj.get("logintype"));
				isvideoList.add((String) obj.get("isvideo"));
				int like = (Integer)obj.get("like");
				 
				if(like<1)
					like=0;
				likeList.add(like);
				 
				
				
				ArrayList files = new ArrayList();
				for(int z=0;z< (int)obj.get("size");z++)
				{
					files.add(obj.get("filename"+z));
				}
				 
				filesize.add(files.size());
				fileList.add(files); 
				
				
				object = new BasicDBObject();
				object.put("writeremail",obj.get("email"));
				object.put("writertime", obj.get("time"));
				 
				DBCursor c = colls.find(object);
				 
				int commentcount=0;
				while(c.hasNext())
				{
					 
					c.next();
					commentcount++;
				}
				commentList.add(commentcount);
				 
			}
			request.setAttribute("reviewcount", emailList.size()-1);
			request.setAttribute("email",emailList);
			request.setAttribute("writername", writerList);
			request.setAttribute("text",bodyList);
			request.setAttribute("time",timeList);
			request.setAttribute("logintype",logintypeList);
			request.setAttribute("filelist",fileList);
			request.setAttribute("filesize", filesize);
			request.setAttribute("isvideo",isvideoList);
			request.setAttribute("like",likeList);
			request.setAttribute("comment",commentList);
			request.setAttribute("placename",placenameList);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		finally
//		{
//			if(m!=null)
//				m.close();
//		}
		return new ModelAndView("/reviewresult.jsp");
		
	}
	
	@RequestMapping("/deleteReview.do")
	public ModelAndView deleteReview(HttpServletRequest request)
	{
		String email = request.getParameter("email");
		String time = request.getParameter("time");
		
		//			m = new Mongo("192.168.0.20",9000);
//					DB db = m.getDB("test");
					DBCollection colls = db.getCollection("review");
					
					BasicDBObject obj = new BasicDBObject();
					obj.put("email", email);
					obj.put("time",time);
					
					colls.remove(obj);
					request.setAttribute("result","success");
		
		return new ModelAndView("/writeresult.jsp");
	}
	
	@RequestMapping("/userInfo.do")
	public ModelAndView userInfo(HttpServletRequest request)
	{
		String email = request.getParameter("email");
//			m = new Mongo("192.168.0.20",9000);
//		DB db = m.getDB("test");
		DBCollection colls = db.getCollection("user");
		
		BasicDBObject obj = new BasicDBObject();
		obj.put("email", email);
		
		DBCursor cursor = colls.find(obj);
		while(cursor.hasNext())
		{
			DBObject object = cursor.next();
			String name = (String) object.get("name");
			email = (String) object.get("email");
			String password = (String) object.get("password");
			
			request.setAttribute("name", name);
			request.setAttribute("email", email);
			request.setAttribute("password", password);
		}
//		finally
//		{
//			if(m!=null)
//				m.close();
//		}
		return new ModelAndView("userinfo.jsp");
	}
	
	@RequestMapping("/scrapReview.do")
	public ModelAndView scrapReview(HttpServletRequest request)
	{
//		DB db = m.getDB("test");
		DBCollection colls = db.getCollection("scrap");
		BasicDBObject object = new BasicDBObject();
		
		String email = request.getParameter("email");
		String writerEmail = request.getParameter("writeremail");
		String writeTime = request.getParameter("writetime");
		
		object.put("email", email);
		object.put("writeremail",writerEmail);
		object.put("writetime",writeTime);
		
		if(colls.find(object).count()==0)
			colls.insert(object);
		request.setAttribute("result", "success");
//		finally
//		{
//			if(m!=null)
//				m.close();
//		}
		return new ModelAndView("writeresult.jsp");
		
	}
	@RequestMapping("/likeReview.do")
	public ModelAndView likeReview(HttpServletRequest request)
	{
//		DB db = m.getDB("test");
		DBCollection colls = db.getCollection("review");
		BasicDBObject obj = new BasicDBObject();
		obj.put("email", request.getAttribute("email"));
		obj.put("time",request.getAttribute("time"));
		
		DBCollection collc = db.getCollection("like");
		BasicDBObject o = new BasicDBObject();
		o.put("email",request.getParameter("liker_email"));
		o.put("writeremail",request.getParameter("email"));
		o.put("writetime",request.getParameter("time"));
		System.out.println("liker email :: " + request.getParameter("liker_email"));
		System.out.println("writer email :: " + request.getParameter("email"));
		System.out.println("write time :: " + request.getParameter("time"));
		if(collc.find(o).count()==0)
		{
			collc.insert(o);
			
			BasicDBObject find = new BasicDBObject();
			find.put("email", request.getParameter("email"));
			find.put("time",request.getParameter("time"));
			
			DBCursor cursor = colls.find(find);
			BasicDBObject target = (BasicDBObject) cursor.next();
			
			BasicDBObject setDoc = new BasicDBObject();
			setDoc.append("like", ((int)target.get("like"))+1);
			BasicDBObject like = new BasicDBObject("$set", setDoc);
			colls.update(target, like);
			
			request.setAttribute("result", "plus_success");
		}
		else
		{
			collc.remove(o);
			
			BasicDBObject find = new BasicDBObject();
			find.put("email", request.getParameter("email"));
			find.put("time",request.getParameter("time"));
			
			DBCursor cursor = colls.find(find);
			BasicDBObject target = (BasicDBObject) cursor.next();
			
			BasicDBObject setDoc = new BasicDBObject();
			setDoc.append("like", ((int)target.get("like"))-1);
			BasicDBObject like = new BasicDBObject("$set", setDoc);
			colls.update(target, like);
			
			request.setAttribute("result", "minus_success");
		}
//		finally
//		{
//			if(m!=null)
//				m.close();
//		}
		return new ModelAndView("/writeresult.jsp");
	}
	
	@RequestMapping("/bookSearch.do")
	public ModelAndView bookSearch(HttpServletRequest request)
	{
//		DB db = m.getDB("test");
		DBCollection colls = db.getCollection("book");
		
		DBCursor cursor = colls.find();
		
		ArrayList<Double> latitudeList = new ArrayList<Double>();
		ArrayList<Double> longitudeList = new ArrayList<Double>();
		ArrayList<String> addressList = new ArrayList<String>();
		
		while(cursor.hasNext())
		{
			DBObject obj = cursor.next();
			Double latitude = (Double) obj.get("latitude");
			Double longitude = (Double) obj.get("longitude");
			String address = (String) obj.get("address");
			
			latitudeList.add(latitude);
			longitudeList.add(longitude);
			addressList.add(address);
		}
		
		request.setAttribute("latitude", latitudeList);
		request.setAttribute("longitude",longitudeList);
		request.setAttribute("address", addressList);
//		finally
//		{
//			if(m!=null)
//				m.close();
//		}
		return new ModelAndView("/bookresult.jsp");
		
	}
	
	@RequestMapping("/uploadProfile.do")
	public ModelAndView uploadProfile(HttpServletRequest request)
	{
		String dir=null;
		MultipartRequest mr =null;
		
		try {
			request.setCharacterEncoding("UTF-8");
			dir = request.getRealPath("/profile/"+request.getHeader("email"))+"/";
			if(!new File(dir).exists())
			{
				new File(dir).mkdirs();
			}
			int max=100*1024*1024;
			mr = new MultipartRequest(request,dir,max,"UTF-8");
			
			Enumeration e = mr.getFileNames();
			while(e.hasMoreElements())
			{
				String fileName = mr.getFilesystemName((String)e.nextElement());
				System.out.println("파일네임 : " +fileName);
				File oldFile = new File(dir + fileName);
				System.out.println(oldFile.exists());
				String after = "profile.jpg";
				File newFile = new File(dir + after);
				newFile.delete();
				newFile = new File(dir + after);
				oldFile.renameTo(newFile);
				
				Image srcImg = ImageIO.read(newFile);
				
				
				int srcWidth = srcImg.getWidth(null);
				int srcHeight = srcImg.getHeight(null);
				
				int width = 500;
				int height = (int) (srcHeight/((double)srcWidth/500));
				
//				if(height>500)
//				{
//					height = 500;
//				}
				
				int destWidth = -1, destHeight = -1;
				if(width<0){
					destWidth = srcWidth;
				}
				else if(width > 0){
					destWidth = width;
				}
				if(height<0){
					destHeight = srcHeight;
				}else if(height > 0){
					destHeight = height;
				}
				Image imgTarget = srcImg.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH);
				int pixels[] = new int[destWidth*destHeight];
				PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, destWidth, destHeight, pixels, 0, destWidth);
			
				pg.grabPixels();
				BufferedImage destImg = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_BGR);
				destImg.setRGB(0, 0, destWidth, destHeight, pixels, 0, destWidth);
				ImageIO.write(destImg, "jpg", newFile);
			} 
			String email = mr.getParameter("email");
			String userName = mr.getParameter("username");
			String password = mr.getParameter("password");
			
//			m = new Mongo("192.168.0.20",9000);
//			DB db = m.getDB("test");
			DBCollection colls = db.getCollection("user");
			
			BasicDBObject obj1 = new BasicDBObject();
			obj1.put("email",email);
			
			BasicDBObject obj2 = new BasicDBObject();
			obj2.put("email", email);
			obj2.put("name",userName);
			obj2.put("password",password);
			
			colls.update(obj1, obj2);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		finally
		{
//			if(m!=null)
//				m.close();
		}
		return null;
	}

	@RequestMapping("/writeReview.do")
	public ModelAndView upload(HttpServletRequest request){
		String dir=null;
		MultipartRequest mr=null;
		String hwakjangja = null;
		try{
			request.setCharacterEncoding("UTF-8");
			dir= request.getRealPath("/upload/"+request.getHeader("email"))+"/";
			if(!new File(dir).exists()){
				new File(dir).mkdirs();
			}
			int max = 100*1024*1024;
			//최대크기 mx바이트, dir 디렉토리에 파일을 업로드하는 MultipartRequest
			//객체를 생성한다.
			
			request.setCharacterEncoding("UTF-8");
			mr = new MultipartRequest(request, dir, max, "UTF-8", new DefaultFileRenamePolicy());
			
			Enumeration e = mr.getFileNames();
			
			int temp=0;
			BasicDBObject object = new BasicDBObject();
			
			while(e.hasMoreElements())
			{
				String before = mr.getFilesystemName((String)e.nextElement());
				int whereismydot = before.indexOf('.');
				hwakjangja = before.substring(whereismydot);
				File oldFile = new File(dir + before);
				System.out.println(oldFile.exists());
				String after = mr.getParameter("filename"+temp);
				File newFile = new File(dir + after + hwakjangja);
				System.out.println("???"+oldFile.renameTo(newFile));
				
				
				String ThumbnailFileName = dir+"Thumbnail_"+mr.getParameter("filename"+temp)+hwakjangja;
				try {
					File destFile = new File(ThumbnailFileName);
					File orgFile = new File(dir + after + hwakjangja);
					Image srcImg = ImageIO.read(orgFile);
					
					
					int srcWidth = srcImg.getWidth(null);
					int srcHeight = srcImg.getHeight(null);
					
					int width = 500;
					int height = (int) (srcHeight/((double)srcWidth/500));
					
					int destWidth = -1, destHeight = -1;
					if(width<0){
						destWidth = srcWidth;
					}
					else if(width > 0){
						destWidth = width;
					}
					if(height<0){
						destHeight = srcHeight;
					}else if(height > 0){
						destHeight = height;
					}
					Image imgTarget = srcImg.getScaledInstance(destWidth, destHeight, Image.SCALE_SMOOTH);
					int pixels[] = new int[destWidth*destHeight];
					PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, destWidth, destHeight, pixels, 0, destWidth);
				
					pg.grabPixels();
					BufferedImage destImg = new BufferedImage(destWidth, destHeight, BufferedImage.TYPE_INT_BGR);
					destImg.setRGB(0, 0, destWidth, destHeight, pixels, 0, destWidth);
					System.out.println("hwakjangja.substring(1) : "+ hwakjangja.substring(1));
					ImageIO.write(destImg, hwakjangja.substring(1), destFile);
				} catch (Exception e2) {
					// TODO: handle exception
					e2.printStackTrace();
				}
				if(new File(ThumbnailFileName).exists()){
					System.out.println("오 있다!!");
				}else{
					System.out.println("없다 ㅠㅠ");
				}
				object.put("filename"+temp, "upload/"+mr.getParameter("email")+"/"+mr.getParameter("filename"+temp)+hwakjangja);
				
				temp++;
			}
//			m = new Mongo("192.168.0.20",9000); 
			
			
			int wherewhich =0;
			DBCollection books = db.getCollection("book");
			BasicDBObject obj = new BasicDBObject();
			obj.put("latitude", new BasicDBObject("$gte",Double.parseDouble(mr.getParameter("latitude"))-0.005).append("$lte", Double.parseDouble(mr.getParameter("latitude"))+0.005));
			obj.put("longitude", new BasicDBObject("$gte",Double.parseDouble(mr.getParameter("longitude"))-0.005).append("$lte", Double.parseDouble(mr.getParameter("longitude"))+0.005));
			DBCursor c = books.find(obj);
			boolean haveBook = false;
			while(c.hasNext())
			{
				haveBook = true;
				break;
			}
			obj = new BasicDBObject();
			obj.put("latitude", Double.parseDouble(mr.getParameter("latitude")));
			obj.put("longitude", Double.parseDouble(mr.getParameter("longitude")));
			if(! haveBook)
			{
				obj.put("address", "Near Reviews I have!");
				books.save(obj);
			}
			DBCollection colls = db.getCollection("review");
			if(mr.getParameter("isvideo")!= null)
			{
				object.put("isvideo","true");
				object.put("size", Integer.parseInt(mr.getParameter("size")+1));
			}
			else
			{
				object.put("isvideo","false");
				object.put("size", Integer.parseInt(mr.getParameter("size")));
			}
			object.put("placename",mr.getParameter("placename"));
			object.put("email", mr.getParameter("email"));
			object.put("logintype", mr.getParameter("logintype"));
			object.put("writername", mr.getParameter("writername"));
			object.put("body", mr.getParameter("body"));
			object.put("time", mr.getParameter("time"));
			object.put("latitude",Double.parseDouble(mr.getParameter("latitude")));
			object.put("longitude",Double.parseDouble(mr.getParameter("longitude")));
			object.put("like", 0);
			colls.save(object);
			request.setAttribute("result", "success");
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("result", "failed");
		}finally{
//			if(m!=null)
//				m.close();
			dir+="\\"+request.getHeader("filename");
			if(request.getHeader("length")!=null)
			{
				long length = Integer.parseInt(request.getHeader("length"));
				System.out.println("dir : "+dir + "length : "+ length);
				File file = new File(dir);
				
				//not works
				if(file.exists()){
					if(file.length()!=length){
						file.delete();
						if(!file.exists()){
						}
					}else{
						
					}
				}
			}
		}
//		System.out.println(m.getFile("images"));
		return new ModelAndView("writeresult.jsp");
	}
	
}
