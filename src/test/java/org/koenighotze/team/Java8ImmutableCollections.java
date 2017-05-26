package org.koenighotze.team;

import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.*;

import org.junit.*;

public class Java8ImmutableCollections {
    @Test(expected = UnsupportedOperationException.class)
    public void boom() {
        Set<String> droids = new HashSet<>(asList("C3PO", "R2D2", "K2SO"));

        Set<String> unmodifiableDroids = Collections.unmodifiableSet(droids);

        unmodifiableDroids.add("\uD83D\uDCA5");
    }

    @Test
    public void changing_sets() {
        Set<String> droids = new HashSet<>(asList("C3PO", "R2D2", "K2SO"));
        Set<String> moreDroids = droids;
        moreDroids.add("Chopper");

        assertThat(droids).hasSize(4);
    }
}
