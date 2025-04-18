package com.pms.entity;

public class Customer {

	private String id;
	private String name;
	private Integer age;
	private String phone;
	private String email;
	private String address;
	private String regDate;
	private String password;
	private Boolean verified = false;
	private Boolean active = false;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
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

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}

	public boolean getVerified() {
		return verified != null ? verified : false;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getActive() {
		return active != null ? active : false;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	 public Integer getAge() {
	        return age;
	    }

	    public void setAge(Integer age) {
	        this.age = age;
	    }

}
