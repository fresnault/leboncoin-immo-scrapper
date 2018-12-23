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

		// url
		String leboncoinResourceUrl = "https://api.leboncoin.fr/finder/search";

		// headers
		HttpHeaders headers = new HttpHeaders();
		headers.put("api_key", Arrays.asList("ba0c2dad52b3ec"));

		int pageIndex = 0;
		boolean hasResults = true;

		while (hasResults) {
			log.info("Récupération de la page {}", pageIndex);

			// body
			int offset = pageIndex * 100;
			String body = "{\"filters\":{\"category\":{\"id\":\"9\"},\"enums\":{\"ad_type\":[\"offer\"],\"real_estate_type\":[\"1\",\"2\",\"3\"]},\"keywords\":{},\"location\":{},\"ranges\":{}},\"limit\":100, \"offset\": "
					+ offset + ", \"limit_alu\":0}";
			HttpEntity<String> request = new HttpEntity<>(body, headers);

			// post request
			ResponseEntity<LeBonCoinPageResults> response = restTemplate.postForEntity(leboncoinResourceUrl, request,
					LeBonCoinPageResults.class);

			if (response.getStatusCode().equals(HttpStatus.OK)) {
				for (LeBonCoinProperty property : response.getBody().getProperties()) {
					Property propertyToSave = new Property().reference(property.getReference())
							.publicationDate(property.getPublicationDate().atZone(ZoneId.systemDefault()))
							.categoryName(property.getCategoryName()).subject(property.getSubject())
							.body(property.getBody()).url(property.getUrl()).price(property.getPriceValue())
							.attributes(property.getAttributes()).location(property.getLocation());

					propertyRepository.save(propertyToSave);
				}

				if (response.getBody().getTotal() < offset) {
					hasResults = false;
				}

				pageIndex++;
			} else {
				log.info("Status code : {}", response.getStatusCode());
				try {
					Thread.sleep(1000 * RandomUtils.nextInt(20));
				} catch (InterruptedException e) {
					hasResults = false;
				}
			}
		}

	}

}
