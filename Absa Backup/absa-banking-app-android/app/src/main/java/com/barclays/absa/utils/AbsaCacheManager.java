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
package com.barclays.absa.utils;

import com.barclays.absa.banking.beneficiaries.services.IBeneficiaryCacheService;
import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.boundary.model.AccountList;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryListObject;
import com.barclays.absa.banking.boundary.model.BeneficiaryObject;
import com.barclays.absa.banking.boundary.model.InterAccountTransferInitObject;
import com.barclays.absa.banking.boundary.model.OnceOffPaymentObject;
import com.barclays.absa.banking.boundary.model.PayBeneficiaryPaymentObject;
import com.barclays.absa.banking.boundary.model.SecureHomePageObject;
import com.barclays.absa.banking.boundary.model.rewards.RewardsAccountDetails;
import com.barclays.absa.banking.boundary.model.rewards.RewardsDetails;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.banking.framework.dagger.DaggerHelperKt;
import com.barclays.absa.banking.framework.data.ApplicationFlowType;
import com.barclays.absa.banking.framework.data.ResponseObject;
import com.barclays.absa.banking.payments.international.services.dto.WesternUnionBeneficiaryListObject;

import java.util.ArrayList;
import java.util.List;

public class AbsaCacheManager {
    private boolean isPaymentListCacheUpdated = true;
    private boolean isAirtimeListCacheUpdated = true;
    private boolean isCashSendListCacheUpdated = true;
    private boolean isWesternUnionListCacheUpdated = true;
    private boolean isBeneficiaryCachingAllowed = true;
    private boolean isAccountsCacheUpdated = true;

    private static AbsaCacheManager instance;
    private static AccountList accountList;
    private static BeneficiaryListObject beneficiaryListObject;
    private static WesternUnionBeneficiaryListObject westernUnionBeneficiaryListObject;
    private static BeneficiaryListObject favBeneficiaryListObject;
    private IBeneficiaryCacheService beneficiaryCacheService = DaggerHelperKt.getServiceInterface(IBeneficiaryCacheService.class);

    private AbsaCacheManager() {
    }

    public static AbsaCacheManager getInstance() {
        if (instance == null) {
            instance = new AbsaCacheManager();
            accountList = new AccountList();
            beneficiaryListObject = new BeneficiaryListObject();
            westernUnionBeneficiaryListObject = new WesternUnionBeneficiaryListObject();
            favBeneficiaryListObject = new BeneficiaryListObject();
        }

        return instance;
    }

    //-----------------------------END OF NORMAL BEN CACHING CODE ------------------------------------------------------------//
    public boolean isBeneficiariesCached(String beneficiaryType) {
        if (isBeneficiaryCachingAllowed && beneficiaryListObject != null && beneficiaryType != null) {
            switch (beneficiaryType) {
                case BMBConstants.PASS_PAYMENT:
                    if (beneficiaryCacheService.getPaymentBeneficiaries().size() > 0 && isPaymentListCacheUpdated) {
                        return true;
                    }
                    break;

                case BMBConstants.PASS_AIRTIME:
                case BMBConstants.PASS_PREPAID:
                    if (beneficiaryCacheService.getPrepaidBeneficiaries().size() > 0 && isAirtimeListCacheUpdated)
                        return true;

                    break;

                case BMBConstants.PASS_CASHSEND:
                    if (beneficiaryCacheService.getCashSendBeneficiaries().size() > 0 && isCashSendListCacheUpdated) {
                        return true;
                    }
                    break;
            }
        }
        return false;
    }

    public void setBeneficiaryCacheStatus(boolean isBeneficiaryCacheFlag, String beneficiaryType) {
        switch (beneficiaryType) {
            case BMBConstants.PASS_PAYMENT:
                isPaymentListCacheUpdated = isBeneficiaryCacheFlag;
                // Cache outdated. Clear list and parser will fill this.
                if (!isPaymentListCacheUpdated) {
                    beneficiaryListObject.setPaymentBeneficiaryList(new ArrayList<>());
                }
                break;

            case BMBConstants.PASS_AIRTIME:
            case BMBConstants.PASS_PREPAID:
                isAirtimeListCacheUpdated = isBeneficiaryCacheFlag;
                if (!isAirtimeListCacheUpdated) {
                    beneficiaryListObject.setAirtimeBeneficiaryList(new ArrayList<>());
                }
                break;

            case BMBConstants.PASS_CASHSEND:
                isCashSendListCacheUpdated = isBeneficiaryCacheFlag;
                if (!isCashSendListCacheUpdated) {
                    beneficiaryListObject.setCashsendBeneficiaryList(new ArrayList<>());
                }
                break;
            case BMBConstants.PASS_WESTERN_UNION:
                isWesternUnionListCacheUpdated = isBeneficiaryCacheFlag;
                if (!isWesternUnionListCacheUpdated) {
                    westernUnionBeneficiaryListObject.setWesternUnionBeneficiaryList(null);
                }
                break;

            case "":
                // Note : Empty type indicates cache outdated for all types
                isAirtimeListCacheUpdated = isCashSendListCacheUpdated = isPaymentListCacheUpdated = isBeneficiaryCacheFlag;
                // This will clear all the list
                beneficiaryListObject.setPaymentBeneficiaryList(new ArrayList<>());
                beneficiaryListObject.setAirtimeBeneficiaryList(new ArrayList<>());
                beneficiaryListObject.setCashsendBeneficiaryList(new ArrayList<>());
                westernUnionBeneficiaryListObject.setWesternUnionBeneficiaryList(null);
                break;
        }
    }

