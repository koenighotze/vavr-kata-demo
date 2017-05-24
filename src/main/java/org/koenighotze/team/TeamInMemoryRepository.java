package org.koenighotze.team;

import java.math.*;
import java.time.*;
import java.util.*;

import io.vavr.control.*;
import org.springframework.stereotype.*;

@Repository
public class TeamInMemoryRepository {
    private final Map<String, Team> data = new HashMap<>();

    public TeamInMemoryRepository() {
        save(new Team("1", "Fortuna Düsseldorf", "https://tmssl.akamaized.net//images/wappen/head/38.png?lm=1405514004",
                      "Friedhelm Funkel", BigDecimal.valueOf(13000000), LocalDate.of(1895, 5, 5)));
        save(new Team("2", "1. FC Kaiserslautern",
                      "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d3/Logo_1_FC_Kaiserslautern.svg/360px-Logo_1_FC_Kaiserslautern.svg.png",
                      "Norbert Meier", BigDecimal.valueOf(15800000), LocalDate.of(1900, 6, 2)));
        save(new Team("3", "FC St Pauli",
                      "https://upload.wikimedia.org/wikipedia/en/thumb/8/81/FC_St._Pauli_logo.svg/460px-FC_St._Pauli_logo.svg.png",
                      "Olaf Janßen", BigDecimal.valueOf(15000000), LocalDate.of(1910, 5, 15)));
    }

    public void save(Team team) {
        data.put(team.getId(), team);
    }

    public Collection<Team> findAll() {
        return data.values();
    }

    public Option<Team> findById(String id) {
        return Option.of(data.get(id));
    }
}
