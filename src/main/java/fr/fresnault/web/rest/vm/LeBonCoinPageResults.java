package fr.fresnault.web.rest.vm;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public class LeBonCoinPageResults implements Serializable {
	
	private static final long serialVersionUID = -8013782421465327034L;

	private Integer total;
	
	@JsonAlias("ads")
	private List<LeBonCoinProperty> properties;

	public LeBonCoinPageResults() {
		super();
		// default constructor
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public List<LeBonCoinProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<LeBonCoinProperty> properties) {
		this.properties = properties;
	}

}
