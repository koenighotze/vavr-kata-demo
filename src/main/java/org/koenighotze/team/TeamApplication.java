package org.koenighotze.team;

import java.math.*;
import java.time.*;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;

@SpringBootApplication
public class TeamApplication {
    @Bean
    CommandLineRunner commandLineRunner(TeamRepository teamRepository) {
        return evt -> {
            teamRepository.deleteAll();
            teamRepository.save(new Team("F95", "http://", "", BigDecimal.ONE, LocalDate.of(1895, 1, 1)));
            teamRepository.save(new Team("FCK", "http://", "", BigDecimal.ONE, LocalDate.of(1895, 1, 1)));
            teamRepository.save(new Team("1FC", "http://", "", BigDecimal.ONE, LocalDate.of(1895, 1, 1)));
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(TeamApplication.class, args);
    }
}
