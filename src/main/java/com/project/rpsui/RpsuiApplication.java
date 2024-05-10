package com.project.rpsui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import com.project.rpsui.GUI.InstanceInfo;
import com.project.rpsui.GUI.LoginSignupPage;
import com.project.rpsui.GUI.MainMenu;

@ComponentScan(basePackages = "com.project.rpsui.GUI")
@SpringBootApplication
public class RpsuiApplication {
	
	
	public static void main(String[] args) {
		
		// LoginSignupPage loginSignupPage = new LoginSignupPage();
		InstanceInfo instanceInfo = new InstanceInfo();
		new LoginSignupPage(instanceInfo);
		// new LoginSignupPage();

		// If necessary, you can still run the Spring application context
        SpringApplication.run(RpsuiApplication.class, args);

		// SpringApplication.run(RpsuiApplication.class, args);
		// // LoginSignupPage loginSignupPage = new LoginSignupPage();
	}


}
