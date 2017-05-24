package org.koenighotze.team;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.util.Base64.getEncoder;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
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
            return ResponseEntity.notFound()
                                 .build();
        }

        if (null != team.getLogoUrl()) {
            try {
                String logo = readTeamLogoWithTimeout(team);

                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(APPLICATION_OCTET_STREAM);

                return new ResponseEntity<>(logo, httpHeaders, OK);
            } catch (TimeoutException | InterruptedException | ExecutionException e) {
                logger.warn("Cannot read logo from " + team.getLogoUrl(), e);
                return ResponseEntity.status(REQUEST_TIMEOUT)
                                     .body("Cannot load logo from " + team.getLogoUrl());
            }
        }
        return ResponseEntity.noContent()
                             .build();

    }

    private String readTeamLogoWithTimeout(Team team) throws InterruptedException, ExecutionException, TimeoutException {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return readLogoFromTeam(team);
            } catch (IOException e) {
                logger.warn("Cannot read image from " + team.getLogoUrl(), e);
                return null;
            }
        }).get(3000, MILLISECONDS);
    }

    private String readLogoFromTeam(Team team) throws IOException {
        BufferedImage image = ImageIO.read(new URL(team.getLogoUrl()));
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return new String(getEncoder().encode(os.toByteArray()), ISO_8859_1);
    }

    private Team hideManagementData(Team team) {
        return new Team(team.getId(), team.getName(), team.getLogoUrl(), null, null, team.getFoundedOn());
    }
}
