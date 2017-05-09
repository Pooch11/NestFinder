package com.alphabgammainc.nestfinder.Classes;

/**
 * The Address object stores the street number , street name ,postal code , city ,country
 * and province.
 * Use this object when you are dealing with an address itself.
 * eg. 4000 Finch Avenue East,M4R1Y3,Toronto,Ontario,Canada
 * Created by soutrikbarua on 2017-04-30.
 */

public class Address {


    private String streetNumber;
    private String streetName;
    private String postalCode;
    private String city;
    private String country;
    private String province;

    public Address(){

    }

    public Address(String streetNumber,String streetName,String city, String province,
                   String postalCode,String country)
    {
        this.streetName=streetName;
        this.streetNumber = streetNumber;
        this.city=city;
        this.province=province;
        this.postalCode=postalCode;
        this.country = country;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

}