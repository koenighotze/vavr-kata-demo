package org.koenighotze.team;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;
import static io.vavr.control.Option.some;
import static org.assertj.core.api.Assertions.assertThat;

import io.vavr.collection.*;
import io.vavr.control.*;
import org.junit.*;

public class OptionTest {
    @Test
    public void some_null() {
        Option<String> someNull = Option.some(null);
        String result = someNull.getOrElse("Something else");

        assertThat(result).isNull();
    }

    @Test(expected = NullPointerException.class)
    public void some_null_with_map() {
        Option.<String>some(null).map(String::toUpperCase);
    }

    @Test
    public void matching_an_option() {
        Option<String> value = Option.of("foo");

        Match(value).of(Case($Some($()), String::toUpperCase), Case($None(), () -> ""));

        Option<Configuration> defaultConfiguration = some(new Configuration());
        Option<Configuration> customConfiguration = Try.of(Configuration::load)
                                                       .toOption();

        customConfiguration.orElse(defaultConfiguration)
                           .get();

        customConfiguration.getOrElse(Configuration::new);
    }

    @Test
    public void option_is_an_iterator() {
        Option<String> foo = Option.of("foo");

        for (String i : foo) {
            assertThat(i).isEqualTo("foo");
        }
    }

    @Test
    public void sequence() {
        Option<Seq<String>> sequence = Option.sequence(List.of(Option.of("foo"), Option.of("qux"), Option.of("zum")));

        assertThat(sequence.get()).isEqualTo(List.of("foo", "qux", "zum"));
    }

    @Test
    public void sequence_is_none_if_one_value_is_none() {
        Option<Seq<String>> sequence = Option.sequence(List.of(Option.none(), Option.of("qux"), Option.of("zum")));

        assertThat(sequence.isEmpty()).isTrue();
    }

    @Test
    public void peek() {
        Option<String> foo = Option.of("foo");
        Option<String> peek = foo.peek(System.out::println)
                                 .map(String::toUpperCase)
                                 .peek(System.out::println);

        assertThat(peek.get()).isEqualTo("FOO");
    }

    @Test
    public void when() {
        Option<String> value = Option.when("".isEmpty(), "foo");

        // could be written as

        if ("".isEmpty()) {
            value = some("foo");
        } else {
            value = Option.none();
        }
    }

    @Test
    public void flatmapping() {
        Option<String> option = Option.of("foo");

        Option<String> result = option.flatMap(s -> Option.of(s.toUpperCase()));

        assertThat(result.get()).isEqualTo("FOO");
    }

    @Test
    public void transforming_an_option() {
        Option<String> option = Option.of("foo");

        String result = option.transform(opt -> opt.map(String::toUpperCase)
                                                   .getOrElse(""));
        option.map(String::toUpperCase)
              .getOrElse("");

        assertThat(result).isEqualTo("FOO");
    }

    @Test
    public void null_handling() {
        Option<String> option = Option.of(null);

        assertThat(option).isEmpty();
    }

    @Test
    public void things_do_not_go_boom() {
        Option.of(null);
    }

    @Test
    public void list_of_optionals() {
        List<Option<String>> list = List.of(Option.none(), Option.of("foo"), Option.of("qux"));

        assertThat(list).hasSize(3);
    }

    @Test
    public void add_optional_to_list() {
        List<String> list = List.ofAll(Option.<String>none())
                                .appendAll(Option.of("foo"))
                                .appendAll(Option.of("qux"));

        assertThat(list).hasSize(2);
    }

}
