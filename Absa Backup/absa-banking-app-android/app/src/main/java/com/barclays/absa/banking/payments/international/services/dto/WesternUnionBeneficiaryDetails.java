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

import com.barclays.absa.banking.boundary.shared.dto.SureCheckResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WesternUnionBeneficiaryDetails extends SureCheckResponse {
    @JsonProperty("nonResidentAccountIdentifier")
    private String nonResidentAccountIdentifier;
    @JsonProperty("beneficiaryAddressLine")
    private String beneficiaryAddressLine;
    @JsonProperty("beneficiaryFirstName")
    private String beneficiaryFirstName;
    @JsonProperty("beneficiaryReference")
    private String beneficiaryReference;
    @JsonProperty("beneficiarySurname")
    private String beneficiarySurname;
    @JsonProperty("paymentType")
    private String paymentType;
    @JsonProperty("eftNumber")
    private String eftNumber;
    @JsonProperty("accountNo")
    private String accountNumber;
    @JsonProperty("lastPayDate")
    private String lastPayDate;
    @JsonProperty("swiftDetailsDTO")
    private SwiftDetails swiftDetails;
    @JsonProperty("id")
    private String id;
    @JsonProperty("refName")
    private String referenceName;
    @JsonProperty("sequenceNumber")
    private String sequenceNumber;
    @JsonProperty("beneficiaryState")
    private String beneficiaryState;
    @JsonProperty("imageName")
    private String imageName;
    @JsonProperty("accountType")
    private String accountType;
    @JsonProperty("beneficiaryShortName")
    private String beneficiaryShortName;
    @JsonProperty("ownNotificationType")
    private String ownNotificationType;
    @JsonProperty("foreignCurrencyWithZAR")
    private String foreignCurrencyWithZAR;
    @JsonProperty("beneficiaryCountryDTO")
    private ResidingCountry beneficiaryCountry;
    @JsonProperty("beneficiarySuburb")
    private String beneficiarySuburb;
    @JsonProperty("westernUnionDetailsDTO")
    private WesternUnionDetails westernUnionDetails;
    @JsonProperty("statementReference")
    private String statementReference;
    @JsonProperty("natureOfPaymentDTO")
    private NatureOfPayment natureOfPayment;
    @JsonProperty("beneficiaryCity")
    private String beneficiaryCity;
    @JsonProperty("tiebNumber")
    private String tiebNumber;
    @JsonProperty("beneficiaryStreet")
    private String beneficiaryStreet;
    @JsonProperty("cifkey")
    private String cifkey;
    @JsonProperty("transferType")
    private String transferType;
    @JsonProperty("beneficiaryIFTType")
    private String beneficiaryIFTType;
    @JsonProperty("lastPayment")
    private String lastPayment;
    @JsonProperty("subType")
    private String subType;
    @JsonProperty("status")
    private String status;
    @JsonProperty("iftPaymentList")
    private List<InternationalPaymentDetails> internationalPaymentDetailsList;

    public WesternUnionBeneficiaryDetails() {
    }

    public String getNonResidentAccountIdentifier() {
        return this.nonResidentAccountIdentifier;
    }

    public String getBeneficiaryAddressLine() {
        return this.beneficiaryAddressLine;
    }

    public String getBeneficiaryFirstName() {
        return this.beneficiaryFirstName;
    }

    public String getBeneficiaryReference() {
        return this.beneficiaryReference;
    }

    public String getBeneficiarySurname() {
        return this.beneficiarySurname;
    }

    public String getPaymentType() {
        return this.paymentType;
    }

    public String getEftNumber() {
        return this.eftNumber;
    }

    public String getAccountNumber() {
        return this.accountNumber;
    }

    public String getLastPayDate() {
        return this.lastPayDate;
    }

    public SwiftDetails getSwiftDetails() {
        return this.swiftDetails;
    }

    public String getId() {
        return this.id;
    }

    public String getReferenceName() {
        return this.referenceName;
    }

    public String getSequenceNumber() {
        return this.sequenceNumber;
    }

    public String getBeneficiaryState() {
        return this.beneficiaryState;
    }

    public String getImageName() {
        return this.imageName;
    }

    public String getAccountType() {
        return this.accountType;
    }

    public String getBeneficiaryShortName() {
        return this.beneficiaryShortName;
    }

    public String getOwnNotificationType() {
        return this.ownNotificationType;
    }

    public String getForeignCurrencyWithZAR() {
        return this.foreignCurrencyWithZAR;
    }

    public ResidingCountry getBeneficiaryCountry() {
        return this.beneficiaryCountry;
    }

    public String getBeneficiarySuburb() {
        return this.beneficiarySuburb;
    }

    public WesternUnionDetails getWesternUnionDetails() {
        return this.westernUnionDetails;
    }

    public String getStatementReference() {
        return this.statementReference;
    }

    public NatureOfPayment getNatureOfPayment() {
        return this.natureOfPayment;
    }

    public String getBeneficiaryCity() {
        return this.beneficiaryCity;
    }

    public String getTiebNumber() {
        return this.tiebNumber;
    }

    public String getBeneficiaryStreet() {
        return this.beneficiaryStreet;
    }

    public String getCifkey() {
        return this.cifkey;
    }

    public String getTransferType() {
        return this.transferType;
    }

    public String getBeneficiaryIFTType() {
        return this.beneficiaryIFTType;
    }

    public String getLastPayment() {
        return this.lastPayment;
    }

    public String getSubType() {
        return this.subType;
    }

    public String getStatus() {
        return this.status;
    }

    public List<InternationalPaymentDetails> getInternationalPaymentDetailsList() {
        return this.internationalPaymentDetailsList;
    }

    public void setNonResidentAccountIdentifier(String nonResidentAccountIdentifier) {
        this.nonResidentAccountIdentifier = nonResidentAccountIdentifier;
    }

    public void setBeneficiaryAddressLine(String beneficiaryAddressLine) {
        this.beneficiaryAddressLine = beneficiaryAddressLine;
    }

    public void setBeneficiaryFirstName(String beneficiaryFirstName) {
        this.beneficiaryFirstName = beneficiaryFirstName;
    }

    public void setBeneficiaryReference(String beneficiaryReference) {
        this.beneficiaryReference = beneficiaryReference;
    }

    public void setBeneficiarySurname(String beneficiarySurname) {
        this.beneficiarySurname = beneficiarySurname;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public void setEftNumber(String eftNumber) {
        this.eftNumber = eftNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setLastPayDate(String lastPayDate) {
        this.lastPayDate = lastPayDate;
    }

    public void setSwiftDetails(SwiftDetails swiftDetails) {
        this.swiftDetails = swiftDetails;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setBeneficiaryState(String beneficiaryState) {
        this.beneficiaryState = beneficiaryState;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setBeneficiaryShortName(String beneficiaryShortName) {
        this.beneficiaryShortName = beneficiaryShortName;
    }

    public void setOwnNotificationType(String ownNotificationType) {
        this.ownNotificationType = ownNotificationType;
    }

    public void setForeignCurrencyWithZAR(String foreignCurrencyWithZAR) {
        this.foreignCurrencyWithZAR = foreignCurrencyWithZAR;
    }

    public void setBeneficiaryCountry(ResidingCountry beneficiaryCountry) {
        this.beneficiaryCountry = beneficiaryCountry;
    }

    public void setBeneficiarySuburb(String beneficiarySuburb) {
        this.beneficiarySuburb = beneficiarySuburb;
    }

    public void setWesternUnionDetails(WesternUnionDetails westernUnionDetails) {
        this.westernUnionDetails = westernUnionDetails;
    }

    public void setStatementReference(String statementReference) {
        this.statementReference = statementReference;
    }

    public void setNatureOfPayment(NatureOfPayment natureOfPayment) {
        this.natureOfPayment = natureOfPayment;
    }

    public void setBeneficiaryCity(String beneficiaryCity) {
        this.beneficiaryCity = beneficiaryCity;
    }

    public void setTiebNumber(String tiebNumber) {
        this.tiebNumber = tiebNumber;
    }

    public void setBeneficiaryStreet(String beneficiaryStreet) {
        this.beneficiaryStreet = beneficiaryStreet;
    }

    public void setCifkey(String cifkey) {
        this.cifkey = cifkey;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public void setBeneficiaryIFTType(String beneficiaryIFTType) {
        this.beneficiaryIFTType = beneficiaryIFTType;
    }

    public void setLastPayment(String lastPayment) {
        this.lastPayment = lastPayment;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setInternationalPaymentDetailsList(List<InternationalPaymentDetails> internationalPaymentDetailsList) {
        this.internationalPaymentDetailsList = internationalPaymentDetailsList;
    }

    @NotNull
    public String toString() {
        return "com.barclays.absa.banking.boundary.international_payments.dto.WesternUnionBeneficiaryDetails(nonResidentAccountIdentifier=" + this.nonResidentAccountIdentifier + ", beneficiaryAddressLine=" + this.beneficiaryAddressLine + ", beneficiaryFirstName=" + this.beneficiaryFirstName + ", beneficiaryReference=" + this.beneficiaryReference + ", beneficiarySurname=" + this.beneficiarySurname + ", paymentType=" + this.paymentType + ", eftNumber=" + this.eftNumber + ", accountNumber=" + this.accountNumber + ", lastPayDate=" + this.lastPayDate + ", swiftDetails=" + this.swiftDetails + ", id=" + this.id + ", referenceName=" + this.referenceName + ", sequenceNumber=" + this.sequenceNumber + ", beneficiaryState=" + this.beneficiaryState + ", imageName=" + this.imageName + ", accountType=" + this.accountType + ", beneficiaryShortName=" + this.beneficiaryShortName + ", ownNotificationType=" + this.ownNotificationType + ", foreignCurrencyWithZAR=" + this.foreignCurrencyWithZAR + ", beneficiaryCountry=" + this.beneficiaryCountry + ", beneficiarySuburb=" + this.beneficiarySuburb + ", westernUnionDetails=" + this.westernUnionDetails + ", statementReference=" + this.statementReference + ", natureOfPayment=" + this.natureOfPayment + ", beneficiaryCity=" + this.beneficiaryCity + ", tiebNumber=" + this.tiebNumber + ", beneficiaryStreet=" + this.beneficiaryStreet + ", cifkey=" + this.cifkey + ", transferType=" + this.transferType + ", beneficiaryIFTType=" + this.beneficiaryIFTType + ", lastPayment=" + this.lastPayment + ", subType=" + this.subType + ", status=" + this.status + ", internationalPaymentDetailsList=" + this.internationalPaymentDetailsList + ")";
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof WesternUnionBeneficiaryDetails)) return false;
        final WesternUnionBeneficiaryDetails other = (WesternUnionBeneficiaryDetails) o;
        if (!other.canEqual(this)) return false;
        final Object this$nonResidentAccountIdentifier = this.getNonResidentAccountIdentifier();
        final Object other$nonResidentAccountIdentifier = other.getNonResidentAccountIdentifier();
        if (this$nonResidentAccountIdentifier == null ? other$nonResidentAccountIdentifier != null : !this$nonResidentAccountIdentifier.equals(other$nonResidentAccountIdentifier))
            return false;
        final Object this$beneficiaryAddressLine = this.getBeneficiaryAddressLine();
        final Object other$beneficiaryAddressLine = other.getBeneficiaryAddressLine();
        if (this$beneficiaryAddressLine == null ? other$beneficiaryAddressLine != null : !this$beneficiaryAddressLine.equals(other$beneficiaryAddressLine))
            return false;
        final Object this$beneficiaryFirstName = this.getBeneficiaryFirstName();
        final Object other$beneficiaryFirstName = other.getBeneficiaryFirstName();
        if (this$beneficiaryFirstName == null ? other$beneficiaryFirstName != null : !this$beneficiaryFirstName.equals(other$beneficiaryFirstName))
            return false;
        final Object this$beneficiaryReference = this.getBeneficiaryReference();
        final Object other$beneficiaryReference = other.getBeneficiaryReference();
        if (this$beneficiaryReference == null ? other$beneficiaryReference != null : !this$beneficiaryReference.equals(other$beneficiaryReference))
            return false;
        final Object this$beneficiarySurname = this.getBeneficiarySurname();
        final Object other$beneficiarySurname = other.getBeneficiarySurname();
        if (this$beneficiarySurname == null ? other$beneficiarySurname != null : !this$beneficiarySurname.equals(other$beneficiarySurname))
            return false;
        final Object this$paymentType = this.getPaymentType();
        final Object other$paymentType = other.getPaymentType();
        if (this$paymentType == null ? other$paymentType != null : !this$paymentType.equals(other$paymentType))
            return false;
        final Object this$eftNumber = this.getEftNumber();
        final Object other$eftNumber = other.getEftNumber();
        if (this$eftNumber == null ? other$eftNumber != null : !this$eftNumber.equals(other$eftNumber))
            return false;
        final Object this$accountNumber = this.getAccountNumber();
        final Object other$accountNumber = other.getAccountNumber();
        if (this$accountNumber == null ? other$accountNumber != null : !this$accountNumber.equals(other$accountNumber))
            return false;
        final Object this$lastPayDate = this.getLastPayDate();
        final Object other$lastPayDate = other.getLastPayDate();
        if (this$lastPayDate == null ? other$lastPayDate != null : !this$lastPayDate.equals(other$lastPayDate))
            return false;
        final Object this$swiftDetails = this.getSwiftDetails();
        final Object other$swiftDetails = other.getSwiftDetails();
        if (this$swiftDetails == null ? other$swiftDetails != null : !this$swiftDetails.equals(other$swiftDetails))
            return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$referenceName = this.getReferenceName();
        final Object other$referenceName = other.getReferenceName();
        if (this$referenceName == null ? other$referenceName != null : !this$referenceName.equals(other$referenceName))
            return false;
        final Object this$sequenceNumber = this.getSequenceNumber();
        final Object other$sequenceNumber = other.getSequenceNumber();
        if (this$sequenceNumber == null ? other$sequenceNumber != null : !this$sequenceNumber.equals(other$sequenceNumber))
            return false;
        final Object this$beneficiaryState = this.getBeneficiaryState();
        final Object other$beneficiaryState = other.getBeneficiaryState();
        if (this$beneficiaryState == null ? other$beneficiaryState != null : !this$beneficiaryState.equals(other$beneficiaryState))
            return false;
        final Object this$imageName = this.getImageName();
        final Object other$imageName = other.getImageName();
        if (this$imageName == null ? other$imageName != null : !this$imageName.equals(other$imageName))
            return false;
        final Object this$accountType = this.getAccountType();
        final Object other$accountType = other.getAccountType();
        if (this$accountType == null ? other$accountType != null : !this$accountType.equals(other$accountType))
            return false;
        final Object this$beneficiaryShortName = this.getBeneficiaryShortName();
        final Object other$beneficiaryShortName = other.getBeneficiaryShortName();
        if (this$beneficiaryShortName == null ? other$beneficiaryShortName != null : !this$beneficiaryShortName.equals(other$beneficiaryShortName))
            return false;
        final Object this$ownNotificationType = this.getOwnNotificationType();
        final Object other$ownNotificationType = other.getOwnNotificationType();
        if (this$ownNotificationType == null ? other$ownNotificationType != null : !this$ownNotificationType.equals(other$ownNotificationType))
            return false;
        final Object this$foreignCurrencyWithZAR = this.getForeignCurrencyWithZAR();
        final Object other$foreignCurrencyWithZAR = other.getForeignCurrencyWithZAR();
        if (this$foreignCurrencyWithZAR == null ? other$foreignCurrencyWithZAR != null : !this$foreignCurrencyWithZAR.equals(other$foreignCurrencyWithZAR))
            return false;
        final Object this$beneficiaryCountry = this.getBeneficiaryCountry();
        final Object other$beneficiaryCountry = other.getBeneficiaryCountry();
        if (this$beneficiaryCountry == null ? other$beneficiaryCountry != null : !this$beneficiaryCountry.equals(other$beneficiaryCountry))
            return false;
        final Object this$beneficiarySuburb = this.getBeneficiarySuburb();
        final Object other$beneficiarySuburb = other.getBeneficiarySuburb();
        if (this$beneficiarySuburb == null ? other$beneficiarySuburb != null : !this$beneficiarySuburb.equals(other$beneficiarySuburb))
            return false;
        final Object this$westernUnionDetails = this.getWesternUnionDetails();
        final Object other$westernUnionDetails = other.getWesternUnionDetails();
        if (this$westernUnionDetails == null ? other$westernUnionDetails != null : !this$westernUnionDetails.equals(other$westernUnionDetails))
            return false;
        final Object this$statementReference = this.getStatementReference();
        final Object other$statementReference = other.getStatementReference();
        if (this$statementReference == null ? other$statementReference != null : !this$statementReference.equals(other$statementReference))
            return false;
        final Object this$natureOfPayment = this.getNatureOfPayment();
        final Object other$natureOfPayment = other.getNatureOfPayment();
        if (this$natureOfPayment == null ? other$natureOfPayment != null : !this$natureOfPayment.equals(other$natureOfPayment))
            return false;
        final Object this$beneficiaryCity = this.getBeneficiaryCity();
        final Object other$beneficiaryCity = other.getBeneficiaryCity();
        if (this$beneficiaryCity == null ? other$beneficiaryCity != null : !this$beneficiaryCity.equals(other$beneficiaryCity))
            return false;
        final Object this$tiebNumber = this.getTiebNumber();
        final Object other$tiebNumber = other.getTiebNumber();
        if (this$tiebNumber == null ? other$tiebNumber != null : !this$tiebNumber.equals(other$tiebNumber))
            return false;
        final Object this$beneficiaryStreet = this.getBeneficiaryStreet();
        final Object other$beneficiaryStreet = other.getBeneficiaryStreet();
        if (this$beneficiaryStreet == null ? other$beneficiaryStreet != null : !this$beneficiaryStreet.equals(other$beneficiaryStreet))
            return false;
        final Object this$cifkey = this.getCifkey();
        final Object other$cifkey = other.getCifkey();
        if (this$cifkey == null ? other$cifkey != null : !this$cifkey.equals(other$cifkey))
            return false;
        final Object this$transferType = this.getTransferType();
        final Object other$transferType = other.getTransferType();
        if (this$transferType == null ? other$transferType != null : !this$transferType.equals(other$transferType))
            return false;
        final Object this$beneficiaryIFTType = this.getBeneficiaryIFTType();
        final Object other$beneficiaryIFTType = other.getBeneficiaryIFTType();
        if (this$beneficiaryIFTType == null ? other$beneficiaryIFTType != null : !this$beneficiaryIFTType.equals(other$beneficiaryIFTType))
            return false;
        final Object this$lastPayment = this.getLastPayment();
        final Object other$lastPayment = other.getLastPayment();
        if (this$lastPayment == null ? other$lastPayment != null : !this$lastPayment.equals(other$lastPayment))
            return false;
        final Object this$subType = this.getSubType();
        final Object other$subType = other.getSubType();
        if (this$subType == null ? other$subType != null : !this$subType.equals(other$subType))
            return false;
        final Object this$status = this.getStatus();
        final Object other$status = other.getStatus();
        if (this$status == null ? other$status != null : !this$status.equals(other$status))
            return false;
        final Object this$internationalPaymentDetailsList = this.getInternationalPaymentDetailsList();
        final Object other$internationalPaymentDetailsList = other.getInternationalPaymentDetailsList();
        return this$internationalPaymentDetailsList == null ? other$internationalPaymentDetailsList == null : this$internationalPaymentDetailsList.equals(other$internationalPaymentDetailsList);
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $nonResidentAccountIdentifier = this.getNonResidentAccountIdentifier();
        result = result * PRIME + ($nonResidentAccountIdentifier == null ? 0 : $nonResidentAccountIdentifier.hashCode());
        final Object $beneficiaryAddressLine = this.getBeneficiaryAddressLine();
        result = result * PRIME + ($beneficiaryAddressLine == null ? 0 : $beneficiaryAddressLine.hashCode());
        final Object $beneficiaryFirstName = this.getBeneficiaryFirstName();
        result = result * PRIME + ($beneficiaryFirstName == null ? 0 : $beneficiaryFirstName.hashCode());
        final Object $beneficiaryReference = this.getBeneficiaryReference();
        result = result * PRIME + ($beneficiaryReference == null ? 0 : $beneficiaryReference.hashCode());
        final Object $beneficiarySurname = this.getBeneficiarySurname();
        result = result * PRIME + ($beneficiarySurname == null ? 0 : $beneficiarySurname.hashCode());
        final Object $paymentType = this.getPaymentType();
        result = result * PRIME + ($paymentType == null ? 0 : $paymentType.hashCode());
        final Object $eftNumber = this.getEftNumber();
        result = result * PRIME + ($eftNumber == null ? 0 : $eftNumber.hashCode());
        final Object $accountNumber = this.getAccountNumber();
        result = result * PRIME + ($accountNumber == null ? 0 : $accountNumber.hashCode());
        final Object $lastPayDate = this.getLastPayDate();
        result = result * PRIME + ($lastPayDate == null ? 0 : $lastPayDate.hashCode());
        final Object $swiftDetails = this.getSwiftDetails();
        result = result * PRIME + ($swiftDetails == null ? 0 : $swiftDetails.hashCode());
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 0 : $id.hashCode());
        final Object $referenceName = this.getReferenceName();
        result = result * PRIME + ($referenceName == null ? 0 : $referenceName.hashCode());
        final Object $sequenceNumber = this.getSequenceNumber();
        result = result * PRIME + ($sequenceNumber == null ? 0 : $sequenceNumber.hashCode());
        final Object $beneficiaryState = this.getBeneficiaryState();
        result = result * PRIME + ($beneficiaryState == null ? 0 : $beneficiaryState.hashCode());
        final Object $imageName = this.getImageName();
        result = result * PRIME + ($imageName == null ? 0 : $imageName.hashCode());
        final Object $accountType = this.getAccountType();
        result = result * PRIME + ($accountType == null ? 0 : $accountType.hashCode());
        final Object $beneficiaryShortName = this.getBeneficiaryShortName();
        result = result * PRIME + ($beneficiaryShortName == null ? 0 : $beneficiaryShortName.hashCode());
        final Object $ownNotificationType = this.getOwnNotificationType();
        result = result * PRIME + ($ownNotificationType == null ? 0 : $ownNotificationType.hashCode());
        final Object $foreignCurrencyWithZAR = this.getForeignCurrencyWithZAR();
        result = result * PRIME + ($foreignCurrencyWithZAR == null ? 0 : $foreignCurrencyWithZAR.hashCode());
        final Object $beneficiaryCountry = this.getBeneficiaryCountry();
        result = result * PRIME + ($beneficiaryCountry == null ? 0 : $beneficiaryCountry.hashCode());
        final Object $beneficiarySuburb = this.getBeneficiarySuburb();
        result = result * PRIME + ($beneficiarySuburb == null ? 0 : $beneficiarySuburb.hashCode());
        final Object $westernUnionDetails = this.getWesternUnionDetails();
        result = result * PRIME + ($westernUnionDetails == null ? 0 : $westernUnionDetails.hashCode());
        final Object $statementReference = this.getStatementReference();
        result = result * PRIME + ($statementReference == null ? 0 : $statementReference.hashCode());
        final Object $natureOfPayment = this.getNatureOfPayment();
        result = result * PRIME + ($natureOfPayment == null ? 0 : $natureOfPayment.hashCode());
        final Object $beneficiaryCity = this.getBeneficiaryCity();
        result = result * PRIME + ($beneficiaryCity == null ? 0 : $beneficiaryCity.hashCode());
        final Object $tiebNumber = this.getTiebNumber();
        result = result * PRIME + ($tiebNumber == null ? 0 : $tiebNumber.hashCode());
        final Object $beneficiaryStreet = this.getBeneficiaryStreet();
        result = result * PRIME + ($beneficiaryStreet == null ? 0 : $beneficiaryStreet.hashCode());
        final Object $cifkey = this.getCifkey();
        result = result * PRIME + ($cifkey == null ? 0 : $cifkey.hashCode());
        final Object $transferType = this.getTransferType();
        result = result * PRIME + ($transferType == null ? 0 : $transferType.hashCode());
        final Object $beneficiaryIFTType = this.getBeneficiaryIFTType();
        result = result * PRIME + ($beneficiaryIFTType == null ? 0 : $beneficiaryIFTType.hashCode());
        final Object $lastPayment = this.getLastPayment();
        result = result * PRIME + ($lastPayment == null ? 0 : $lastPayment.hashCode());
        final Object $subType = this.getSubType();
        result = result * PRIME + ($subType == null ? 0 : $subType.hashCode());
        final Object $status = this.getStatus();
        result = result * PRIME + ($status == null ? 0 : $status.hashCode());
        final Object $internationalPaymentDetailsList = this.getInternationalPaymentDetailsList();
        result = result * PRIME + ($internationalPaymentDetailsList == null ? 0 : $internationalPaymentDetailsList.hashCode());
        return result;
    }

    private boolean canEqual(Object other) {
        return other instanceof WesternUnionBeneficiaryDetails;
    }
}