package keti.sgs.exec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"keti.*"})
public class KetiServerLauncher {
  public static void main(String[] args) {
    SpringApplication.run(KetiServerLauncher.class, args);
  }
}
