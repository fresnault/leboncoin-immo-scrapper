package fr.fresnault.web.rest;

import com.codahale.metrics.annotation.Timed;
import fr.fresnault.domain.Property;
import fr.fresnault.repository.PropertyRepository;
import fr.fresnault.repository.search.PropertySearchRepository;
import fr.fresnault.web.rest.errors.BadRequestAlertException;
import fr.fresnault.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Property.
 */
@RestController
@RequestMapping("/api")
public class PropertyResource {

    private final Logger log = LoggerFactory.getLogger(PropertyResource.class);

    private static final String ENTITY_NAME = "leboncoinImmoScrapperProperty";

    private final PropertyRepository propertyRepository;

    private final PropertySearchRepository propertySearchRepository;

    public PropertyResource(PropertyRepository propertyRepository, PropertySearchRepository propertySearchRepository) {
        this.propertyRepository = propertyRepository;
        this.propertySearchRepository = propertySearchRepository;
    }

    /**
     * POST  /properties : Create a new property.
     *
     * @param property the property to create
     * @return the ResponseEntity with status 201 (Created) and with body the new property, or with status 400 (Bad Request) if the property has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/properties")
    @Timed
    public ResponseEntity<Property> createProperty(@Valid @RequestBody Property property) throws URISyntaxException {
        log.debug("REST request to save Property : {}", property);
        if (property.getId() != null) {
            throw new BadRequestAlertException("A new property cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Property result = propertyRepository.save(property);
        propertySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/properties/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /properties : Updates an existing property.
     *
     * @param property the property to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated property,
     * or with status 400 (Bad Request) if the property is not valid,
     * or with status 500 (Internal Server Error) if the property couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/properties")
    @Timed
    public ResponseEntity<Property> updateProperty(@Valid @RequestBody Property property) throws URISyntaxException {
        log.debug("REST request to update Property : {}", property);
        if (property.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Property result = propertyRepository.save(property);
        propertySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, property.getId().toString()))
            .body(result);
    }

    /**
     * GET  /properties : get all the properties.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of properties in body
     */
    @GetMapping("/properties")
    @Timed
    public List<Property> getAllProperties() {
        log.debug("REST request to get all Properties");
        return propertyRepository.findAll();
    }

    /**
     * GET  /properties/:id : get the "id" property.
     *
     * @param id the id of the property to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the property, or with status 404 (Not Found)
     */
    @GetMapping("/properties/{id}")
    @Timed
    public ResponseEntity<Property> getProperty(@PathVariable String id) {
        log.debug("REST request to get Property : {}", id);
        Optional<Property> property = propertyRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(property);
    }

    /**
     * DELETE  /properties/:id : delete the "id" property.
     *
     * @param id the id of the property to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/properties/{id}")
    @Timed
    public ResponseEntity<Void> deleteProperty(@PathVariable String id) {
        log.debug("REST request to delete Property : {}", id);

        propertyRepository.deleteById(id);
        propertySearchRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
    }

    /**
     * SEARCH  /_search/properties?query=:query : search for the property corresponding
     * to the query.
     *
     * @param query the query of the property search
     * @return the result of the search
     */
    @GetMapping("/_search/properties")
    @Timed
    public List<Property> searchProperties(@RequestParam String query) {
        log.debug("REST request to search Properties for query {}", query);
        return StreamSupport
            .stream(propertySearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

}
