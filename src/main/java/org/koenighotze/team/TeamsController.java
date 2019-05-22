package org.koenighotze.team;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.CheckedFunction1.lift;
import static io.vavr.Patterns.$Failure;
import static io.vavr.Patterns.$Success;
import static io.vavr.Predicates.instanceOf;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.ResponseEntity.ok;
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
import org.springframework.core.io.*;
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
        List<Team> teams = teamRepository.findAll()
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

    @RequestMapping(value = "/{id}/logo", method = GET, produces = APPLICATION_OCTET_STREAM_VALUE)
    @ResponseBody
    public HttpEntity<InputStreamResource> fetchLogo(@PathVariable String id) {
        return teamRepository.findById(id)
                             .map(this::fetchLogoForTeam)
                             .getOrElse(TeamsController::logoFetchNotFoundResponse);
    }

    private HttpEntity<InputStreamResource> fetchLogoForTeam(Team team) {
        //@formatter:off
        Try<Option<ByteArrayOutputStream>> tryLogo = readLogoFromTeamWithTimeout(team.getLogoUrl())
                                                                .onFailure(t -> logger.warn("Fetch failed", t));
        return
            Match(tryLogo).of(
                Case($Success($()), t -> t.map(TeamsController::logoFetchSuccessful).getOrElse(TeamsController::logoFetchFailed)),
                Case($Failure($(instanceOf(TimeoutException.class))), TeamsController::logoFetchTimedoutResponse),
                Case($Failure($()), TeamsController::logoFetchFailed)
            );
        //@formatter:on

        // Bonus question: why is an invalid URL exception not printed?
    }

    private static HttpEntity<InputStreamResource> logoFetchFailed() {
        return new ResponseEntity<>(BAD_REQUEST);
    }

    private static HttpEntity<InputStreamResource> logoFetchNotFoundResponse() {
        return new ResponseEntity<>(NOT_FOUND);
    }

    private static HttpEntity<InputStreamResource> logoFetchSuccessful(ByteArrayOutputStream logo) {
        return ResponseEntity.ok(new InputStreamResource(new ByteArrayInputStream(logo.toByteArray())));
    }

    private static HttpEntity<InputStreamResource> logoFetchTimedoutResponse() {
        return new ResponseEntity<>(REQUEST_TIMEOUT);
    }

    private Try<Option<ByteArrayOutputStream>> readLogoFromTeamWithTimeout(String logo) {
        return Try.of(() -> CompletableFuture.supplyAsync(() -> liftedReadLogoFromTeam(logo))
                                             .get(3000, MILLISECONDS));
    }

    Option<ByteArrayOutputStream> liftedReadLogoFromTeam(String logo) {
        return lift(this::readLogoFromTeam).apply(logo);
    }

    private ByteArrayOutputStream readLogoFromTeam(String logo) throws IOException {
        BufferedImage image = ImageIO.read(new URL(logo));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return os;
    }

    private Team hideManagementData(Team team) {
        return new Team(team.getId(), team.getName(), team.getLogoUrl(), null, null, team.getFoundedOn());
    }
}
