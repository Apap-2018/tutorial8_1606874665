package com.apap.tutorial8.controller;

import java.util.regex.Pattern;

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

	public boolean validatePassword (String pass) {
		if(pass.length() >= 8 && Pattern.compile("[a-zA-Z]").matcher(pass).find() && Pattern.compile("[0-9]").matcher(pass).find()) {
			return true;
		}
		return false;
	}
	
	@RequestMapping( value = "/addUser", method = RequestMethod.POST)
	private ModelAndView addUserSubmit(@ModelAttribute UserRoleModel user, RedirectAttributes redirect) {
		String message="";
		
		if(this.validatePassword(user.getPassword())) {
			userService.addUser(user);
			message= null;
		}
		else {
			message="password minimal terdiri dari 8 kata dengan minimal 1 huruf dan angka";
		}
		ModelAndView redirects = new ModelAndView("redirect:/");
		redirect.addFlashAttribute("msg", message);
		return redirects;
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
				if(this.validatePassword(pass.getNewPassword())) {
					String passBaru = pass.getNewPassword();
					userService.changePassword(user, passBaru);
					message = "password berhasil diubah";
				}
				else {
					message="password harus terdiri dari 8 karakter dengan minimal 1 angka dan huruf";
				}
				//System.out.println("PESAN  cocok ama DB: " + message);
			}
			else {
				message = "password lama anda salah";
				//System.out.println("PESAN Gak COcok AMa DB : " + message);
			}	
		}
		else {
			message = "password baru tidak sesuai";
			//System.out.println("PESAN beda konfirmasi : " + message);
		}
		
		//System.out.println("PESAN  terakhir :\"\"( : " + message);
		ModelAndView modelAndView = new ModelAndView("redirect:/user/updatePass");
		redir.addFlashAttribute("msg",message);
		return modelAndView;
	}
}	
