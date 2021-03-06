package com.springbook.view.user;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springbook.biz.user.UserService;
import com.springbook.biz.user.UserVO;
import com.springbook.biz.user.impl.UserDAO;

@Controller
public class UserController {
	// 1. JoinController
	@Autowired
	private UserService userService;
	
	@RequestMapping(value="/join.do", method=RequestMethod.POST)
	public String jogin(UserVO vo) {
		System.out.println(vo); 
		int join = 0;
		join = userService.insertUserVO(vo);
		if(join == 1) {						//회원가입 성공
			return "redirect:home.do";
		} else {		
			return "/user/join";			//회원 가입 실패
		}
	}
	//다국어 처리 설정 때문에 Controller를 거쳐야 함.
	@RequestMapping(value="/joinPage.do", method=RequestMethod.GET)
	public String joinPage() {
		return "/user/join";
	}
	
	// 회원 개인 정보 조회
	@RequestMapping("/getUser.do")
	public String getUser(UserVO vo, Model model, HttpSession session) {
		String auth = (String)session.getAttribute("userName");
		if(auth == null && auth.equals("")) {	//유저 정보 세션에 없으면 로그인 창으로 이동
			return "redirect:login.do";
		} else {
			model.addAttribute("user", userService.getUserVO(vo));	//유저 정보 갖고 페이지 이동
			return "/user/joinInfo";
		}
	}
	
	@RequestMapping(value="/updateUser.do", method=RequestMethod.POST)
	public String updateUser(@ModelAttribute("user") UserVO vo, Model model
			, RedirectAttributes redirectAttributes, HttpSession session) {
		System.out.println("개인정보 수정");
		userService.updateUser(vo);
		redirectAttributes.addAttribute("id", session.getAttribute("userId") );
		return "redirect:getUser.do";
	}
	
	@RequestMapping("/deleteUser.do")
	public String deletUser(UserVO vo) {
		System.out.println("회원 탈퇴 처리");
		userService.deleteUser(vo);
		return "redirect:login.do";
	}
	
	// 2.loginController
	@RequestMapping(value="/login.do", method=RequestMethod.GET)
	public String loginView(@ModelAttribute("user") UserVO vo) {
		System.out.println("로그인 화면으로 이동");
		vo.setId("test");
		vo.setPassword("1234");
		return "/user/login";
	}

	@RequestMapping(value="/login.do", method=RequestMethod.POST)
	public String login(UserVO vo, UserDAO userDAO, HttpSession session) {
		System.out.println("로그인 인증 처리");
		
		if(vo.getId() == null || vo.getId().equals("")) {
			throw new IllegalArgumentException("아이디는 반드시 입력해야 합니다.");
		}
		if(vo.getPassword() == null || vo.getPassword().equals("")) {
			throw new IllegalArgumentException("패스워드는 반드시 입력해야 합니다.");
		}
		
		UserVO user = userDAO.getUser(vo);
		
		if(user != null) {
			session.setAttribute("userName", user.getName());
			session.setAttribute("userId", user.getId());
			return "redirect:home.do";
		} else {
			return "redirect:login.do";
		}
	}
	
	// 3. LogoutController
	@RequestMapping("/logout.do")
	public String logout(HttpSession session) {
		System.out.println("로그아웃 처리");

		session.invalidate();
		return "redirect:home.do";
	}
}
