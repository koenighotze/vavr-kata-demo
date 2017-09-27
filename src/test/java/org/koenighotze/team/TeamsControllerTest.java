package org.koenighotze.team;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.*;

import io.vavr.control.*;
import org.junit.*;

public class TeamsControllerTest {
    @Test
    public void returns_the_image_data_for_a_teams_logo() {
        TeamsController teamsController = new TeamsController(new TeamInMemoryRepository());
        Option<ByteArrayOutputStream> logo = teamsController.liftedReadLogoFromTeam(
            TeamsControllerTest.class.getResource("index.png")
                                     .toString());

        assertThat(logo.isDefined()).isTrue();
    }

    @Test
    public void returns_an_empty_optional_if_reading_a_logo_fails() {
        TeamsController teamsController = new TeamsController(new TeamInMemoryRepository());
        Option<ByteArrayOutputStream> logo = teamsController.liftedReadLogoFromTeam("clearly a bad url");

        assertThat(logo.isEmpty()).isTrue();
    }
}