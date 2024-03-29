package fr.polytech.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "address", schema = "public")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String street;

    private String number;

    private String city;

    private String zipCode;
    private String country;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street.toUpperCase();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number.toUpperCase();
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city.toUpperCase();
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode.toUpperCase();
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country.toUpperCase();
    }
}
