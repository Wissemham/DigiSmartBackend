package com.stage.digibackend.repository;

import com.stage.digibackend.Collections.Historique;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HistoriqueRepository extends MongoRepository<Historique, String> {
}
