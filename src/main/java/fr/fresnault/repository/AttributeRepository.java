package fr.fresnault.repository;

import fr.fresnault.domain.Attribute;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Spring Data MongoDB repository for the Attribute entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AttributeRepository extends MongoRepository<Attribute, String> {

}
