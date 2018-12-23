package fr.fresnault.service;

import java.time.ZoneId;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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

		String leboncoinResourceUrl = "https://api.leboncoin.fr/finder/search";

		HttpHeaders headers = new HttpHeaders();
		headers.put("api_key", Arrays.asList("ba0c2dad52b3ec"));

		// metropolitan departments
		for (int department = 1; department <= 95; department++) {
			int pageIndex = 0;
			int nbRetry = 0;
			boolean hasResults = true;

			log.info("Department number {}", department);
			while (hasResults && nbRetry < 10) {
				log.info("Page number {}", pageIndex);

				// body
				int offset = pageIndex * 100;
				String body = generateBodyRequest(department, offset);
				HttpEntity<String> request = new HttpEntity<>(body, headers);

				// post request
				ResponseEntity<LeBonCoinPageResults> response = restTemplate.postForEntity(leboncoinResourceUrl,
						request, LeBonCoinPageResults.class);

				if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody().getProperties() != null) {
					if (nbRetry > 0) {
						nbRetry = 0;
					}
					for (LeBonCoinProperty property : response.getBody().getProperties()) {
						Integer reference = property.getReference();
						if (!propertyRepository.existsByReference(reference)) {
							Property propertyToSave = new Property().reference(reference)
									.publicationDate(property.getPublicationDate().atZone(ZoneId.systemDefault()))
									.categoryName(property.getCategoryName()).subject(property.getSubject())
									.body(property.getBody()).url(property.getUrl()).price(property.getPriceValue())
									.attributes(property.getAttributes()).location(property.getLocation());
							propertyRepository.save(propertyToSave);
							log.info("Property saved #{}", reference);
						} else {
							log.info("Property already exists #{}", reference);
						}
					}

					if (response.getBody().getProperties().size() < 100) {
						hasResults = false;
					}

					pageIndex++;
				} else {
					// If an error occurred during the request, we wait some
					// seconds before recall the API
					log.info("Status code : {}", response.getStatusCode());
					if (pageIndex < 300) {
						try {
							Thread.sleep(1000 * RandomUtils.nextInt(20));
						} catch (InterruptedException e) {
							hasResults = false;
						}
					} else {
						hasResults = false;
					}

					nbRetry++;
				}
			}

		}

	}

	private String generateBodyRequest(int department, int offset) {
		return "{\"filters\":{\"category\":{\"id\":\"8\"},\"enums\":{\"ad_type\":[\"offer\"]},\"keywords\":{},\"location\":{\"departments\":[\""
				+ department + "\"]},\"ranges\":{}},\"limit\":100, \"offset\": "
				+ offset + ", \"limit_alu\":0}";
	}

}
