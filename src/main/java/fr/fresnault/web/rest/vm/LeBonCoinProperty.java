package fr.fresnault.web.rest.vm;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

import fr.fresnault.domain.Attribute;
import fr.fresnault.domain.Location;

public class LeBonCoinProperty implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;

	@JsonAlias("list_id")
	private Integer reference;

	@JsonAlias("first_publication_date")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime publicationDate;

	@JsonAlias("category_name")
	private String categoryName;

	private String subject;

	private String body;

	private String url;

	@JsonAlias("price")
	private Integer[] price;

	private Set<Attribute> attributes = new HashSet<>();

	private Location location;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getReference() {
		return reference;
	}

	public void setReference(Integer reference) {
		this.reference = reference;
	}

	public LocalDateTime getPublicationDate() {
		return publicationDate;
	}

	public void setPublicationDate(LocalDateTime publicationDate) {
		this.publicationDate = publicationDate;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer[] getPrice() {
		return price;
	}

	public void setPrice(Integer[] price) {
		this.price = price;
	}

	public Set<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(Set<Attribute> attributes) {
		this.attributes = attributes;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "Property{" + "id=" + getId() + ", reference=" + getReference() + ", publicationDate='"
				+ getPublicationDate() + "'" + ", categoryName='" + getCategoryName() + "'" + ", subject='"
				+ getSubject() + "'" + ", body='" + getBody() + "'" + ", url='" + getUrl() + "'" + ", price="
				+ getPrice() + "}";
	}

	public Integer getPriceValue() {
		if (price != null && price.length > 0) {
			return price[0];
		}
		return null;
	}
}
