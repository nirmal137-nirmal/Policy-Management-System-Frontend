package com.pms.entity;

public class Scheme {

	private int id;

	private String schemeName;

	private String description;

	private String eligibilityCriteria;

	private String benefits;

	private String schemeDetails;

	private boolean schemeIsActive;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEligibilityCriteria() {
		return eligibilityCriteria;
	}

	public void setEligibilityCriteria(String eligibilityCriteria) {
		this.eligibilityCriteria = eligibilityCriteria;
	}

	public String getBenefits() {
		return benefits;
	}

	public void setBenefits(String benefits) {
		this.benefits = benefits;
	}

	public String getSchemeDetails() {
		return schemeDetails;
	}

	public void setSchemeDetails(String schemeDetails) {
		this.schemeDetails = schemeDetails;
	}

	public boolean isSchemeIsActive() {
		return schemeIsActive;
	}

	public void setSchemeIsActive(boolean schemeIsActive) {
		this.schemeIsActive = schemeIsActive;
	}
}