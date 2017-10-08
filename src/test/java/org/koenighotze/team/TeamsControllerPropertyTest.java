package org.koenighotze.team;

import static io.vavr.test.Arbitrary.string;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import io.vavr.collection.*;
import io.vavr.test.*;
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
public class TeamsControllerPropertyTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void fetching_a_user_always_returns_found_or_not_found() {
        Property.def("Fetching should never fail")
                .forAll(arbitraryUnicodeId())
                .suchThat(id -> List.ofAll(NOT_FOUND.value(), OK.value())
                                    .contains(fetchUser(id)))
                .check()
                .assertIsSatisfied();

    }

    private Integer fetchUser(String id) throws Exception {
        return mockMvc.perform(get("/teams/{id}", id))
                      .andReturn()
                      .getResponse()
                      .getStatus();
    }

    private Arbitrary<String> arbitraryUnicodeId() {
        Gen<Character> randomUnicode = random -> (char) random.nextInt();

        return string(randomUnicode);
    }
}
