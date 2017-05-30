package org.koenighotze.team;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.*;

import org.junit.*;

public class OptionalTest {
    
    @Test
    public void null_handling() {
        Optional<String> optional = Optional.ofNullable(null);

        assertThat(optional).isEmpty();
    }

    @Test(expected = NullPointerException.class)
    public void things_go_boom() {
        Optional.of(null);
    }

    @Test
    public void list_of_optionals() {
        List<Optional<String>> list = new ArrayList<>();

        list.add(Optional.empty());
        list.add(Optional.of("foo"));
        list.add(Optional.of("qux"));

        assertThat(list).hasSize(3);
    }

    @Test
    public void add_optional_to_list() {
        List<String> list = new ArrayList<>();

        Optional.<String>empty().map(list::add);
        Optional.of("foo").map(list::add);
        Optional.of("qux").map(list::add);

        assertThat(list).hasSize(2);
    }
}
