package org.koenighotze.team;

import io.vavr.collection.*;
import org.junit.*;

public class ForVsMap {
    @Test
    public void for_vs_map() {
        String[] names = new String[]{"Foo", "Bar", "Baz"};

        String[] upper = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            upper[i] = names[i].toUpperCase();
        }

        String[] upper2 = Array.of(names)
                               .map(String::toUpperCase)
                               .toJavaArray(String.class);

    }

}
