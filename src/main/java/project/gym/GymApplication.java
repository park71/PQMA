package project.gym;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "project.gym.member") // 빈 스캔할 패키지 지정
public class GymApplication {



	public static void main(String[] args) {

		SpringApplication.run(GymApplication.class, args);
	}


}
