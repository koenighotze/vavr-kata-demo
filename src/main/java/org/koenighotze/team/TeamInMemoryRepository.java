package org.koenighotze.team;

import java.util.*;
import java.util.concurrent.*;

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

    public Team findById(String id) {
        return data.get(id);
    }

    public void deleteAll() {
        data.clear();
    }
}
