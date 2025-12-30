package com.smartMunicipal.Smart.Municipal.Services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartMunicipalServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartMunicipalServicesApplication.class, args);

        //todo can add public announcement in dashboard or email(Implement a email service and use it in admin service to announce the public announcements)
        //todo can include citizen feedback to the complaint raised
        //todo display the waste collection schedule via dashboard(static)
	}

}
