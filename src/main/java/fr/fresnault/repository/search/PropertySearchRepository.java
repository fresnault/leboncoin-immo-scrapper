package fr.fresnault.repository.search;

import fr.fresnault.domain.Property;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Property entity.
 */
public interface PropertySearchRepository extends ElasticsearchRepository<Property, String> {
}
