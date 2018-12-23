package fr.fresnault.web.rest;

import fr.fresnault.LeboncoinImmoScrapperApp;

import fr.fresnault.domain.Property;
import fr.fresnault.repository.PropertyRepository;
import fr.fresnault.repository.search.PropertySearchRepository;
import fr.fresnault.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;


import static fr.fresnault.web.rest.TestUtil.sameInstant;
import static fr.fresnault.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the PropertyResource REST controller.
 *
 * @see PropertyResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LeboncoinImmoScrapperApp.class)
public class PropertyResourceIntTest {

    private static final Integer DEFAULT_REFERENCE = 1;
    private static final Integer UPDATED_REFERENCE = 2;

    private static final ZonedDateTime DEFAULT_PUBLICATION_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_PUBLICATION_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_CATEGORY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_BODY = "AAAAAAAAAA";
    private static final String UPDATED_BODY = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_PRICE = 1;
    private static final Integer UPDATED_PRICE = 2;

    @Autowired
    private PropertyRepository propertyRepository;

    /**
     * This repository is mocked in the fr.fresnault.repository.search test package.
     *
     * @see fr.fresnault.repository.search.PropertySearchRepositoryMockConfiguration
     */
    @Autowired
    private PropertySearchRepository mockPropertySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private Validator validator;

    private MockMvc restPropertyMockMvc;

