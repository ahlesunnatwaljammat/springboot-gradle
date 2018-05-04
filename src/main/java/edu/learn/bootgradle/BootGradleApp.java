package edu.learn.bootgradle;

import edu.learn.entities.User;
import edu.learn.repos.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@Slf4j
@SpringBootApplication
@EntityScan(basePackages = {"edu.learn.entities"})
@EnableReactiveMongoRepositories(basePackages = {"edu.learn.repos"})
public class BootGradleApp {
    @Bean
    public CommandLineRunner addSampleData(UserRepo userRepo){
        return args -> {
            userRepo.save(new User("nabbasi","x")).block();
            userRepo.findByUsername("nabbasi").subscribe(user -> log.info(user.toString()));
            User user = userRepo.findByUsername("nabbasi").block();
            log.info(user.toString());
        };
    }

	public static void main(String[] args) {
		SpringApplication.run(BootGradleApp.class, args);
	}
}
