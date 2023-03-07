package com.portfolio.sb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MainController {
	
	@GetMapping({"", "/"})
	public String root(HttpServletResponse re) {
		return "redirect:/question/list";
	}
}
