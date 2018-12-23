package fr.fresnault.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import fr.fresnault.domain.Property;


/**
 * Spring Data MongoDB repository for the Property entity.
 */
@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {

	boolean existsByReference(Integer reference);

}
