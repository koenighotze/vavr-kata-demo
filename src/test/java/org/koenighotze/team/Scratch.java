package org.koenighotze.team;

import static java.util.Arrays.asList;

import java.util.*;

import io.vavr.control.*;
import org.junit.*;

/**
 * @author David Schmitz
 */
public class Scratch {
    @Test
    public  void foo() {
        ArrayList<String> strings = new ArrayList<>(asList("qux", "zum"));
        Optional<String> optional = Optional.ofNullable("Bla");
        optional.map(strings::add);

        System.out.println(strings);
    }

    public static void bar() {
        Option<String> optional = Option.of("Bla");
        io.vavr.collection.List<String> strings = io.vavr.collection.List.of("qux", "zum");

        strings.appendAll(optional);

    }
}
