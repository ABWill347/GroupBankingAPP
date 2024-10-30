package domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;

@Entity
@Table(name = "ADDRESS")
public class Address {

    @Id
    @GeneratedValue
    @Column(name = "ADDRESS_ID")
    private Long id;

    @NotEmpty
    @Column(name = "STREET_NUMBER")
    private String street_number;

    @NotEmpty
    @Column(name = "STREET_NAME")
    private String street_name;

    @NotEmpty
    @Column(name = "CITY")
    private String city;

    @NotEmpty
    @Column(name = "STATE")
    private String state;

    @NotEmpty
    @Column(name = "ZIP")
    private String zip;


    public Address() {
    }

    public Address(String city, Long id, String state, String street_name, String street_number, String zip) {
        this.city = city;
        this.id = id;
        this.state = state;
        this.street_name = street_name;
        this.street_number = street_number;
        this.zip = zip;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet_number() {
        return street_number;
    }

    public void setStreet_number(String street_number) {
        this.street_number = street_number;
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }


}
