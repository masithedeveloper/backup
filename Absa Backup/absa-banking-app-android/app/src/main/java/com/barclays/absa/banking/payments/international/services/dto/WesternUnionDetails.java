/*
 * Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied
 * or distributed other than on a need-to-know basis and any recipients may be
 * required to sign a confidentiality undertaking in favor of Absa Bank
 * Limited
 *
 */
package com.barclays.absa.banking.payments.international.services.dto;

import com.barclays.absa.banking.framework.data.ResponseObject;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

public class WesternUnionDetails extends ResponseObject {
    @JsonProperty("compNameDesc")
    private String companyNameDescription;
    @JsonProperty("gender")
    private String gender;
    @JsonProperty("streetAddress")
    private String streetAddress;
    @JsonProperty("residingCountryDTO")
    private ResidingCountry residingCountry;
    @JsonProperty("compNameCode")
    private String companyNameCode;

    public WesternUnionDetails() {
    }

    private String getCompanyNameDescription() {
        return this.companyNameDescription;
    }

    public String getGender() {
        return this.gender;
    }

    public String getStreetAddress() {
        return this.streetAddress;
    }

    public ResidingCountry getResidingCountry() {
        return this.residingCountry;
    }

    private String getCompanyNameCode() {
        return this.companyNameCode;
    }

    public void setCompanyNameDescription(String companyNameDescription) {
        this.companyNameDescription = companyNameDescription;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setResidingCountry(ResidingCountry residingCountry) {
        this.residingCountry = residingCountry;
    }

    public void setCompanyNameCode(String companyNameCode) {
        this.companyNameCode = companyNameCode;
    }

    @NotNull
    public String toString() {
        return "com.barclays.absa.banking.boundary.international_payments.dto.WesternUnionDetails(companyNameDescription=" + this.companyNameDescription + ", gender=" + this.gender + ", streetAddress=" + this.streetAddress + ", residingCountry=" + this.residingCountry + ", companyNameCode=" + this.companyNameCode + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof WesternUnionDetails)) return false;
        final WesternUnionDetails other = (WesternUnionDetails) o;
        if (!other.canEqual(this)) return false;
        final Object this$companyNameDescription = this.getCompanyNameDescription();
        final Object other$companyNameDescription = other.getCompanyNameDescription();
        if (this$companyNameDescription == null ? other$companyNameDescription != null : !this$companyNameDescription.equals(other$companyNameDescription))
            return false;
        final Object this$gender = this.getGender();
        final Object other$gender = other.getGender();
        if (this$gender == null ? other$gender != null : !this$gender.equals(other$gender))
            return false;
        final Object this$streetAddress = this.getStreetAddress();
        final Object other$streetAddress = other.getStreetAddress();
        if (this$streetAddress == null ? other$streetAddress != null : !this$streetAddress.equals(other$streetAddress))
            return false;
        final Object this$residingCountry = this.getResidingCountry();
        final Object other$residingCountry = other.getResidingCountry();
        if (this$residingCountry == null ? other$residingCountry != null : !this$residingCountry.equals(other$residingCountry))
            return false;
        final Object this$companyNameCode = this.getCompanyNameCode();
        final Object other$companyNameCode = other.getCompanyNameCode();
        return this$companyNameCode == null ? other$companyNameCode == null : this$companyNameCode.equals(other$companyNameCode);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $companyNameDescription = this.getCompanyNameDescription();
        result = result * PRIME + ($companyNameDescription == null ? 0 : $companyNameDescription.hashCode());
        final Object $gender = this.getGender();
        result = result * PRIME + ($gender == null ? 0 : $gender.hashCode());
        final Object $streetAddress = this.getStreetAddress();
        result = result * PRIME + ($streetAddress == null ? 0 : $streetAddress.hashCode());
        final Object $residingCountry = this.getResidingCountry();
        result = result * PRIME + ($residingCountry == null ? 0 : $residingCountry.hashCode());
        final Object $companyNameCode = this.getCompanyNameCode();
        result = result * PRIME + ($companyNameCode == null ? 0 : $companyNameCode.hashCode());
        return result;
    }

    private boolean canEqual(Object other) {
        return other instanceof WesternUnionDetails;
    }
}