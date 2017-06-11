package org.koenighotze.team;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import io.vavr.collection.*;
import org.junit.*;

public class UsingTreeSet {

    @Test
    public void treeset_is_immutable() {
        TreeSet<String> droids = TreeSet.of("C3PO", "R2D2", "K2SO");

        TreeSet<String> moreDroids = droids.add("Chopper");

        assertThat(droids).hasSize(3);
        assertThat(moreDroids).hasSize(4);
    }

}
