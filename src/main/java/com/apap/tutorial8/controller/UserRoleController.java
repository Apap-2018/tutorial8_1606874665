package com.apap.tutorial8.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.apap.tutorial8.model.PasswordModel;
import com.apap.tutorial8.model.UserRoleModel;
import com.apap.tutorial8.service.UserRoleService;

@Controller
@RequestMapping("/user")
public class UserRoleController {
	@Autowired
	private UserRoleService userService;

	@RequestMapping( value = "/addUser", method = RequestMethod.POST)
	private String addUserSubmit(@ModelAttribute UserRoleModel user) {
		userService.addUser(user);
		return "home";
	}
	
	@RequestMapping( value = "/updatePass")
	private String updatePassword() {
		return "update-password";
	}
	
	@RequestMapping( value = "/submit", method = RequestMethod.POST)
	private ModelAndView updatePasswordSubmit(@ModelAttribute PasswordModel pass, Model model, RedirectAttributes redir) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		UserRoleModel user = userService.findUserByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
		String message = "";
		if (pass.getConfirmPassword().equals(pass.getNewPassword())) {
			if (passwordEncoder.matches(pass.getOldPassword(), user.getPassword())) {
				userService.changePassword(user, pass.getNewPassword());
				message = "password berhasil diubah";

				System.out.println("PESAN  cocok ama DB: " + message);
			}
			else {
				message = "password lama anda salah";

				System.out.println("PESAN Gak COcok AMa DB : " + message);
			}	
		}
		else {
			message = "password baru tidak sesuai";

			System.out.println("PESAN beda konfirmasi : " + message);
		}
		System.out.println("PESAN  terakhir :\"\"( : " + message);
		ModelAndView modelAndView = new ModelAndView("redirect:/user/updatePass");
		redir.addFlashAttribute("msg",message);
		return modelAndView;
	}
}	
