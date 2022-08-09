package com.teamside.project.alpha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.teamside.project")
@EnableJpaAuditing
public class SideProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(SideProjectApplication.class, args);
	}

}
