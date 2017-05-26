package org.koenighotze.team;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/timeout")
public class TimeoutController {

    @RequestMapping(method = GET)
    public String timeout() throws InterruptedException {
        sleep(SECONDS.toMillis(30));
        return "Done";
    }
}
