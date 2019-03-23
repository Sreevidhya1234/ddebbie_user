package com.DDebbieinc.entity;

/**
 * Created by savera on 11/2/16.
 */
public class UserDetailsPojo {

    private String id;
    private String customerName;
    private String contactNumber;
    private String emgContactNumber;
    private String email;
    private String status;
    private String photo;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmgContactNumber() {
        return emgContactNumber;
    }

    public void setEmgContactNumber(String emgContactNumber) {
        this.emgContactNumber = emgContactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
