package org.koenighotze.team;

import static java.util.stream.Collectors.toList;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.*;

import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
public class TeamsController {
    private final TeamRepository teamRepository;

    @Autowired
    public TeamsController(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @RequestMapping(method = GET)
    public HttpEntity<List<Team>> getAllTeams() {
        List<Team> teams = teamRepository.findAll()
                                         .stream()
                                         .map(this::hideManagementData)
                                         .collect(toList());
        return ResponseEntity.ok(teams);
    }

    @RequestMapping(value = "/{name}", method = GET)
    public HttpEntity<Team> findTeam(@PathVariable String name) {
        Team team = teamRepository.findByName(name);
        if (null == team) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(team);
    }

    private Team hideManagementData(Team team) {
        return new Team(team.getName(), team.getLogoUrl(), null, null, team.getFoundedOn());
    }
}
