package com.controller;

import java.security.PrivateKey;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dao.PasswordEncode;
import com.service.ComService;

import annotation.maps.ComMap;
import annotation.maps.TestMappable;
import beans.UserBean;


@Controller 
@RequestMapping("/")
public class ComController {
	public static String RSA_INSTANCE="rsa";
	@Autowired TestMappable testMappable;
	@Autowired  ComMap cm;
	@Autowired PasswordEncode pe;
	@Autowired ComService cs;
	
	/*인터셉터*/
	@RequestMapping(value = {"/login_error"}, method = RequestMethod.GET)
	public String login_error() {
		return "login_error";
	}
	/*인터셉터*/
	
	/*회원가입*/
	//중복체크
	@RequestMapping(value = {"/register_check"}, method = RequestMethod.POST)
	public  @ResponseBody int registerCheck(UserBean ub) {
		return cs.registerCheck(ub);
	}
	//restful 회원가입
	@RequestMapping(value = {"/signup"}, method = RequestMethod.GET)
	public String signup(){	
		return "home";
	}
	//회원가입
	@RequestMapping(value = {"/signup"}, method = RequestMethod.POST)
	public String signup(UserBean ub,HttpSession session,MultipartFile avatar_img) throws Exception {
		System.out.println("컨트롤러 도착");
		try {
		cs.add_user(ub, session, avatar_img);
		}catch(Exception e) {	
			e.printStackTrace();
		}
		System.out.println("컨트롤러 종료");
		
		return "home";
	}
	/*테스팅*/
	@RequestMapping(value = { "/","/home"}, method = RequestMethod.GET)
	public String homePage(ModelMap m) {
		return "home";
	}
	//rsa암호화 키 발급
	@RequestMapping(value = {"/log"}, method = RequestMethod.GET)
	public @ResponseBody HashMap<String, String> module(HttpServletRequest request){
		return pe.initRsa(request);
	}
	//로그인
	@RequestMapping(value= {"/log"},method=RequestMethod.POST)
	public @ResponseBody int login(UserBean ub,HttpSession session) throws Exception {
		 PrivateKey privateKey = (PrivateKey) session.getAttribute(pe.getRsa_web_key());
		 System.out.println(ub);
		 	// 복호화
	        ub.setM_id(pe.decryptRsa(privateKey, ub.getM_id()));
	        ub.setM_pw(pe.decryptRsa(privateKey, ub.getM_pw()));
	        
	        //db push
	        try {
	        if(pe.match(ub.getM_pw(),(String) cm.login(ub).getM_pw())) {
	        	//ub에 회원정보 담기
	        	ub=cm.login(ub);
	        	cs.login_session(session, ub);
	        	System.out.println(ub);
	        	return 1;
	        }
	        }catch(NullPointerException e) {
	        	return 0;
	        }
	        
	    return 0;
	}
	//로그아웃
	@RequestMapping(value= {"/logout"},method=RequestMethod.GET)
	public @ResponseBody void logout(HttpSession session) {
		session.invalidate();
	}
	/*회원*/
/*
	@RequestMapping(value = {"/chat"}, method = RequestMethod.POST)
	public @ResponseBody void chat(ChatBean cb,HttpSession session) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		System.out.println((String)session.getAttribute("userId"));
		cb.setFromId((String)session.getAttribute("userId"));
		cs.sendChat(cb);
	}
	@RequestMapping(value= {"/chatlist"},method=RequestMethod.POST)
	public @ResponseBody List<Map<String, Object>> getChatList(ChatBean cb,HttpSession session){
		cb.setFromId((String) session.getAttribute("userId"));
		return cs.getChatListByRecent(cb,5);
	}
	*/
	/*채팅*/
	
	@RequestMapping(value = { "/k-drive"}, method = RequestMethod.GET)
	public String k_drive(ModelMap m) {
		return "k-drive";
	}	
}