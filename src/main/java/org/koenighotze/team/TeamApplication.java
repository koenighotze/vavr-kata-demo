package org.koenighotze.team;

import static java.util.UUID.randomUUID;

import java.math.*;
import java.time.*;

import com.fasterxml.jackson.databind.*;
import io.vavr.jackson.datatype.*;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;

@SpringBootApplication
public class TeamApplication {
    @Bean
    CommandLineRunner commandLineRunner(TeamInMemoryRepository teamRepository) {
        return evt -> {
            //@formatter:off
            teamRepository.save(new Team(randomUUID().toString(),
                                         "Fortuna Düsseldorf",
                                         "https://tmssl.akamaized.net//images/wappen/head/38.png?lm=1405514004",
                                         "Friedhelm Funkel",
                                         BigDecimal.valueOf(13000000),
                                         LocalDate.of(1895, 5, 5)));
            teamRepository.save(new Team(randomUUID().toString(),
                                         "1. FC Kaiserslautern",
                                         "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Logo_1_FC_Kaiserslautern.svg/360px-Logo_1_FC_Kaiserslautern.svg.png",
                                         "Norbert Meier",
                                         BigDecimal.valueOf(15800000),
                                         LocalDate.of(1900, 6, 2)));
            teamRepository.save(new Team(randomUUID().toString(),
                                         "FC St Pauli",
                                         "https://upload.wikimedia.org/wikipedia/en/thumb/8/81/FC_St._Pauli_logo.svg/460px-FC_St._Pauli_logo.svg.png",
                                         "Olaf Janßen",
                                         BigDecimal.valueOf(15000000),
                                         LocalDate.of(1910, 5, 15)));
            //@formatter:on
        };
    }

    @Bean
    public Module vavrModule() {
        return new VavrModule();
    }

//    @Bean
//    public Module jsr310Module() {
//        return new JavaTimeModule();
//    }


    public static void main(String[] args) {
        SpringApplication.run(TeamApplication.class, args);
    }
}
