package ru.lapikov.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class PersonFilter {

    private long id;
    private String firstName;
    private String lastName;
    private String middleName;
    private LocalDate dateOfBirth;
    private Gender gender;

    private BigDecimal oldest;
    private BigDecimal youngest;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public BigDecimal getOldest() {
        return oldest;
    }

    public void setOldest(BigDecimal oldest) {
        this.oldest = oldest;
    }

    public BigDecimal getYoungest() {
        return youngest;
    }

    public void setYoungest(BigDecimal youngest) {
        this.youngest = youngest;
    }
}
