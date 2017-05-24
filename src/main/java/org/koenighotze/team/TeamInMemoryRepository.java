package org.koenighotze.team;

import org.springframework.data.mongodb.repository.*;

public interface TeamInMemoryRepository extends MongoRepository<Team, String> {
    Team findByName(String name);
}
