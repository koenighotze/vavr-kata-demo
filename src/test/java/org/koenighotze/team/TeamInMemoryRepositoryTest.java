package org.koenighotze.team;

import static java.math.BigDecimal.ONE;
import static java.time.LocalDate.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.*;
import java.time.*;

import io.vavr.collection.*;
import io.vavr.control.*;
import org.junit.*;

public class TeamInMemoryRepositoryTest {

    private Team team;
    private TeamInMemoryRepository repository;

    @Before
    public void init() {
        //@formatter:off
        team = new Team("999",
                        randomUUID().toString(),
                        "someurl",
                        "sometrainer",
                        BigDecimal.valueOf(1232),
                        LocalDate.of(1233, 5, 5));

        repository = new TeamInMemoryRepository();
        repository.deleteAll();

        repository.save(new Team("1",
                                 "Fortuna Düsseldorf",
                                 "https://tmssl.akamaized.net//images/wappen/head/38.png?lm=1405514004",
                                 "Friedhelm Funkel",
                                 BigDecimal.valueOf(13000000),
                                 LocalDate.of(1895, 5, 5)));
        //@formatter:on
    }

    @Test
    public void save_stores_a_team() {
        repository.save(team);

        assertThat(repository.findById(team.getId()).get()).isEqualTo(team);
    }

    @Test
    public void save_overwrites_an_existing_team() {
        repository.save(team);
        Team newTeam = new Team(team.getId(), "foo", "bar", "qux", ONE, now());

        repository.save(newTeam);

        assertThat(repository.findById(team.getId()).get()).isEqualTo(newTeam);
    }

    @Test
    public void an_existing_team_can_be_found() {
        repository.save(team);

        Team foundTeam = repository.findById(team.getId())
                                   .get();

        assertThat(foundTeam).isEqualTo(team);
    }

    @Test
    public void if_the_team_is_missing_it_cannot_be_found() {
        Option<Team> foundTeam = repository.findById(team.getId());

        assertThat(foundTeam.isEmpty());
    }

    @Test
    public void findAll_returns_all_teams() {
        repository.save(team);

        List<Team> allTeams = repository.findAll();

        assertThat(allTeams).hasSize(2);
    }

    @Test
    public void findAll_returns_empty_if_no_teams_are_found() {
        repository.deleteAll();

        List<Team> allTeams = repository.findAll();

        assertThat(allTeams).isEmpty();
    }

}