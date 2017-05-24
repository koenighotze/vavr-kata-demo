package org.koenighotze.team;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;
import java.util.stream.*;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.context.*;
import org.springframework.test.context.junit4.*;
import org.springframework.test.web.servlet.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TeamsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void should_return_the_list_of_teams() throws Exception {
        mockMvc.perform(get("/teams/"))
               .andExpect(status().isOk());
    }

    @Test
    public void should_return_200_if_team_is_known() throws Exception {
        mockMvc.perform(get("/teams/1"))
               .andExpect(status().isOk());
    }

    @Test
    public void should_return_404_if_team_is_not_known() throws Exception {
        mockMvc.perform(get("/teams/NoTeam"))
               .andExpect(status().isNotFound());
    }

    @Test
    public void should_always_return_a_valid_http_code() throws Exception {
        ResultMatcher matcher = (MvcResult result) -> assertThat(Arrays.asList(OK.value(), NOT_FOUND.value())).contains(
            result.getResponse()
                  .getStatus());

        Stream.of("Foo", "F95", "bar", "baz", "123")
              .forEach(teamName -> {
                  try {
                      mockMvc.perform(get("/teams/{name}", teamName))
                             .andExpect(matcher);
                  } catch (Exception e) {
                      throw new RuntimeException(e);
                  }
              });
    }
}
