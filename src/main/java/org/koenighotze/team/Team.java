package org.koenighotze.team;

import java.math.*;
import java.time.*;

import lombok.*;

@Data
public class Team {
    @NonNull
    private final String id;
    @NonNull
    private final String name;
    @NonNull
    private final String logoUrl;
    private final String coach;
    private final BigDecimal estimatedMarketValue;
    @NonNull
    private final LocalDate foundedOn;
}
