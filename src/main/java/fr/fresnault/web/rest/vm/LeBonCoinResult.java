package fr.fresnault.web.rest.vm;

import java.io.Serializable;

public class LeBonCoinResult implements Serializable {

	private static final long serialVersionUID = 5136596514113642257L;

	private Long list_id;

	private String subject;

	public LeBonCoinResult() {
		super();
		// default constructor
	}

	public Long getList_id() {
		return list_id;
	}

	public void setList_id(Long list_id) {
		this.list_id = list_id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
