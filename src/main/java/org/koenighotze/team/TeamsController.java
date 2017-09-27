package org.koenighotze.team;

import static io.vavr.CheckedFunction1.lift;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;
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

    @RequestMapping(value = "/{id}/logo", method = GET)
    @ResponseBody
    public HttpEntity<InputStreamResource> fetchLogo(@PathVariable String id) {
        return teamRepository.findById(id)
                             .map(this::fetchLogoForTeam)
                             .getOrElse(TeamsController::logoFetchNotFoundResponse);

    }

    private HttpEntity<InputStreamResource> fetchLogoForTeam(Team team) {
        try {
            return readLogoFromTeamWithTimeout(team.getLogoUrl())
                        .map(TeamsController::logoFetchSuccessful)
                        .getOrElse(TeamsController::logoFetchFailed);
        } catch (InterruptedException | TimeoutException e) {
            logger.warn("Logo fetch aborted due to timeout", e);
            return logoFetchTimedoutResponse();
        } catch (ExecutionException e) {
            logger.warn("Logo fetch failed to to internal error", e.getCause());
            return logoFetchFailed();
        }
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

    private Option<ByteArrayOutputStream> readLogoFromTeamWithTimeout(String logo) throws InterruptedException, ExecutionException, TimeoutException {
        return CompletableFuture.supplyAsync(() -> liftedReadLogoFromTeam(logo))
                                .get(3000, MILLISECONDS);
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
