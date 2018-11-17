package de.gruppe2.agamoTTTo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@Slf4j
@SpringBootApplication
public class AgamoTTToApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AgamoTTToApplication.class, args);
	}
}
