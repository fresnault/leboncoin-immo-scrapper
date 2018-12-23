package fr.fresnault.web.rest.vm;

import java.io.Serializable;
import java.util.List;

public class LeBonCoinPageResults implements Serializable {
	
	private static final long serialVersionUID = -8013782421465327034L;

	private Integer total;
	
	private List<LeBonCoinResult> results;

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

	public List<LeBonCoinResult> getResults() {
		return results;
	}

	public void setResults(List<LeBonCoinResult> results) {
		this.results = results;
	}

}
