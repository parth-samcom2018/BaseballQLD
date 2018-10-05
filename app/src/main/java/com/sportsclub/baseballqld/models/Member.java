package com.sportsclub.baseballqld.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Member {

    public String username;
    public String access_token;
    public int memberId;

    public String emailID;

    //only gotten by calling memberdetails node;
    public String firstName;
    public String lastName;
    public String email;
    public String displayName;
    public String country;
    public String dateOfBirth;
    public String birthYear;
    public String dateJoined;
    public String gender;
    public String avatarUrl;
    public String profileUrl;
    public String aboutMe;
    public String postCode;

    @SerializedName("error")
    private boolean error;
    @SerializedName("message")
    private String message;
    @SerializedName("data")
    private DataEntity data;

    public String getBirthYearFromDOB()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss a");

        try {
            Date birthYear = formatter.parse(this.dateOfBirth);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthYear);
            return ""+calendar.get(Calendar.YEAR);

        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("hq","could not parse birth year:"+e.getMessage());
            return "";
        }
    }

    public void copyAttributesFromDetails(Member.DataEntity d)
    {
        this.firstName = d.firstNameX;
        this.lastName = d.lastNameX;
        this.email = d.emailX;
        this.displayName = d.displayNameX;
        this.country = d.countryX;
        this.dateOfBirth = d.dateOfBirthX;
        this.dateJoined = d.dateJoinedX;
        this.gender = d.genderX;
        this.avatarUrl = d.avatarUrlX;
        this.profileUrl = d.profileUrlX;
        //this.aboutMe = d.aboutMe;
        this.postCode = d.postCodeX;
        this.birthYear = d.birthYearX;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }


    public static class DataEntity {


        @SerializedName("memberId")
        private int memberIdX;
        @SerializedName("username")
        private String usernameX;
        @SerializedName("firstName")
        private String firstNameX;
        @SerializedName("lastName")
        private String lastNameX;
        @SerializedName("email")
        private String emailX;
        @SerializedName("displayName")
        private String displayNameX;
        @SerializedName("country")
        private String countryX;
        @SerializedName("dateOfBirth")
        private String dateOfBirthX;
        @SerializedName("dateJoined")
        private String dateJoinedX;
        @SerializedName("gender")
        private String genderX;
        @SerializedName("avatarUrl")
        private String avatarUrlX;
        @SerializedName("profileUrl")
        private String profileUrlX;
        @SerializedName("postCode")
        private String postCodeX;
        @SerializedName("suburb")
        private Object suburb;
        @SerializedName("birthYear")
        private String birthYearX;

        public int getMemberIdX() {
            return memberIdX;
        }

        public void setMemberIdX(int memberIdX) {
            this.memberIdX = memberIdX;
        }

        public String getUsernameX() {
            return usernameX;
        }

        public void setUsernameX(String usernameX) {
            this.usernameX = usernameX;
        }

        public String getFirstNameX() {
            return firstNameX;
        }

        public void setFirstNameX(String firstNameX) {
            this.firstNameX = firstNameX;
        }

        public String getLastNameX() {
            return lastNameX;
        }

        public void setLastNameX(String lastNameX) {
            this.lastNameX = lastNameX;
        }

        public String getEmailX() {
            return emailX;
        }

        public void setEmailX(String emailX) {
            this.emailX = emailX;
        }

        public String getDisplayNameX() {
            return displayNameX;
        }

        public void setDisplayNameX(String displayNameX) {
            this.displayNameX = displayNameX;
        }

        public String getCountryX() {
            return countryX;
        }

        public void setCountryX(String countryX) {
            this.countryX = countryX;
        }

        public String getDateOfBirthX() {
            return dateOfBirthX;
        }

        public void setDateOfBirthX(String dateOfBirthX) {
            this.dateOfBirthX = dateOfBirthX;
        }

        public String getDateJoinedX() {
            return dateJoinedX;
        }

        public void setDateJoinedX(String dateJoinedX) {
            this.dateJoinedX = dateJoinedX;
        }

        public String getGenderX() {
            return genderX;
        }

        public void setGenderX(String genderX) {
            this.genderX = genderX;
        }

        public String getAvatarUrlX() {
            return avatarUrlX;
        }

        public void setAvatarUrlX(String avatarUrlX) {
            this.avatarUrlX = avatarUrlX;
        }

        public String getProfileUrlX() {
            return profileUrlX;
        }

        public void setProfileUrlX(String profileUrlX) {
            this.profileUrlX = profileUrlX;
        }

        public String getPostCodeX() {
            return postCodeX;
        }

        public void setPostCodeX(String postCodeX) {
            this.postCodeX = postCodeX;
        }

        public Object getSuburb() {
            return suburb;
        }

        public void setSuburb(Object suburb) {
            this.suburb = suburb;
        }

        public String getBirthYearX() {
            return birthYearX;
        }

        public void setBirthYearX(String birthYearX) {
            this.birthYearX = birthYearX;
        }
    }
}
