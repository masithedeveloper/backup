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

package com.barclays.absa.banking.newToBank;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.barclays.absa.banking.boundary.docHandler.DocHandlerGetCaseResponseListener;
import com.barclays.absa.banking.boundary.docHandler.DocHandlerInteractor;
import com.barclays.absa.banking.boundary.docHandler.DocHandlerUploadResponseListener;
import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerDocument;
import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerGetCaseByIdResponse;
import com.barclays.absa.banking.boundary.docHandler.dto.DocHandlerUploadDocumentResponse;
import com.barclays.absa.banking.boundary.shared.dto.TransactionResponse;
import com.barclays.absa.banking.framework.ExtendedResponseListener;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.framework.utils.AppConstants;
import com.barclays.absa.banking.newToBank.dto.CustomerPortfolioInfo;
import com.barclays.absa.banking.newToBank.dto.NewToBankTempData;
import com.barclays.absa.banking.newToBank.services.NewToBankInteractor;
import com.barclays.absa.banking.newToBank.services.NewToBankService;
import com.barclays.absa.banking.newToBank.services.dto.AbsaRewardsResponse;
import com.barclays.absa.banking.newToBank.services.dto.BusinessEvolveCardPackageResponse;
import com.barclays.absa.banking.newToBank.services.dto.CardPackageResponse;
import com.barclays.absa.banking.newToBank.services.dto.CodesLookupDetailsSelector;
import com.barclays.absa.banking.newToBank.services.dto.ConfigValue;
import com.barclays.absa.banking.newToBank.services.dto.CreateCombiCardAccountResponse;
import com.barclays.absa.banking.newToBank.services.dto.CreateCombiDetails;
import com.barclays.absa.banking.newToBank.services.dto.CreateCustomerPortfolioAccountResponse;
import com.barclays.absa.banking.newToBank.services.dto.CustomerDetails;
import com.barclays.absa.banking.newToBank.services.dto.ExpressPreLogonRequestSecurityNotificationResponse;
import com.barclays.absa.banking.newToBank.services.dto.ExpressPreLogonResendSecurityNotificationResponse;
import com.barclays.absa.banking.newToBank.services.dto.ExpressPreLogonValidateSecurityNotificationResponse;
import com.barclays.absa.banking.newToBank.services.dto.GetAllConfigsForApplicationResponse;
import com.barclays.absa.banking.newToBank.services.dto.GetFilteredSiteDetailsResponse;
import com.barclays.absa.banking.newToBank.services.dto.GetScoringStatusResponse;
import com.barclays.absa.banking.newToBank.services.dto.IdDocumentType;
import com.barclays.absa.banking.newToBank.services.dto.NewToBankKeepAliveResponse;
import com.barclays.absa.banking.newToBank.services.dto.PerformCasaRiskProfilingResponse;
import com.barclays.absa.banking.newToBank.services.dto.PerformPhotoMatchAndMobileLookupDTO;
import com.barclays.absa.banking.newToBank.services.dto.PhotoMatchAndMobileLookUpResponse;
import com.barclays.absa.banking.newToBank.services.dto.ProofOfResidenceResponse;
import com.barclays.absa.banking.newToBank.services.dto.RegisterOnlineBankingPasswordResponse;
import com.barclays.absa.banking.newToBank.services.dto.RegistrationNewApplicationCustomerResponse;
import com.barclays.absa.banking.newToBank.services.dto.ResultAnimations;
import com.barclays.absa.banking.newToBank.services.dto.RetrieveIdOcrDetailsFromDocumentResponse;
import com.barclays.absa.banking.newToBank.services.dto.ValidateCustomerAndCreateCaseResponse;
import com.barclays.absa.banking.shared.services.SharedInteractor;
import com.barclays.absa.banking.shared.services.SharedService;
import com.barclays.absa.banking.shared.services.dto.CIFCodeLookUpType;
import com.barclays.absa.banking.shared.services.dto.CodesLookupDetails;
import com.barclays.absa.banking.shared.services.dto.GetCodesResponse;
import com.barclays.absa.banking.shared.services.dto.SourceOfFundsLookUpType;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import styleguide.forms.SelectorList;
import styleguide.utils.extensions.StringExtensions;

import static com.barclays.absa.banking.framework.app.BMBConstants.FAILED;
import static com.barclays.absa.banking.framework.app.BMBConstants.FAILURE;
import static com.barclays.absa.banking.framework.app.BMBConstants.SUCCESS;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.COUNTRY_OF_BIRTH;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.EMPLOYMENT_SECTOR;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.EMPLOYMENT_STATUS;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.MEDICAL_OCCUPATION;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.MONTHLY_INCOME_RANGE;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.NATIONALITY;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.OCCUPATION_CODE;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.OCCUPATION_LEVEL;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.RESIDENTIAL_STATUS;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.REWARDS_CONFIG_DATE_KEY;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.REWARDS_CONFIG_KEY;
import static com.barclays.absa.banking.newToBank.NewToBankConstants.SOURCE_OF_FUNDS;

public class NewToBankPresenter {

    private static final List<String> SCORING_SUCCESS_STATUS = Arrays.asList("ACCEPT", "PRE_APPROVED", "NOT_APPLICABLE");
    private static final List<String> SCORING_REFERRAL_STATUS = Arrays.asList("CREDIT_REFERRAL", "SALES_REFERRAL", "FRAUD_REFERRAL");
    private static final List<String> SCORING_DECLINED_STATUS = Arrays.asList("DECLINE", "POLICY_DECLINES");
    private static final String MEDIUM_RISK_RATING = "M";
    private static final String HIGH_RISK_RATING = "H";
    private static final String VERY_HIGH_RISK_RATING = "VH";
    private static final String UNFORTUNATE_ERROR = "Unfortunately you will not be able to apply for another product using our app.";

    static final String RETRY_TRANSACTION = "Please re-try transaction";
    public static final String TECHNICAL_ERROR = "The transaction could not complete due to a technical error.";

