package fr.fresnault.service;

import java.time.ZoneId;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import fr.fresnault.domain.Property;
import fr.fresnault.repository.PropertyRepository;
import fr.fresnault.web.rest.vm.LeBonCoinPageResults;
import fr.fresnault.web.rest.vm.LeBonCoinProperty;

@Service
public class LeBonCoinScrapperService {

	private static final Logger log = LoggerFactory.getLogger(LeBonCoinScrapperService.class);

	private PropertyRepository propertyRepository;

	public LeBonCoinScrapperService(PropertyRepository propertyRepository) {
		this.propertyRepository = propertyRepository;
	}

	@PostConstruct
	public void runScrap() {
		RestTemplate restTemplate = new RestTemplate();

		// url
		String leboncoinResourceUrl = "https://api.leboncoin.fr/finder/search";

		// headers
		HttpHeaders headers = new HttpHeaders();
		headers.put("api_key", Arrays.asList("ba0c2dad52b3ec"));

		// body
		String body = "{\"filters\":{\"category\":{\"id\":\"9\"},\"enums\":{\"ad_type\":[\"offer\"],\"real_estate_type\":[\"1\",\"2\",\"3\"]},\"keywords\":{},\"location\":{},\"ranges\":{}},\"limit\":100, \"offset\": 0, \"limit_alu\":0}";
		HttpEntity<String> request = new HttpEntity<>(body, headers);

		// post request
		ResponseEntity<LeBonCoinPageResults> response = restTemplate.postForEntity(leboncoinResourceUrl, request,
				LeBonCoinPageResults.class);

		// return request 200
		log.info("Status code : {}", response.getStatusCode());
		LeBonCoinPageResults leboncoinPageResults = response.getBody();
		log.info("Count results : {}", leboncoinPageResults.getTotal());

		for (LeBonCoinProperty property : leboncoinPageResults.getProperties()) {
			Property propertyToSave = new Property()
					.reference(property.getReference())
					.publicationDate(property.getPublicationDate().atZone(ZoneId.systemDefault()))
					.categoryName(property.getCategoryName())
					.subject(property.getSubject())
					.body(property.getBody())
					.url(property.getUrl())
					.price(property.getPriceValue())
					.attributes(property.getAttributes())
					.location(property.getLocation());
			
			propertyRepository.save(propertyToSave);
		}
	}

}