    public AccountList getAccountsList() {
        return accountList;
    }

    public void clearCache() {

        // Clear Accounts
        accountList = new AccountList();
        isAccountsCacheUpdated = false;

        // Clear Beneficiaries
        beneficiaryListObject = new BeneficiaryListObject();
        westernUnionBeneficiaryListObject = new WesternUnionBeneficiaryListObject();
        isPaymentListCacheUpdated = false;
        isAirtimeListCacheUpdated = false;
        isCashSendListCacheUpdated = false;
        isWesternUnionListCacheUpdated = false;
        //isBeneficiaryCachingAllowed = false;

        //Clear Fav beneficiaries
        favBeneficiaryListObject = new BeneficiaryListObject();
    }

    public void updateBeneficiaryList(String type, ArrayList<BeneficiaryObject> arrList) {
        if (isBeneficiaryCachingAllowed) {
            @SuppressWarnings("unchecked")
            ArrayList<BeneficiaryObject> benObjList = (ArrayList<BeneficiaryObject>) arrList.clone();
            if (beneficiaryListObject != null) {
                switch (type) {
                    case BMBConstants.PASS_PAYMENT:
                        beneficiaryListObject.setPaymentBeneficiaryList(benObjList);
                        break;
                    case BMBConstants.PASS_CASHSEND:
                        beneficiaryListObject.setCashsendBeneficiaryList(benObjList);
                        break;
                    case BMBConstants.PASS_AIRTIME:
                    case BMBConstants.PASS_PREPAID:
                        beneficiaryListObject.setAirtimeBeneficiaryList(benObjList);
                        break;
                }
            }
        }
    }

    public BeneficiaryListObject getCachedBeneficiaryListObject() {
        return AbsaCacheManager.beneficiaryListObject;
    }

    public boolean isBeneficiaryCachingAllowed() {
        return isBeneficiaryCachingAllowed;
    }

    //-----------------------------END OF NORMAL BEN CACHING CODE ------------------------------------------------------------//


    //-----------------------------FAVORITE BEN CACHING CODE ------------------------------------------------------------//
    public void updateCachedFavBeneficiaryList(BeneficiaryListObject object) {
        AbsaCacheManager.favBeneficiaryListObject = object;
    }

    //-----------------------------ACCOUNTS CACHING CODE ------------------------------------------------------------//
    public AccountList getCachedAccountListObject() {
        return accountList;
    }

    public boolean isAccountsCached() {
        if (accountList == null) return false;
        accountList.getAccountsList();
        return accountList.getAccountsList().size() > 0 && isAccountsCacheUpdated;
    }

    public void appendAccountList(ArrayList<AccountObject> acctList) {
        appendAccountList(acctList, 0);
    }

    public void appendToEndAccountList(ArrayList<AccountObject> acctList) {
        accountList.getAccountsList().addAll(acctList);
        isAccountsCacheUpdated = true;
    }

    public void appendAccountList(ArrayList<AccountObject> acctList, int index) {
        accountList.getAccountsList().addAll(index, acctList);
        isAccountsCacheUpdated = true;
    }

    public void removeAccountFromList(AccountObject accountObject) {
        accountList.getAccountsList().remove(accountObject);
        isAccountsCacheUpdated = true;
    }

    public void updateAccountList(ArrayList<AccountObject> acctList) {
        // Fetching only account list.
        // I dont bother about from & to account list, As my cache manager will take care of this.
        accountList.setAccountsList(acctList);
        isAccountsCacheUpdated = true;
    }

    public void updateBalClearDate(String date) {
        accountList.setBalNotYetClrdDt(date);
    }

    public ArrayList<AccountObject> filterFromAccountList(String requestType) {
        if (requestType != null && requestType.trim().length() > 0) {
            return FilterAccountList.filterCustAccountsForOperatorAccess(accountList.getAccountsList(), requestType, AccessPrivileges.getInstance().isOperator());
        } else {
            return FilterAccountList.filterAccountList(FilterAccountList.ACCOUNT_DEBIT, accountList.getAccountsList());
        }
    }

    public ArrayList<AccountObject> filterFromAccountList(ArrayList<AccountObject> list, String requestType) {
        if (requestType != null && requestType.trim().length() > 0) {
            return FilterAccountList.filterCustAccountsForOperatorAccess(list, requestType, AccessPrivileges.getInstance().isOperator());
        } else {
            return FilterAccountList.filterAccountList(FilterAccountList.ACCOUNT_DEBIT, list);
        }
    }

    public ArrayList<AccountObject> filterToAccountList() {
        return FilterAccountList.filterAccountList(FilterAccountList.ACCOUNT_CREDIT, accountList.getAccountsList());
    }

