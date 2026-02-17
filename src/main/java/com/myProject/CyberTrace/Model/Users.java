package com.myProject.CyberTrace.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Users {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	private String contactNo;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@Column(nullable = false)
	private String password;
	private String gender;
	private String address;
	
	@Column(length = 1000)
	private String profilePic;
	private LocalDateTime regDate;
	
	
	@Column(nullable = false, unique = true)
	private String govtIdProof;
	
	
	@Enumerated(EnumType.STRING)
	private UserStatus status; 
	@Enumerated(EnumType.STRING)
	private UserRole role;

	
	public enum UserStatus{
		BLOCKED, UNBLOCKED, DELETED
	}
	
	
	public enum UserRole{
		ADMIN,INVESTIGATOR
	}

	
	//   GETTER AND SETTER

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


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


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public LocalDateTime getRegDate() {
		return regDate;
	}


	public void setRegDate(LocalDateTime regDate) {
		this.regDate = regDate;
	}


	public String getGovtIdProof() {
		return govtIdProof;
	}


	public void setGovtIdProof(String govtIdProof) {
		this.govtIdProof = govtIdProof;
	}


	public UserStatus getStatus() {
		return status;
	}


	public void setStatus(UserStatus status) {
		this.status = status;
	}


	public UserRole getRole() {
		return role;
	}


	public void setRole(UserRole role) {
		this.role = role;
	}


	public String getProfilePic() {
		return profilePic;
	}


	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	
	
	
}
