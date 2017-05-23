package org.koenighotze.team;

import org.springframework.data.mongodb.repository.*;

public interface TeamRepository extends MongoRepository<Team, String> {
    Team findByName(String name);
}
