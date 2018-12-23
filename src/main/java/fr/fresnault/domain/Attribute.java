package fr.fresnault.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * A Attribute.
 */
public class Attribute implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Field("key")
    private String key;

    @NotNull
    @Field("value")
    private String value;

    @NotNull
	@JsonAlias("key_label")
    @Field("key_label")
    private String keyLabel;

    @NotNull
	@JsonAlias("value_label")
    @Field("value_label")
    private String valueLabel;

	@Field("property")
	@JsonIgnoreProperties("attributes")
	private Property property;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getKey() {
        return key;
    }

    public Attribute key(String key) {
        this.key = key;
        return this;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public Attribute value(String value) {
        this.value = value;
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getKeyLabel() {
        return keyLabel;
    }

    public Attribute keyLabel(String keyLabel) {
        this.keyLabel = keyLabel;
        return this;
    }

    public void setKeyLabel(String keyLabel) {
        this.keyLabel = keyLabel;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public Attribute valueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
        return this;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }

	public Property getProperty() {
		return property;
	}

	public Attribute property(Property property) {
		this.property = property;
		return this;
	}

	public void setProperty(Property property) {
		this.property = property;
	}
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

}
