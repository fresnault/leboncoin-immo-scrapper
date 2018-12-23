package fr.fresnault.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Property.
 */
@Document(collection = "property")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "property")
public class Property implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("reference")
    private Integer reference;

    @NotNull
    @Field("publication_date")
    private ZonedDateTime publicationDate;

    @NotNull
    @Field("category_name")
    private String categoryName;

    @NotNull
    @Field("subject")
    private String subject;

    @NotNull
    @Field("body")
    private String body;

    @NotNull
    @Field("url")
    private String url;

    @Field("price")
    private Integer price;

    @Field("attributes")
    private Set<Attribute> attributes = new HashSet<>();

    @Field("location")
    @JsonIgnoreProperties("properties")
    private Location location;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getReference() {
        return reference;
    }

    public Property reference(Integer reference) {
        this.reference = reference;
        return this;
    }

    public void setReference(Integer reference) {
        this.reference = reference;
    }

    public ZonedDateTime getPublicationDate() {
        return publicationDate;
    }

    public Property publicationDate(ZonedDateTime publicationDate) {
        this.publicationDate = publicationDate;
        return this;
    }

    public void setPublicationDate(ZonedDateTime publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Property categoryName(String categoryName) {
        this.categoryName = categoryName;
        return this;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSubject() {
        return subject;
    }

    public Property subject(String subject) {
        this.subject = subject;
        return this;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public Property body(String body) {
        this.body = body;
        return this;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public Property url(String url) {
        this.url = url;
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPrice() {
        return price;
    }

    public Property price(Integer price) {
        this.price = price;
        return this;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public Property attributes(Set<Attribute> attributes) {
        this.attributes = attributes;
        return this;
    }

    public Property addAttributes(Attribute attribute) {
        this.attributes.add(attribute);
        attribute.setProperty(this);
        return this;
    }

    public Property removeAttributes(Attribute attribute) {
        this.attributes.remove(attribute);
        attribute.setProperty(null);
        return this;
    }

    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Location getLocation() {
        return location;
    }

    public Property location(Location location) {
        this.location = location;
        return this;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Property property = (Property) o;
        if (property.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), property.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Property{" +
            "id=" + getId() +
            ", reference=" + getReference() +
            ", publicationDate='" + getPublicationDate() + "'" +
            ", categoryName='" + getCategoryName() + "'" +
            ", subject='" + getSubject() + "'" +
            ", body='" + getBody() + "'" +
            ", url='" + getUrl() + "'" +
            ", price=" + getPrice() +
            "}";
    }
}
