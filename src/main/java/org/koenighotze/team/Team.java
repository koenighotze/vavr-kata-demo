package org.koenighotze.team;

import java.math.*;
import java.time.*;

import lombok.*;

@Data
public class Team {
    private final String name;
    private final String logoUrl;
    private final String coach;
    private final BigDecimal seasonalBudget;
    private final LocalDate foundedOn;
}