    private Property property;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final PropertyResource propertyResource = new PropertyResource(propertyRepository, mockPropertySearchRepository);
        this.restPropertyMockMvc = MockMvcBuilders.standaloneSetup(propertyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Property createEntity() {
        Property property = new Property()
            .reference(DEFAULT_REFERENCE)
            .publicationDate(DEFAULT_PUBLICATION_DATE)
            .categoryName(DEFAULT_CATEGORY_NAME)
            .subject(DEFAULT_SUBJECT)
            .body(DEFAULT_BODY)
            .url(DEFAULT_URL)
            .price(DEFAULT_PRICE);
        return property;
    }

    @Before
    public void initTest() {
        propertyRepository.deleteAll();
        property = createEntity();
    }

    @Test
    public void createProperty() throws Exception {
        int databaseSizeBeforeCreate = propertyRepository.findAll().size();

        // Create the Property
        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isCreated());

        // Validate the Property in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeCreate + 1);
        Property testProperty = propertyList.get(propertyList.size() - 1);
        assertThat(testProperty.getReference()).isEqualTo(DEFAULT_REFERENCE);
        assertThat(testProperty.getPublicationDate()).isEqualTo(DEFAULT_PUBLICATION_DATE);
        assertThat(testProperty.getCategoryName()).isEqualTo(DEFAULT_CATEGORY_NAME);
        assertThat(testProperty.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testProperty.getBody()).isEqualTo(DEFAULT_BODY);
        assertThat(testProperty.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testProperty.getPrice()).isEqualTo(DEFAULT_PRICE);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(1)).save(testProperty);
    }

    @Test
    public void createPropertyWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = propertyRepository.findAll().size();

        // Create the Property with an existing ID
        property.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isBadRequest());

        // Validate the Property in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeCreate);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    public void checkReferenceIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setReference(null);

        // Create the Property, which fails.

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkPublicationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setPublicationDate(null);

        // Create the Property, which fails.

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkCategoryNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setCategoryName(null);

        // Create the Property, which fails.

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setSubject(null);

        // Create the Property, which fails.

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkBodyIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setBody(null);

        // Create the Property, which fails.

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkUrlIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setUrl(null);

        // Create the Property, which fails.

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = propertyRepository.findAll().size();
        // set the field null
        property.setPrice(null);

        // Create the Property, which fails.

        restPropertyMockMvc.perform(post("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isBadRequest());

        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    public void getAllProperties() throws Exception {
        // Initialize the database
        propertyRepository.save(property);

        // Get all the propertyList
        restPropertyMockMvc.perform(get("/api/properties?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].publicationDate").value(hasItem(sameInstant(DEFAULT_PUBLICATION_DATE))))
            .andExpect(jsonPath("$.[*].categoryName").value(hasItem(DEFAULT_CATEGORY_NAME.toString())))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT.toString())))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)));
    }
    
    @Test
    public void getProperty() throws Exception {
        // Initialize the database
        propertyRepository.save(property);

        // Get the property
        restPropertyMockMvc.perform(get("/api/properties/{id}", property.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(property.getId()))
            .andExpect(jsonPath("$.reference").value(DEFAULT_REFERENCE))
            .andExpect(jsonPath("$.publicationDate").value(sameInstant(DEFAULT_PUBLICATION_DATE)))
            .andExpect(jsonPath("$.categoryName").value(DEFAULT_CATEGORY_NAME.toString()))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT.toString()))
            .andExpect(jsonPath("$.body").value(DEFAULT_BODY.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE));
    }

    @Test
    public void getNonExistingProperty() throws Exception {
        // Get the property
        restPropertyMockMvc.perform(get("/api/properties/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateProperty() throws Exception {
        // Initialize the database
        propertyRepository.save(property);

        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();

        // Update the property
        Property updatedProperty = propertyRepository.findById(property.getId()).get();
        updatedProperty
            .reference(UPDATED_REFERENCE)
            .publicationDate(UPDATED_PUBLICATION_DATE)
            .categoryName(UPDATED_CATEGORY_NAME)
            .subject(UPDATED_SUBJECT)
            .body(UPDATED_BODY)
            .url(UPDATED_URL)
            .price(UPDATED_PRICE);

        restPropertyMockMvc.perform(put("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProperty)))
            .andExpect(status().isOk());

        // Validate the Property in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);
        Property testProperty = propertyList.get(propertyList.size() - 1);
        assertThat(testProperty.getReference()).isEqualTo(UPDATED_REFERENCE);
        assertThat(testProperty.getPublicationDate()).isEqualTo(UPDATED_PUBLICATION_DATE);
        assertThat(testProperty.getCategoryName()).isEqualTo(UPDATED_CATEGORY_NAME);
        assertThat(testProperty.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testProperty.getBody()).isEqualTo(UPDATED_BODY);
        assertThat(testProperty.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testProperty.getPrice()).isEqualTo(UPDATED_PRICE);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(1)).save(testProperty);
    }

    @Test
    public void updateNonExistingProperty() throws Exception {
        int databaseSizeBeforeUpdate = propertyRepository.findAll().size();

        // Create the Property

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPropertyMockMvc.perform(put("/api/properties")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(property)))
            .andExpect(status().isBadRequest());

        // Validate the Property in the database
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(0)).save(property);
    }

    @Test
    public void deleteProperty() throws Exception {
        // Initialize the database
        propertyRepository.save(property);

        int databaseSizeBeforeDelete = propertyRepository.findAll().size();

        // Get the property
        restPropertyMockMvc.perform(delete("/api/properties/{id}", property.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Property> propertyList = propertyRepository.findAll();
        assertThat(propertyList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Property in Elasticsearch
        verify(mockPropertySearchRepository, times(1)).deleteById(property.getId());
    }

    @Test
    public void searchProperty() throws Exception {
        // Initialize the database
        propertyRepository.save(property);
        when(mockPropertySearchRepository.search(queryStringQuery("id:" + property.getId())))
            .thenReturn(Collections.singletonList(property));
        // Search the property
        restPropertyMockMvc.perform(get("/api/_search/properties?query=id:" + property.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(property.getId())))
            .andExpect(jsonPath("$.[*].reference").value(hasItem(DEFAULT_REFERENCE)))
            .andExpect(jsonPath("$.[*].publicationDate").value(hasItem(sameInstant(DEFAULT_PUBLICATION_DATE))))
            .andExpect(jsonPath("$.[*].categoryName").value(hasItem(DEFAULT_CATEGORY_NAME)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].body").value(hasItem(DEFAULT_BODY)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE)));
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Property.class);
        Property property1 = new Property();
        property1.setId("id1");
        Property property2 = new Property();
        property2.setId(property1.getId());
        assertThat(property1).isEqualTo(property2);
        property2.setId("id2");
        assertThat(property1).isNotEqualTo(property2);
        property1.setId(null);
        assertThat(property1).isNotEqualTo(property2);
    }
}
