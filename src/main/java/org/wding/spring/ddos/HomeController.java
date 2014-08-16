package org.wding.spring.ddos;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

	@RequestMapping({"/","/home"})
	@ResponseBody
	public String home(){
		return new Date().toGMTString();
	}
}