    private boolean hasTakenPhoto;
    private boolean scanIdDocumentRequired;
    private boolean scanAddressDocumentRequired;
    private int retryCounter = 0;
    private String lastToken;
    private String lastnVal;
    private String idNumber;
    private String clientTypeGroup;
    private IdDocumentType documentType;
    private WeakReference<NewToBankView> newToBankViewWeakReference;
    private NewToBankService newToBankService;
    private SharedService sharedService;
    private DocHandlerInteractor docHandlerInteractor;
    private DocHandlerDocument docHandlerIdCardDocumentBack;
    private CustomerDetails customerDetails;
    private ExtendedResponseListener<BusinessEvolveCardPackageResponse> businessEvolveStandardExtendedResponseListener = new ExtendedResponseListener<BusinessEvolveCardPackageResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(BusinessEvolveCardPackageResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                view.dismissProgressDialog();
                view.getNewToBankTempData().setBusinessEvolveStandardPackage(successResponse);
                view.navigateToAccountOffersFragment();
            }
        }
    };
    private ExtendedResponseListener<BusinessEvolveCardPackageResponse> businessEvolveIslamicExtendedResponseListener = new ExtendedResponseListener<BusinessEvolveCardPackageResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(BusinessEvolveCardPackageResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                view.getNewToBankTempData().setBusinessEvolveIslamicPackage(successResponse);
            }
        }
    };

    NewToBankPresenter(NewToBankService newToBankService, NewToBankView newToBankView) {
        this.newToBankService = newToBankService;
        newToBankViewWeakReference = new WeakReference<>(newToBankView);
        flexiBankingAccountExtendedResponseListener.setView(newToBankView);
        premiumBankingAccountExtendedResponseListener.setView(newToBankView);
        goldBankingAccountExtendedResponseListener.setView(newToBankView);
        flexiBankingAccountExtendedResponseListener.setView(newToBankView);
        businessEvolveIslamicExtendedResponseListener.setView(newToBankView);
        businessEvolveStandardExtendedResponseListener.setView(newToBankView);
        absaRewardsResponseExtendedResponseListener.setView(newToBankView);
        proofOfResidenceResponseExtendedResponseListener.setView(newToBankView);
        getAllCodesResponseListener.setView(newToBankView);
        getAllConfigsForApplicationResponseExtendedResponseListener.setView(newToBankView);
        validateCustomerAndCreateCaseResponseExtendedResponseListener.setView(newToBankView);
        photoMatchAndMobileLookUpResponseExtendedResponseListener.setView(newToBankView);
        getScoringStatusResponseExtendedResponseListener.setView(newToBankView);
        createCombiCardAccountResponseExtendedResponseListener.setView(newToBankView);
        keepAliveResponseExtendedResponseListener.setView(newToBankView);
        filteredSiteDetailsResponseExtendedResponseListener.setView(newToBankView);
        retrieveIdOcrDetailsFromDocumentResponseExtendedResponseListener.setView(newToBankView);
        registrationNewApplicationCustomerResponseExtendedResponseListener.setView(newToBankView);
        registerOnlineBankingPasswordResponseExtendedResponseListener.setView(newToBankView);
        expressPreLogonRequestSecurityNotificationResponseExtendedResponseListener.setView(newToBankView);
        expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener.setView(newToBankView);
        expressPreLogonResendSecurityNotificationResponseExtendedResponseListener.setView(newToBankView);
    }

    public NewToBankPresenter(NewToBankView newToBankView) {
        newToBankService = new NewToBankInteractor();
        docHandlerInteractor = new DocHandlerInteractor();
        sharedService = new SharedInteractor();
        newToBankViewWeakReference = new WeakReference<>(newToBankView);
        premiumBankingAccountExtendedResponseListener.setView(newToBankView);
        goldBankingAccountExtendedResponseListener.setView(newToBankView);
        flexiBankingAccountExtendedResponseListener.setView(newToBankView);
        businessEvolveIslamicExtendedResponseListener.setView(newToBankView);
        businessEvolveStandardExtendedResponseListener.setView(newToBankView);
        absaRewardsResponseExtendedResponseListener.setView(newToBankView);
        proofOfResidenceResponseExtendedResponseListener.setView(newToBankView);
        getAllCodesResponseListener.setView(newToBankView);
        getAllConfigsForApplicationResponseExtendedResponseListener.setView(newToBankView);
        validateCustomerAndCreateCaseResponseExtendedResponseListener.setView(newToBankView);
        photoMatchAndMobileLookUpResponseExtendedResponseListener.setView(newToBankView);
        getScoringStatusResponseExtendedResponseListener.setView(newToBankView);
        createCombiCardAccountResponseExtendedResponseListener.setView(newToBankView);
        keepAliveResponseExtendedResponseListener.setView(newToBankView);
        filteredSiteDetailsResponseExtendedResponseListener.setView(newToBankView);
        retrieveIdOcrDetailsFromDocumentResponseExtendedResponseListener.setView(newToBankView);
        registrationNewApplicationCustomerResponseExtendedResponseListener.setView(newToBankView);
        registerOnlineBankingPasswordResponseExtendedResponseListener.setView(newToBankView);
        expressPreLogonRequestSecurityNotificationResponseExtendedResponseListener.setView(newToBankView);
        expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener.setView(newToBankView);
        expressPreLogonResendSecurityNotificationResponseExtendedResponseListener.setView(newToBankView);
    }

    private ExtendedResponseListener<CardPackageResponse> premiumBankingAccountExtendedResponseListener = new ExtendedResponseListener<CardPackageResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(CardPackageResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                view.getNewToBankTempData().setPremiumPackage(successResponse.getCardPackage());
                fetchGoldBankingAccount();
            }
        }
    };

    private ExtendedResponseListener<CardPackageResponse> goldBankingAccountExtendedResponseListener = new ExtendedResponseListener<CardPackageResponse>() {
        private NewToBankView view;

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(CardPackageResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                view.getNewToBankTempData().setGoldPackage(successResponse.getCardPackage());
                fetchFlexiBankingAccount();
            }
        }
    };

    private ExtendedResponseListener<CardPackageResponse> flexiBankingAccountExtendedResponseListener = new ExtendedResponseListener<CardPackageResponse>() {
        private NewToBankView view;

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(CardPackageResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                view.getNewToBankTempData().setFlexiPackage(successResponse.getCardPackage());
                view.dismissProgressDialog();
                view.navigateToAccountOffersFragment();
            }
        }
    };

    private ExtendedResponseListener<AbsaRewardsResponse> absaRewardsResponseExtendedResponseListener = new ExtendedResponseListener<AbsaRewardsResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(AbsaRewardsResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (successResponse != null) {
                    view.getNewToBankTempData().setRewardsInfo(successResponse);
                    view.navigateToAbsaRewardsFragment();
                } else {
                    view.navigateToGenericResultFragment(false, false, TECHNICAL_ERROR, ResultAnimations.generalFailure);
                }
                view.dismissProgressDialog();
            }
        }
    };

    private ExtendedResponseListener<ProofOfResidenceResponse> proofOfResidenceResponseExtendedResponseListener = new ExtendedResponseListener<ProofOfResidenceResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(ProofOfResidenceResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToProofOfAddressFragment(successResponse);
            }
        }
    };

    private ExtendedResponseListener<GetCodesResponse> getAllCodesResponseListener = new ExtendedResponseListener<GetCodesResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(GetCodesResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (showFailure(view, successResponse)) return;

                SelectorList<CodesLookupDetailsSelector> sourceOfFundsList = new SelectorList<>();
                SelectorList<CodesLookupDetailsSelector> occupationCodeList = new SelectorList<>();
                SelectorList<CodesLookupDetailsSelector> employmentStatusList = new SelectorList<>();
                SelectorList<CodesLookupDetailsSelector> monthlyIncomeRangeList = new SelectorList<>();
                SelectorList<CodesLookupDetailsSelector> countryOfBirthList = new SelectorList<>();
                SelectorList<CodesLookupDetailsSelector> medicalOccupationList = new SelectorList<>();
                SelectorList<CodesLookupDetailsSelector> nationalityList = new SelectorList<>();
                SelectorList<CodesLookupDetailsSelector> residentialStatusList = new SelectorList<>();
                SelectorList<CodesLookupDetailsSelector> occupationLevelList = new SelectorList<>();
                SelectorList<CodesLookupDetailsSelector> employmentSectorList = new SelectorList<>();

                for (CodesLookupDetails codesLookupDetail : successResponse.getCodesLookupDetailsList()) {
                    CodesLookupDetailsSelector codesLookupDetailsSelector = new CodesLookupDetailsSelector(StringExtensions.toTitleCase(codesLookupDetail.getEngCodeDescription()), codesLookupDetail.getItemCode(), codesLookupDetail.getCodesLookupType());

                    switch (codesLookupDetail.getCodesLookupType()) {
                        case SOURCE_OF_FUNDS:
                            sourceOfFundsList.add(codesLookupDetailsSelector);
                            break;
                        case OCCUPATION_CODE:
                            occupationCodeList.add(codesLookupDetailsSelector);
                            break;
                        case EMPLOYMENT_STATUS:
                            if (!"10".equalsIgnoreCase(codesLookupDetail.getItemCode()) && !"04".equalsIgnoreCase(codesLookupDetail.getItemCode())) {
                                employmentStatusList.add(codesLookupDetailsSelector);
                            }
                            break;
                        case MONTHLY_INCOME_RANGE:
                            monthlyIncomeRangeList.add(codesLookupDetailsSelector);
                            break;
                        case NATIONALITY:
                            nationalityList.add(codesLookupDetailsSelector);
                            break;
                        case COUNTRY_OF_BIRTH:
                            countryOfBirthList.add(codesLookupDetailsSelector);
                            break;
                        case MEDICAL_OCCUPATION:
                            medicalOccupationList.add(codesLookupDetailsSelector);
                            break;
                        case RESIDENTIAL_STATUS:
                            residentialStatusList.add(codesLookupDetailsSelector);
                            break;
                        case OCCUPATION_LEVEL:
                            occupationLevelList.add(codesLookupDetailsSelector);
                            break;
                        case EMPLOYMENT_SECTOR:
                            employmentSectorList.add(codesLookupDetailsSelector);
                            break;
                    }
                }

                NewToBankTempData newToBankTempData = view.getNewToBankTempData();

                newToBankTempData.setNationalityList(nationalityList);
                newToBankTempData.setOccupationCodeList(occupationCodeList);
                newToBankTempData.setSourceOfFundsList(sourceOfFundsList);
                newToBankTempData.setEmploymentStatusList(employmentStatusList);
                newToBankTempData.setMonthlyIncomeRangeList(monthlyIncomeRangeList);
                newToBankTempData.setCountryOfBirthList(countryOfBirthList);
                newToBankTempData.setMedicalOccupationList(medicalOccupationList);
                newToBankTempData.setResidentialStatusList(residentialStatusList);
                newToBankTempData.setOccupationLevelList(occupationLevelList);
                newToBankTempData.setEmploymentSectorList(employmentSectorList);

                Collections.sort(sourceOfFundsList, (o1, o2) -> o1.getItemCode().compareTo(o2.getItemCode()));
                Collections.sort(nationalityList, (o1, o2) -> o1.getItemCode().compareTo(o2.getItemCode()));

                for (CodesLookupDetailsSelector codesLookupDetailsSelector : monthlyIncomeRangeList) {
                    codesLookupDetailsSelector.setEngCodeDescription(codesLookupDetailsSelector.getEngCodeDescription().replace("-", " to R"));
                }

                if (!(view.isBusinessFlow() || view.isStudentFlow())) {
                    view.navigateToChooseAccountFragment();
                }

                view.dismissProgressDialog();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    private ExtendedResponseListener<GetAllConfigsForApplicationResponse> getAllConfigsForApplicationResponseExtendedResponseListener = new ExtendedResponseListener<GetAllConfigsForApplicationResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(GetAllConfigsForApplicationResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (showFailure(view, successResponse)) return;

                view.dismissProgressDialog();

                List<ConfigValue> configValueList = successResponse.getListOfConfigValues();

                for (ConfigValue configValue : configValueList) {
                    if (REWARDS_CONFIG_KEY.equals(configValue.getConfigKey())) {
                        view.setRewardsAmount(configValue.getConfigValue());
                    } else if (REWARDS_CONFIG_DATE_KEY.equals(configValue.getConfigKey())) {
                        view.setRewardsDateDeadline(configValue.getConfigValue());
                    }
                }
            }
        }
    };

    private ExtendedResponseListener<ValidateCustomerAndCreateCaseResponse> validateCustomerAndCreateCaseResponseExtendedResponseListener = new ExtendedResponseListener<ValidateCustomerAndCreateCaseResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(ValidateCustomerAndCreateCaseResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (UNFORTUNATE_ERROR.equalsIgnoreCase(successResponse.getTransactionMessage())) {
                    view.dismissProgressDialog();
                    view.showExistingCustomerError();
                    return;
                } else if (RETRY_TRANSACTION.equalsIgnoreCase(successResponse.getTransactionMessage()) && retryCounter < 2) {
                    retryCounter++;
                    view.dismissProgressDialog();
                    performValidateCustomerAndCreateCase(customerDetails);
                    return;
                }

                retryCounter = 0;
                if (showFailure(view, successResponse, true, true)) {
                    view.trackCurrentFragment(NewToBankConstants.IDENTITY_SCREEN_ID_GOLDENSOURCE_FAILURE);
                    return;
                }

                scanIdDocumentRequired = successResponse.getScanIdDocumentRequired();
                fetchDocumentByCaseId(successResponse.getBcmsCaseNumber());
                view.getNewToBankTempData().setAbsaRewardsExist(successResponse.getAbsaRewardsExist());
            }
        }
    };

    private ExtendedResponseListener<PhotoMatchAndMobileLookUpResponse> photoMatchAndMobileLookUpResponseExtendedResponseListener = new ExtendedResponseListener<PhotoMatchAndMobileLookUpResponse>() {
        private NewToBankView view;

        @Override
        public void onRequestStarted() {
        }

        @Override
        public void onSuccess(PhotoMatchAndMobileLookUpResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (RETRY_TRANSACTION.equalsIgnoreCase(successResponse.getTransactionMessage()) && retryCounter < 2) {
                retryCounter++;
                performPhotoMatchAndMobileLookup();
                return;
            }
            retryCounter = 0;

            if (showFailure(view, successResponse)) return;

            if (view != null) {
                PerformPhotoMatchAndMobileLookupDTO performPhotoMatchAndMobileLookupDTO = successResponse.getPerformPhotoMatchAndMobileLookupDTO();

                if (performPhotoMatchAndMobileLookupDTO == null) {
                    view.navigateToGenericResultFragment(false, false, TECHNICAL_ERROR, ResultAnimations.generalFailure);
                    return;
                }

                Boolean idScanRequired = performPhotoMatchAndMobileLookupDTO.getIdScanRequired();
                view.dismissProgressDialog();

                if (idScanRequired == null) {
                    view.showMessageError(AppConstants.GENERIC_ERROR_MSG);
                    view.navigateToWelcomeActivity();
                } else if (idScanRequired) {
                    view.navigateToSAIDBookOrCardFragment();
                } else {
                    if (performPhotoMatchAndMobileLookupDTO.getNameAndSurname() != null) {
                        String fullName = StringExtensions.toTitleCaseRemovingCommas(performPhotoMatchAndMobileLookupDTO.getNameAndSurname());
                        view.getNewToBankTempData().getCustomerDetails().setFullName(fullName);
                    }

                    view.navigateToConfirmIdentityFragment(performPhotoMatchAndMobileLookupDTO);
                }
            }
        }

        @Override

        public void onPolling() {
            performPhotoMatchAndMobileLookup();
        }
    };

    private boolean showFailure(NewToBankView view, TransactionResponse successResponse) {
        return showFailure(view, successResponse, true);
    }

    private boolean showFailure(NewToBankView view, TransactionResponse successResponse, boolean shouldNavigate) {
        return showFailure(view, successResponse, shouldNavigate, false);
    }

    private boolean showFailure(NewToBankView view, ExpressPreLogonRequestSecurityNotificationResponse successResponse) {
        successResponse.getResult();
        if (FAILED.equalsIgnoreCase(successResponse.getResult())) {
            view.dismissProgressDialog();
            view.navigateToGenericResultFragment(false, false, successResponse.getTransactionMessage(), ResultAnimations.generalFailure);
            return true;
        }
        return false;
    }

    private boolean showFailure(NewToBankView view, TransactionResponse successResponse, boolean shouldNavigate, boolean retainState) {
        if (successResponse.getTransactionStatus() != null &&
                (FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus())
                        || BMBConstants.REJECTED.equalsIgnoreCase(successResponse.getTransactionStatus())
                        || FAILED.equalsIgnoreCase(successResponse.getTransactionStatus()))) {

            view.dismissProgressDialog();

            if (shouldNavigate) {
                view.navigateToGenericResultFragment(retainState, false, successResponse.getTransactionMessage(), ResultAnimations.generalFailure);
            }

            return true;
        }
        return false;
    }

    private ExtendedResponseListener<GetScoringStatusResponse> getScoringStatusResponseExtendedResponseListener = new ExtendedResponseListener<GetScoringStatusResponse>() {
        private NewToBankView view;

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(GetScoringStatusResponse successResponse) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                NewToBankTempData newToBankTempData = view.getNewToBankTempData();

                if (!successResponse.getScoringStatus().isEmpty()) {
                    if (successResponse.getAccountNumber().isEmpty()) {
                        view.navigateToScoringFailure(true);
                    } else {
                        newToBankTempData.getRegistrationDetails().setAccountNumber(successResponse.getAccountNumber());

                        if (SCORING_REFERRAL_STATUS.contains(successResponse.getScoringStatus().toUpperCase())) {
                            view.navigateToThankYouForYourApplicationScreen();
                        } else if (SCORING_DECLINED_STATUS.contains(successResponse.getScoringStatus().toUpperCase())) {
                            view.navigateToScoringFailure(newToBankTempData.getInBranchInfo().getInBranchIndicator());
                        } else if (SCORING_SUCCESS_STATUS.contains(successResponse.getScoringStatus().toUpperCase())) {
                            view.navigateToNewToBankWelcomeToAbsaFragment(newToBankTempData.getAddressDetails().getAddressChanged());
                        } else if (showFailure(view, successResponse, false)) {
                            if (newToBankTempData.getInBranchInfo().getInBranchIndicator()) {
                                view.navigateToScoringFailure(true);
                            } else {
                                view.navigateToScoringFailure(false);
                            }
                        } else if (showFailure(view, successResponse)) {
                        } else if (view.isBusinessFlow() && !successResponse.getAccountNumber().isEmpty()) {
                            view.navigateToNewToBankWelcomeToAbsaFragment(newToBankTempData.getAddressDetails().getAddressChanged());
                        }
                    }
                }
            }
        }

        @Override
        public void onPolling() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> performGetScoringStatus(), 1000);
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            view = newToBankViewWeakReference.get();
            if (view != null) {
                NewToBankTempData newToBankTempData = view.getNewToBankTempData();
                if (newToBankTempData != null && (newToBankTempData.getInBranchInfo().getInBranchIndicator())) {
                    view.navigateToScoringFailure(true);
                } else {
                    view.navigateToScoringFailure(false);
                }
            }
        }
    };

    private ExtendedResponseListener<CreateCombiCardAccountResponse> createCombiCardAccountResponseExtendedResponseListener = new ExtendedResponseListener<CreateCombiCardAccountResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(CreateCombiCardAccountResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (showFailure(view, successResponse, false)) {
                    view.navigateToCardOrderedSuccessFailure();
                    return;
                }

                view.dismissProgressDialog();

                if (!TextUtils.isEmpty(successResponse.getCardNumber())) {
                    view.navigateToCardOrderedSuccessScreen();
                } else {
                    view.showStandardCardScreen();
                }
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            super.onFailure(failureResponse);
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.navigateToCardOrderedSuccessFailure();
                view.dismissProgressDialog();
            }
        }
    };

    private ExtendedResponseListener<NewToBankKeepAliveResponse> keepAliveResponseExtendedResponseListener = new ExtendedResponseListener<NewToBankKeepAliveResponse>() {
        private NewToBankView view;

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(NewToBankKeepAliveResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                view.resetKeepAliveTimer();
            }
        }
    };

    private ExtendedResponseListener<GetFilteredSiteDetailsResponse> filteredSiteDetailsResponseExtendedResponseListener = new ExtendedResponseListener<GetFilteredSiteDetailsResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(GetFilteredSiteDetailsResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (successResponse != null) {
                    view.setListOfBranches(successResponse.getSiteFilteredDetailsDTOList());

                    if (!TextUtils.isEmpty(view.getNewToBankTempData().getRegistrationDetails().getAccountNumber())) {
                        view.navigateToSelectPreferredBranchFragment();
                    }
                }
                view.dismissProgressDialog();
            }
        }

        @Override
        public void onFailure(ResponseObject failureResponse) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.navigateToGenericResultFragment(true, false, ResponseObject.extractErrorMessage(failureResponse), ResultAnimations.generalFailure);
                view.dismissProgressDialog();
            }
        }
    };

    private ExtendedResponseListener<RetrieveIdOcrDetailsFromDocumentResponse> retrieveIdOcrDetailsFromDocumentResponseExtendedResponseListener = new ExtendedResponseListener<RetrieveIdOcrDetailsFromDocumentResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(RetrieveIdOcrDetailsFromDocumentResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (RETRY_TRANSACTION.equalsIgnoreCase(successResponse.getTransactionMessage()) && retryCounter < 2) {
                    retryCounter++;
                    view.dismissProgressDialog();
                    performRetrieveIdOcrDetailsFromDocument(documentType, idNumber);
                    return;
                }
                retryCounter = 0;

                if (showFailure(view, successResponse)) {
                    view.trackFragmentAction(NewToBankConstants.CONFIRM_IDENTITY_SCREEN, NewToBankConstants.ID_TIM_PLATFORM_DOWN);
                    return;
                }

                if (successResponse.getRetrieveIDOCRDetails().getPhotoMatchRequired() && hasTakenPhoto) {
                    performPhotoMatchAndMobileLookup();
                } else if (successResponse.getRetrieveIDOCRDetails().getPhotoMatchRequired()) {
                    view.dismissProgressDialog();
                    view.navigateToTakeMyPhotosFragment();
                }
            }
        }

        @Override
        public void onPolling() {
            performRetrieveIdOcrDetailsFromDocument(documentType, idNumber);
        }
    };

    private ExtendedResponseListener<CreateCustomerPortfolioAccountResponse> createCustomerPortfolioAccountResponseExtendedResponseListener = new ExtendedResponseListener<CreateCustomerPortfolioAccountResponse>() {
        private NewToBankView view;

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(CreateCustomerPortfolioAccountResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (showFailure(view, successResponse)) return;

                performGetScoringStatus();
            }
        }
    };

    private ExtendedResponseListener<PerformCasaRiskProfilingResponse> performCasaRiskProfilingResponseExtendedResponseListener = new ExtendedResponseListener<PerformCasaRiskProfilingResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(PerformCasaRiskProfilingResponse successResponse) {
            view = newToBankViewWeakReference.get();
            NewToBankTempData newToBankTempData = view.getNewToBankTempData();
            if (view != null) {
                if (successResponse != null) {
                    if (FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus()) && successResponse.getRiskRating() != null && (HIGH_RISK_RATING.equalsIgnoreCase(successResponse.getRiskRating()) || VERY_HIGH_RISK_RATING.equalsIgnoreCase(successResponse.getRiskRating()))) {
                        view.navigateToCasaFailureScreen(newToBankTempData.getInBranchInfo().getInBranchIndicator());
                    } else if (successResponse.getProofOfResidenceScanRequired() != null && successResponse.getProofOfResidenceScanRequired() && SUCCESS.equalsIgnoreCase(successResponse.getTransactionStatus()) && MEDIUM_RISK_RATING.equalsIgnoreCase(successResponse.getRiskRating())) {
                        scanAddressDocumentRequired = true;
                        fetchProofOfResidenceInfo();
                    } else if (FAILURE.equalsIgnoreCase(successResponse.getTransactionStatus()) && successResponse.getRiskRating().isEmpty()) {
                        view.navigateToGenericResultFragment(false, false, AppConstants.GENERIC_ERROR_MSG, ResultAnimations.generalFailure);
                    } else {
                        if (view.isBusinessFlow()) {
                            view.navigateToBusinessBankingSummaryFragment();
                        } else {
                            view.navigateToApplicationSummary();
                        }
                    }
                } else {
                    view.navigateToGenericResultFragment(false, false, AppConstants.GENERIC_ERROR_MSG, ResultAnimations.generalFailure);
                }
                view.dismissProgressDialog();
            }
        }
    };

    private ExtendedResponseListener<RegistrationNewApplicationCustomerResponse> registrationNewApplicationCustomerResponseExtendedResponseListener = new ExtendedResponseListener<RegistrationNewApplicationCustomerResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(RegistrationNewApplicationCustomerResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (showFailure(view, successResponse)) return;

                lastnVal = successResponse.getNonce();
                lastToken = successResponse.getTokenNumber();

                view.dismissProgressDialog();
                view.setPINSuccessful();
            }
        }
    };

    private ExtendedResponseListener<RegisterOnlineBankingPasswordResponse> registerOnlineBankingPasswordResponseExtendedResponseListener = new ExtendedResponseListener<RegisterOnlineBankingPasswordResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(RegisterOnlineBankingPasswordResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (showFailure(view, successResponse)) return;

                view.dismissProgressDialog();
                view.setPasswordSuccessful();
            }
        }
    };

    private ExtendedResponseListener<ExpressPreLogonRequestSecurityNotificationResponse> expressPreLogonRequestSecurityNotificationResponseExtendedResponseListener = new ExtendedResponseListener<ExpressPreLogonRequestSecurityNotificationResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(ExpressPreLogonRequestSecurityNotificationResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                if (showFailure(view, successResponse)) return;

                view.dismissProgressDialog();

                if (successResponse.getSecurityNotificationType().equalsIgnoreCase("OTP") && "processing".equalsIgnoreCase(successResponse.getResult())) {
                    view.navigateToNewToBankTVNFragment();
                } else if (successResponse.getResult() != null && "processing".equalsIgnoreCase(successResponse.getResult())) {
                    view.navigateToNewToBankSurecheckFragment();
                    validateSecurityNotification(successResponse.getCellNumber());
                }
            }
        }
    };

    private ExtendedResponseListener<ExpressPreLogonValidateSecurityNotificationResponse> expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener = new ExtendedResponseListener<ExpressPreLogonValidateSecurityNotificationResponse>() {
        private NewToBankView view;

        @Override
        public void onRequestStarted() {

        }

        @Override
        public void onSuccess(ExpressPreLogonValidateSecurityNotificationResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {

                if (BMBConstants.REJECTED.equalsIgnoreCase(successResponse.getResult()) || FAILED.equalsIgnoreCase(successResponse.getResult())) {
                    view.dismissProgressDialog();
                    if (view.isBusinessFlow()) {
                        view.navigateToSureCheckRejectScreen();
                    } else {
                        view.showSureCheckResend();
                    }
                    return;
                }

                if ("revertback".equalsIgnoreCase(successResponse.getResult())) {
                    view.dismissProgressDialog();
                    view.navigateToNewToBankTVNFragment();
                } else if ("processing".equalsIgnoreCase(successResponse.getResult())) {
                    validateSecurityNotification(successResponse.getCellNumber());
                } else if ("processed".equalsIgnoreCase(successResponse.getResult())) {
                    if (scanIdDocumentRequired) {
                        view.navigateToSAIDBookOrCardFragment();
                    } else {
                        view.navigateToTakeMyPhotosFragment();
                    }
                    view.dismissProgressDialog();
                } else {
                    view.dismissProgressDialog();
                }
            }
        }
    };

    private ExtendedResponseListener<ExpressPreLogonResendSecurityNotificationResponse> expressPreLogonResendSecurityNotificationResponseExtendedResponseListener = new ExtendedResponseListener<ExpressPreLogonResendSecurityNotificationResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(ExpressPreLogonResendSecurityNotificationResponse successResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                view.dismissProgressDialog();

                if (showFailure(view, successResponse)) return;

                if ("0".equals(successResponse.getResendsRemaining())) {
                    view.navigateToGenericResultFragment(false, false, AppConstants.GENERIC_ERROR_MSG, ResultAnimations.generalFailure);
                } else {
                    validateSecurityNotification(view.getNewToBankTempData().getCustomerDetails().getCellphoneNumber());
                }
            }
        }
    };

    private DocHandlerGetCaseResponseListener docHandlerGetCaseResponseListener = new DocHandlerGetCaseResponseListener() {
        private NewToBankView view;

        @Override
        public void onGetCaseSuccess(DocHandlerGetCaseByIdResponse docHandlerGetCaseByIdResponse) {
            view = newToBankViewWeakReference.get();

            if (view != null) {
                view.setDocHandlerResponse(docHandlerGetCaseByIdResponse);

                view.navigateToNumberConfirmationFragment();
                view.dismissProgressDialog();
            }
        }

        @Override
        public void onGetCaseFailure(String failure) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToGenericResultFragment(true, false, failure, ResultAnimations.generalFailure);
            }
        }
    };

    private DocHandlerUploadResponseListener docHandlerSelfieUploadResponseListener = new DocHandlerUploadResponseListener() {
        private NewToBankView view;

        @Override
        public void onUploadSuccess(DocHandlerUploadDocumentResponse docHandlerUploadDocumentResponse) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                hasTakenPhoto = true;
                performPhotoMatchAndMobileLookup();
            }
        }

        @Override
        public void onUploadFailure(String failure) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToGenericResultFragment(true, false, failure, ResultAnimations.generalFailure);
            }
        }
    };

    private DocHandlerUploadResponseListener docHandlerIdBookUploadResponseListener = new DocHandlerUploadResponseListener() {
        private NewToBankView view;

        @Override
        public void onUploadSuccess(DocHandlerUploadDocumentResponse docHandlerUploadDocumentResponse) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                performRetrieveIdOcrDetailsFromDocument(IdDocumentType.ID_DOCUMENT, idNumber);
            }
        }

        @Override
        public void onUploadFailure(String failure) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToGenericResultFragment(true, false, failure, ResultAnimations.generalFailure);
            }
        }
    };

    private DocHandlerUploadResponseListener docHandlerIdCardFrontUploadResponseListener = new DocHandlerUploadResponseListener() {
        private NewToBankView view;

        @Override
        public void onUploadSuccess(DocHandlerUploadDocumentResponse docHandlerUploadDocumentResponse) {
            docHandlerInteractor.submitDocument(docHandlerIdCardDocumentBack, docHandlerIdCardBackUploadResponseListener);
        }

        @Override
        public void onUploadFailure(String failure) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToGenericResultFragment(true, false, failure, ResultAnimations.generalFailure);
            }
        }
    };

    private DocHandlerUploadResponseListener docHandlerIdCardBackUploadResponseListener = new DocHandlerUploadResponseListener() {
        private NewToBankView view;

        @Override
        public void onUploadSuccess(DocHandlerUploadDocumentResponse docHandlerUploadDocumentResponse) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                performRetrieveIdOcrDetailsFromDocument(IdDocumentType.ID_CARD, idNumber);
            }
        }

        @Override
        public void onUploadFailure(String failure) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.navigateToGenericResultFragment(true, false, failure, ResultAnimations.generalFailure);
            }
        }
    };

    private DocHandlerUploadResponseListener docHandlerProofOfResidenceUploadResponseListener = new DocHandlerUploadResponseListener() {
        private NewToBankView view;

        @Override
        public void onUploadSuccess(DocHandlerUploadDocumentResponse docHandlerUploadDocumentResponse) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                if (view.isBusinessFlow()) {
                    if (scanAddressDocumentRequired) {
                        view.navigateToBusinessBankingSummaryFragment();
                    } else {
                        view.navigateToDocumentsUploadSuccessfully();
                    }
                } else {
                    view.navigateToApplicationSummary();
                }
            }
        }

        @Override
        public void onUploadFailure(String failure) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                view.dismissProgressDialog();
                view.trackFragmentAction(NewToBankConstants.CONFIRM_ADDRESS_CASA_FAIL, NewToBankConstants.PROOF_OF_RESIDENCE_DOCHANDLER_FAILURE);
                view.navigateToGenericResultFragment(true, false, failure, ResultAnimations.generalFailure);
            }
        }
    };

    private ExtendedResponseListener<GetCodesResponse> sourceOfIncomeExtendedResponseListener = new ExtendedResponseListener<GetCodesResponse>() {
        private NewToBankView view;

        @Override
        public void onSuccess(GetCodesResponse successResponse) {
            view = newToBankViewWeakReference.get();
            if (view != null) {
                SelectorList<CodesLookupDetailsSelector> sourceOfIncome = new SelectorList<>();

                for (CodesLookupDetails codesLookupDetail : successResponse.getCodesLookupDetailsList()) {
                    CodesLookupDetailsSelector codesLookupDetailsSelector = new CodesLookupDetailsSelector(StringExtensions.toTitleCase(codesLookupDetail.getEngCodeDescription()), codesLookupDetail.getItemCode(), codesLookupDetail.getCodesLookupType());
                    if (SOURCE_OF_FUNDS.equalsIgnoreCase(codesLookupDetail.getCodesLookupType())) {
                        sourceOfIncome.add(codesLookupDetailsSelector);
                    }
                }

                NewToBankTempData newToBankTempData = view.getNewToBankTempData();
                newToBankTempData.setSourceOfIncomeList(sourceOfIncome);
                if (view.isBusinessFlow() || view.isStudentFlow()) {
                    fetchCifCodes();
                } else {
                    view.navigateToIncomeAndExpensesFragment();
                }
                view.dismissProgressDialog();
            }
        }
    };

    void fetchCardBundles() {
        newToBankService.fetchPremiumBankingAccount(premiumBankingAccountExtendedResponseListener);
    }

    private void fetchGoldBankingAccount() {
        newToBankService.fetchGoldBankingAccount(goldBankingAccountExtendedResponseListener);
    }

    private void fetchFlexiBankingAccount() {
        newToBankService.fetchFlexiBankingAccount(flexiBankingAccountExtendedResponseListener);
    }

    void fetchBusinessEvolveIslamicAccount() {
        newToBankService.fetchBusinessEvolveIslamicAccount(businessEvolveIslamicExtendedResponseListener);
    }

    void fetchBusinessEvolveStandardAccount() {
        newToBankService.fetchBusinessEvolveStandardAccount(businessEvolveStandardExtendedResponseListener);
    }

    void fetchAbsaRewards(boolean isFree) {
        newToBankService.fetchAbsaRewards(isFree, absaRewardsResponseExtendedResponseListener);
    }

    void fetchProofOfResidenceInfo() {
        newToBankService.fetchProofOfResidenceInfo(proofOfResidenceResponseExtendedResponseListener);
    }

    void fetchCifCodes() {
        sharedService.fetchCIFCodes(SourceOfFundsLookUpType.SOURCE_OF_FUNDS, CIFCodeLookUpType.ALL, getAllCodesResponseListener);
    }

    void performGetAllConfigsForApplication() {
        newToBankService.performGetAllConfigsForApplication(getAllConfigsForApplicationResponseExtendedResponseListener);
    }

    void performValidateCustomerAndCreateCase(CustomerDetails customerDetails) {
        this.customerDetails = customerDetails;
        scanIdDocumentRequired = false;
        newToBankService.performValidateCustomerAndCreateCase(customerDetails, validateCustomerAndCreateCaseResponseExtendedResponseListener);
    }

    private void performPhotoMatchAndMobileLookup() {
        newToBankService.performPhotoMatchAndMobileLookup(photoMatchAndMobileLookUpResponseExtendedResponseListener);
    }

    private void performGetScoringStatus() {
        newToBankService.performGetScoringStatus(getScoringStatusResponseExtendedResponseListener);
    }

    void performGetFilteredSiteDetails(String searchValue) {
        newToBankService.performGetFilteredSiteDetails(searchValue, filteredSiteDetailsResponseExtendedResponseListener);
    }

    void performCreateCombiCardAccount(CreateCombiDetails createCombiDetails) {
        lastnVal = createCombiDetails.getTokenNumber();
        newToBankService.performCreateCombiCardAccount(createCombiDetails, createCombiCardAccountResponseExtendedResponseListener);
    }

    private void performRetrieveIdOcrDetailsFromDocument(IdDocumentType documentType, String idNumber) {
        newToBankService.performRetrieveIdOcrDetailsFromDocument(documentType, idNumber, retrieveIdOcrDetailsFromDocumentResponseExtendedResponseListener);
    }

    void performKeepAlive() {
        newToBankService.performKeepAlive(keepAliveResponseExtendedResponseListener);
    }

    void requestSecurityNotification(String cellNumber) {
        newToBankService.performExpressPreLogonRequestSecurityNotificationNewToBank(cellNumber, expressPreLogonRequestSecurityNotificationResponseExtendedResponseListener);
    }

    private void validateSecurityNotification(String cellNumber) {
        newToBankService.performExpressPreLogonValidateSecurityNotificationNewToBank(cellNumber, expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener);
    }

    void validateSecurityNotification(String cellNumber, String TVN) {
        newToBankService.performExpressPreLogonValidateSecurityNotificationNewToBank(cellNumber, TVN, expressPreLogonValidateSecurityNotificationResponseExtendedResponseListener);
    }

    void resendSecurityNotification(String cellNumber) {
        newToBankService.performExpressPreLogonResendSecurityNotificationNewToBank(cellNumber, expressPreLogonResendSecurityNotificationResponseExtendedResponseListener);
    }

    void createCustomerPortfolio(CustomerPortfolioInfo customerPortfolioInfo) {
        newToBankService.performCreateCustomerPortfolio(customerPortfolioInfo, createCustomerPortfolioAccountResponseExtendedResponseListener);
    }

    void performCasaRiskProfiling(CustomerPortfolioInfo customerPortfolioInfo) {
        newToBankService.performCasaRiskProfiling(customerPortfolioInfo, performCasaRiskProfilingResponseExtendedResponseListener);
    }

    private void fetchDocumentByCaseId(String bcmsCaseNumber) {
        docHandlerInteractor.getDocumentByCaseId(bcmsCaseNumber, docHandlerGetCaseResponseListener);
    }

    void uploadSelfieToDocHandler(DocHandlerDocument docHandlerDocument) {
        docHandlerInteractor.submitDocument(docHandlerDocument, docHandlerSelfieUploadResponseListener);
    }

    void uploadIdBookToDocHandler(DocHandlerDocument docHandlerDocument, String idNumber) {
        this.documentType = IdDocumentType.ID_DOCUMENT;
        this.idNumber = idNumber;
        docHandlerInteractor.submitDocument(docHandlerDocument, docHandlerIdBookUploadResponseListener);
    }

    void uploadIdCardToDocHandler(DocHandlerDocument docHandlerIdCardDocumentFront, DocHandlerDocument docHandlerIdCardDocumentBack, String idNumber) {
        this.documentType = IdDocumentType.ID_CARD;
        this.idNumber = idNumber;
        this.docHandlerIdCardDocumentBack = docHandlerIdCardDocumentBack;
        docHandlerInteractor.submitDocument(docHandlerIdCardDocumentFront, docHandlerIdCardFrontUploadResponseListener);
    }

    void uploadProofOfResidenceToDocHandler(DocHandlerDocument docHandlerDocument) {
        docHandlerInteractor.submitDocument(docHandlerDocument, docHandlerProofOfResidenceUploadResponseListener);
    }

    void setOnlineBankingPIN(String idNumber, String PIN, String clientTypeGroup) {
        this.clientTypeGroup = clientTypeGroup;
        newToBankService.performRegistrationNewApplicationCustomer(idNumber, PIN, lastnVal, clientTypeGroup, registrationNewApplicationCustomerResponseExtendedResponseListener);
    }

    void setOnlineBankingPassword(String password) {
        newToBankService.performRegisterOnlineBankingPassword(password, lastToken, lastnVal, clientTypeGroup, registerOnlineBankingPasswordResponseExtendedResponseListener);
    }

    public void setNewToBankService(NewToBankService newToBankService) {
        this.newToBankService = newToBankService;
    }

    void onRewardsNextButtonClick() {
        sharedService.fetchCIFCodes(SourceOfFundsLookUpType.SOURCE_OF_INCOME, CIFCodeLookUpType.SOURCE_OF_FUNDS, sourceOfIncomeExtendedResponseListener);
    }

    void fetchSourceOfIncomeList() {
        sharedService.fetchCIFCodes(SourceOfFundsLookUpType.SOURCE_OF_INCOME, CIFCodeLookUpType.ALL, sourceOfIncomeExtendedResponseListener);
    }

}
