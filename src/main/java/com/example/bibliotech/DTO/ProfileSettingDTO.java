package com.example.bibliotech.DTO;

import java.util.Date;
import java.util.Objects;

public class ProfileSettingDTO {
    private int userId;
    private String username;
    private String fullName;
    private String phone;
    private Date dob;
    private String gender;
    private String address;
    private String nationality;
    private String bio;
    private String profilePictureUrl;

    // Default constructor
    public ProfileSettingDTO() {}

    // Constructor with required fields
    public ProfileSettingDTO(int userId, String username) {
        this.userId = userId;
        this.username = username;
    }

    // All args constructor
    public ProfileSettingDTO(int userId, String username, String fullName,
                             String phone, Date dob, String gender, String address,
                             String nationality, String bio, String profilePictureUrl) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.phone = phone;
        this.dob = dob;
        this.gender = gender;
        this.address = address;
        this.nationality = nationality;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
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

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ProfileSettingDTO dto;

        public Builder() {
            dto = new ProfileSettingDTO();
        }

        public Builder userId(int userId) {
            dto.setUserId(userId);
            return this;
        }

        public Builder username(String username) {
            dto.setUsername(username);
            return this;
        }

        public Builder fullName(String fullName) {
            dto.setFullName(fullName);
            return this;
        }

        public Builder phone(String phone) {
            dto.setPhone(phone);
            return this;
        }

        public Builder dob(Date dob) {
            dto.setDob(dob);
            return this;
        }

        public Builder gender(String gender) {
            dto.setGender(gender);
            return this;
        }

        public Builder address(String address) {
            dto.setAddress(address);
            return this;
        }

        public Builder nationality(String nationality) {
            dto.setNationality(nationality);
            return this;
        }

        public Builder bio(String bio) {
            dto.setBio(bio);
            return this;
        }

        public Builder profilePictureUrl(String profilePictureUrl) {
            dto.setProfilePictureUrl(profilePictureUrl);
            return this;
        }

        public ProfileSettingDTO build() {
            return dto;
        }
    }

    // toString method for logging
    @Override
    public String toString() {
        return "ProfileSettingDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", dob=" + dob +
                ", gender='" + gender + '\'' +
                ", address='" + address + '\'' +
                ", nationality='" + nationality + '\'' +
                ", bio='" + bio + '\'' +
                ", profilePictureUrl='" + profilePictureUrl + '\'' +
                '}';
    }

    // equals and hashCode for comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProfileSettingDTO that = (ProfileSettingDTO) o;
        return userId == that.userId &&
                Objects.equals(username, that.username) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(phone, that.phone) &&
                Objects.equals(dob, that.dob) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(address, that.address) &&
                Objects.equals(nationality, that.nationality) &&
                Objects.equals(bio, that.bio) &&
                Objects.equals(profilePictureUrl, that.profilePictureUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, fullName, phone, dob,
                gender, address, nationality, bio, profilePictureUrl);
    }
}