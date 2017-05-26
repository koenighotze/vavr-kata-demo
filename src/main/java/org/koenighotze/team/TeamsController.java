package org.koenighotze.team;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;
import static io.vavr.Predicates.instanceOf;
import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Base64.getEncoder;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import javax.imageio.*;

import io.vavr.collection.*;
import io.vavr.control.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/teams")
// Note: obviously nobody should write a controller like this.
// this is just for demonstrating bad code!
public class TeamsController {
    private static final Logger logger = getLogger(TeamsController.class);

    private final TeamInMemoryRepository teamRepository;

    @Autowired
    public TeamsController(TeamInMemoryRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @RequestMapping(method = GET)
    public HttpEntity<List<Team>> getAllTeams() {
        List<Team> teams = List.ofAll(teamRepository.findAll())
                               .map(this::hideManagementData);
        return ResponseEntity.ok(teams);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public HttpEntity<Team> findTeam(@PathVariable String id) {
        return teamRepository.findById(id)
                             .map(ResponseEntity::ok)
                             .getOrElse(() -> ResponseEntity.notFound()
                                                            .build());
    }

    @RequestMapping(value = "/{id}/logo", method = GET)
    public HttpEntity<String> fetchLogo(@PathVariable String id) {
        //@formatter:off
        return teamRepository.findById(id)
                             .map(Team::getLogoUrl)
                             .map(this::readTeamLogoWithTimeout)
                             .map(tryLogo ->
                                      Match(tryLogo).of(
                                        Case($Success($()), TeamsController::logoFetchSuccessful),
                                        Case($Failure($(instanceOf(TimeoutException.class))), TeamsController::logoFetchTimedoutResponse),
                                        Case($Failure($()), TeamsController::logoFetchFailed)
                                      )
                             )
                             .getOrElse(TeamsController::logoFetchNotFoundResponse);
        //@formatter:on
    }

    private static HttpEntity<String> logoFetchFailed() {
        return new ResponseEntity<>("Cannot fetch logo ", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static HttpEntity<String> logoFetchNotFoundResponse() {
        return new ResponseEntity<>("Cannot load logo ", NOT_FOUND);
    }

    private static HttpEntity<String> logoFetchSuccessful(String imageData) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(APPLICATION_OCTET_STREAM);

        return new ResponseEntity<>(imageData, httpHeaders, OK);
    }

    private static HttpEntity<String> logoFetchTimedoutResponse() {
        return new ResponseEntity<>("Cannot load logo due to timeout ", REQUEST_TIMEOUT);
    }

    private Try<String> readTeamLogoWithTimeout(String logo) {
        return Try.of(() -> CompletableFuture.supplyAsync(() -> readLogoFromTeam(logo).get())
                                             .get(3000, MILLISECONDS));
    }

    private Try<String> readLogoFromTeam(String logo) {
        return Try.of(() -> {
            BufferedImage image = ImageIO.read(new URL(logo));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            return new String(getEncoder().encode(os.toByteArray()), ISO_8859_1);
        });
    }

    private Team hideManagementData(Team team) {
        return new Team(team.getId(), team.getName(), team.getLogoUrl(), null, null, team.getFoundedOn());
    }
}
