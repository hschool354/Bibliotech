package com.example.bibliotech.model;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class Users {
    private int userId;
    private String username;
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private Date dob;
    private String gender;
    private String address;
    private String nationality;
    private String bio;
    private String profilePictureUrl;
    private boolean isAdmin;
    private boolean isPremium;
    private BigDecimal accountBalance;
    private String registrationStatus;
    private Timestamp lastLoginDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Default constructor
    public Users() {}

    // Constructor with essential fields
    public Users(int userId, String username, String password, String isAdmin) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.isAdmin = "1".equals(isAdmin) || "true".equalsIgnoreCase(isAdmin);
    }

    // Full constructor
    public Users(int userId, String username, String email, String password,
                String fullName, String phone, Date dob, String gender,
                String address, String nationality, String bio,
                String profilePictureUrl, boolean isAdmin, boolean isPremium,
                BigDecimal accountBalance, String registrationStatus,
                Timestamp lastLoginDate, Timestamp createdAt, Timestamp updatedAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.nationality = nationality;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
        this.isAdmin = isAdmin;
        this.isPremium = isPremium;
        this.accountBalance = accountBalance;
        this.registrationStatus = registrationStatus;
        this.lastLoginDate = lastLoginDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public BigDecimal getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(BigDecimal accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public Timestamp getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Timestamp lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", isAdmin=" + isAdmin +
                ", isPremium=" + isPremium +
                ", registrationStatus='" + registrationStatus + '\'' +
                '}';
    }
}