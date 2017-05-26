package org.koenighotze.team;

import java.util.*;
import java.util.concurrent.*;

import io.vavr.control.*;
import org.springframework.stereotype.*;

@Repository
public class TeamInMemoryRepository {
    private final Map<String, Team> data = new ConcurrentHashMap<>();

    public void save(Team team) {
        data.put(team.getId(), team);
    }

    public Collection<Team> findAll() {
        return data.values();
    }

    public Option<Team> findById(String id) {
        return Option.of(data.get(id));
    }

    public void deleteAll() {
        data.clear();
    }
}