    public ArrayList<AccountObject> filterToAccountList(ArrayList<AccountObject> list) {
        return FilterAccountList.filterAccountList(FilterAccountList.ACCOUNT_CREDIT, list);
    }

    public void getModelForAccounts(ResponseObject object, ApplicationFlowType flowType) {
        if (object instanceof InterAccountTransferInitObject) {
            ((InterAccountTransferInitObject) object).setFromAccounts(filterFromAccountList(""));
            ((InterAccountTransferInitObject) object).setToAccounts(filterToAccountList());
        } else if (object instanceof SecureHomePageObject) {

            ((SecureHomePageObject) object).setAccounts(accountList.getAccountsList());
            ((SecureHomePageObject) object).setFromAccounts(filterFromAccountList(""));
            ((SecureHomePageObject) object).setToAccounts(filterToAccountList());

            ((SecureHomePageObject) object).setPaymentBeneficiaries(favBeneficiaryListObject.getPaymentBeneficiaryList());
            ((SecureHomePageObject) object).setCashsendBeneficiaries(favBeneficiaryListObject.getCashsendBeneficiaryList());
            ((SecureHomePageObject) object).setAirtimeBeneficiaries(favBeneficiaryListObject.getAirtimeBeneficiaryList());

        } else if (object instanceof AccountList) {
            ((AccountList) object).setAccountsList(accountList.getAccountsList());
            ((AccountList) object).setFromAccountList(filterFromAccountList(""));
            ((AccountList) object).setToAccountList(filterToAccountList());
            ((AccountList) object).setBalNotYetClrdDt(accountList.getBalNotYetClrdDt());
        } else if (object instanceof OnceOffPaymentObject) {
            ((OnceOffPaymentObject) object).setFromAccounts(filterFromAccountList(FilterAccountList.REQ_TYPE_PAYMENT));
        } else if (object instanceof PayBeneficiaryPaymentObject) {
            if (flowType.equals(ApplicationFlowType.PREPAID_ELECTRICITY)) {
                ((PayBeneficiaryPaymentObject) object).setFromAccounts(getTransactionalAccounts());
            } else if (flowType.equals(ApplicationFlowType.CASH_SEND_REBUILD)) {
                ((PayBeneficiaryPaymentObject) object).setFromAccounts(filterFromAccountList(FilterAccountList.REQ_TYPE_CASHSEND));
            } else {
                ((PayBeneficiaryPaymentObject) object).setFromAccounts(filterFromAccountList(FilterAccountList.REQ_TYPE_PAYMENT));
            }
        }
        // This is mandatory,
        object.setFlowType(flowType);
    }

    public void setAccountsCacheStatus(boolean isAccountCacheFlag) {
        isAccountsCacheUpdated = isAccountCacheFlag;

        // If False - clear cache
        if (!isAccountsCacheUpdated) {
            accountList = new AccountList();
        }
    }

    //-----------------------------END OF ACCOUNTS CACHING CODE ------------------------------------------------------------//

    public static ArrayList<AccountObject> getTransactionalAccounts() {
        if (accountList == null) {
            return null;
        }
        return FilterAccountList.getTransactionalAccounts(accountList.getAccountsList());
    }

    public static ArrayList<AccountObject> getCiaAccounts() {
        if (accountList == null) {
            return new ArrayList<>();
        }
        return FilterAccountList.getCiaAccounts(accountList.getAccountsList());
    }

    private List<AccountObject> getRewardsAccountsFromCache(List<RewardsAccountDetails> rewardsAccountDetailsList) {
        List<AccountObject> rewardsAccountList = new ArrayList<>();
        if (AbsaCacheManager.getInstance().isAccountsCached()) {
            List<AccountObject> accountsFromCache = accountList.getAccountsList();
            for (int i = 0; i < accountsFromCache.size(); i++) {
                for (RewardsAccountDetails rewardsAccountDetails : rewardsAccountDetailsList) {
                    if (rewardsAccountDetails != null && rewardsAccountDetails.getAccountNumber() != null && rewardsAccountDetails.getAccountNumber().equalsIgnoreCase(accountsFromCache.get(i).getAccountNumber())) {
                        AccountObject accountObject = new AccountObject();
                        accountObject.setAccountNumber(accountsFromCache.get(i).getAccountNumber());
                        accountObject.setDescription(accountsFromCache.get(i).getDescription());
                        accountObject.setAvailableBalance(accountsFromCache.get(i).getAvailableBalance());
                        accountObject.setCurrentBalance(accountsFromCache.get(i).getAvailableBalance());
                        rewardsAccountList.add(accountObject);
                    }
                }
            }
        }
        return rewardsAccountList;
    }

    public AccountObject getRewardAccount(RewardsDetails rewardsDetails) {
        List<RewardsAccountDetails> rewardsAccountDetailsList = rewardsDetails.getAccountList();
        List<AccountObject> rewardsAccounts = getRewardsAccountsFromCache(rewardsAccountDetailsList);
        for (AccountObject accountObject : rewardsAccounts) {
            if (accountObject.getAccountNumber().equals(rewardsDetails.getFromAccount())) {
                return accountObject;
            }
        }
        return null;
    }
}