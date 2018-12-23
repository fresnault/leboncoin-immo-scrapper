package fr.fresnault.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonAlias;

/**
 * A Location.
 */
public class Location implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
	@JsonAlias("region_name")
    @Field("region_name")
    private String regionName;

    @NotNull
	@JsonAlias("department_name")
    @Field("department_name")
    private String departmentName;

    @NotNull
    @Field("city")
    private String city;

    @NotNull
    @Field("zipcode")
    private String zipcode;

    @Field("properties")
    private Set<Property> properties = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getRegionName() {
        return regionName;
    }

    public Location regionName(String regionName) {
        this.regionName = regionName;
        return this;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Location departmentName(String departmentName) {
        this.departmentName = departmentName;
        return this;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getCity() {
        return city;
    }

    public Location city(String city) {
        this.city = city;
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipcode() {
        return zipcode;
    }

    public Location zipcode(String zipcode) {
        this.zipcode = zipcode;
        return this;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public Set<Property> getProperties() {
        return properties;
    }

    public Location properties(Set<Property> properties) {
        this.properties = properties;
        return this;
    }

    public Location addProperties(Property property) {
        this.properties.add(property);
        property.setLocation(this);
        return this;
    }

    public Location removeProperties(Property property) {
        this.properties.remove(property);
        property.setLocation(null);
        return this;
    }

    public void setProperties(Set<Property> properties) {
        this.properties = properties;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

}
