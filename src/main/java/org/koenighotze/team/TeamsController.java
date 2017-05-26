package org.koenighotze.team;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Base64.getEncoder;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.REQUEST_TIMEOUT;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import javax.imageio.*;

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
        List<Team> teams = teamRepository.findAll()
                                         .stream()
                                         .map(this::hideManagementData)
                                         .collect(toList());
        return ResponseEntity.ok(teams);
    }

    @RequestMapping(value = "/{id}", method = GET)
    public HttpEntity<Team> findTeam(@PathVariable String id) {
        Team team = teamRepository.findById(id);
        if (null == team) {
            return ResponseEntity.notFound()
                                 .build();
        }
        return ResponseEntity.ok(team);
    }

    @RequestMapping(value = "/{id}/logo", method = GET)
    public HttpEntity<String> fetchLogo(@PathVariable String id) {
        Team team = teamRepository.findById(id);
        if (null == team) {
            return logoFetchNotFoundResponse();
        }

        if (null == team.getLogoUrl()) {
            return logoFetchNotFoundResponse();
        }

        try {
            String logo = readTeamLogoWithTimeout(team);

            return logoFetchSuccessful(logo);
        } catch (TimeoutException | InterruptedException e) {
            logger.warn("Cannot read logo from " + team.getLogoUrl(), e);
            return logoFetchTimedoutResponse();
        } catch (ExecutionException e) {
            return logoFetchFailed();
        }

    }

    private String readTeamLogoWithTimeout(Team team) throws InterruptedException, ExecutionException, TimeoutException {
        //@formatter:off
        // bad completable future error handling incoming. DO NOT do this!
        return CompletableFuture.supplyAsync(() -> {
            try {
                return readLogoFromTeam(team);
            } catch (IOException e) {
                logger.warn("Cannot read image from " + team.getLogoUrl(), e);
                throw new RuntimeException(e);
            }
        })
        .get(3000, MILLISECONDS);
        //@formatter:on
    }

    private String readLogoFromTeam(Team team) throws IOException {
        BufferedImage image = ImageIO.read(new URL(team.getLogoUrl()));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return new String(getEncoder().encode(os.toByteArray()), ISO_8859_1);
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

    private Team hideManagementData(Team team) {
        return new Team(team.getId(), team.getName(), team.getLogoUrl(), null, null, team.getFoundedOn());
    }
}
