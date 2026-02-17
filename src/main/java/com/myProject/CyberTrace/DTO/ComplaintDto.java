package com.myProject.CyberTrace.DTO;

public class ComplaintDto {

	private String name;
	
	private String contactNo;
	
	private String whatsappNo;
	
	private String email;
	
	private String address;
	
	private String typeOfScam;
	
	private String platform;
	
	private String title;
	
	private String description;
	
	private double lostAmount;
	
	private String scammer;

	// Getter Setter
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContactNo() {
		return contactNo;
	}

	public void setContactNo(String contactNo) {
		this.contactNo = contactNo;
	}

	public String getWhatsappNo() {
		return whatsappNo;
	}

	public void setWhatsappNo(String whatsappNo) {
		this.whatsappNo = whatsappNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTypeOfScam() {
		return typeOfScam;
	}

	public void setTypeOfScam(String typeOfScam) {
		this.typeOfScam = typeOfScam;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getLostAmount() {
		return lostAmount;
	}

	public void setLostAmount(double lostAmount) {
		this.lostAmount = lostAmount;
	}

	public String getScammer() {
		return scammer;
	}

	public void setScammer(String scammer) {
		this.scammer = scammer;
	}
	
}
