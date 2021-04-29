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

import com.barclays.absa.banking.boundary.model.AccessPrivileges;
import com.barclays.absa.banking.boundary.model.AccountObject;
import com.barclays.absa.banking.boundary.monitoring.MonitoringInteractor;
import com.barclays.absa.banking.framework.app.BMBApplication;
import com.barclays.absa.banking.framework.app.BMBConstants;
import com.barclays.absa.crypto.SymmetricCryptoHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.barclays.absa.crypto.SymmetricCryptoHelper.NEW_RELIC_SECURITY_KEY;

public class FilterAccountList {

    private static final int ACCOUNT_ALL_AVAILABLE_ACCOUNTS = -1;
    private static final int ACCOUNT_ALL_ACCOUNTS = 0;
    static final int ACCOUNT_DEBIT = 1;
    static final int ACCOUNT_CREDIT = 2;
    private static final int ACCOUNT_BALANCE = 4;
    private static final int ACCOUNT_STATEMENT = 8;
    private static final int ACCOUNT_ACCOUNT_INFO = 16;
    private static final int ACCOUNT_BILL_PAYMENT = 32;
    private static final int ACCOUNT_OWN_DEFINED = 64;
    private static final int ACCOUNT_PAYMENT = 96; //?
    private static final int ACCOUNT_RECURRING_PAYMENT = 128;
    private static final int ACCOUNT_STOP_CHEQUE_PAYMENT = 256;
    private static final int ACCOUNT_PREPAID = 512;
    private static final int ACCOUNT_OVERDRAFT = 1024;
    private static final int ACCOUNT_ARCHIVED_STATEMENT = 2048;
    private static final int ACCOUNT_ATM_CARDLESS = 4096; //this is CashSend
    private static final int ACCOUNT_STOP_DEBIT_ORDER = 8192;
    private static final int ACCOUNT_ELECTRONIC_STATEMENT_DELIVERY = 16384;
    private static final int ACCOUNT_UPDATE_CIF = 32768;

    private static String crashAccountNumber = "";

    //Strings from Express side mapping to AOL access bits
    public static Map<String, Integer> accountActionsAndAccessBitsMapping = new HashMap<>();
    //Mapping of account bits to account type access bits
    public static Map<Integer, Integer> accountAccessBitsToAccountTypeBitsMapping = new HashMap<>();

    static {
        accountActionsAndAccessBitsMapping.put("DR", ACCOUNT_DEBIT);
        accountActionsAndAccessBitsMapping.put("CR", ACCOUNT_CREDIT);
        accountActionsAndAccessBitsMapping.put("BAL", ACCOUNT_BALANCE);
        accountActionsAndAccessBitsMapping.put("STMT", ACCOUNT_STATEMENT);
        accountActionsAndAccessBitsMapping.put("ACC_INF", ACCOUNT_ACCOUNT_INFO);
        accountActionsAndAccessBitsMapping.put("BILL_PMT", ACCOUNT_BILL_PAYMENT);
        accountActionsAndAccessBitsMapping.put("OWN_DEF", ACCOUNT_OWN_DEFINED);
        accountActionsAndAccessBitsMapping.put("PMT", ACCOUNT_PAYMENT);
        accountActionsAndAccessBitsMapping.put("RECUR_PMT", ACCOUNT_RECURRING_PAYMENT);
        accountActionsAndAccessBitsMapping.put("STOP_PMT", ACCOUNT_STOP_CHEQUE_PAYMENT);
        accountActionsAndAccessBitsMapping.put("PREPD", ACCOUNT_PREPAID);
        accountActionsAndAccessBitsMapping.put("OVRDRFT", ACCOUNT_OVERDRAFT);
        accountActionsAndAccessBitsMapping.put("ARCH_STMT", ACCOUNT_ARCHIVED_STATEMENT);
        accountActionsAndAccessBitsMapping.put("CSHSND", ACCOUNT_ATM_CARDLESS);
        accountActionsAndAccessBitsMapping.put("STOP_DO", ACCOUNT_STOP_DEBIT_ORDER);
        accountActionsAndAccessBitsMapping.put("ELEC_STMT", ACCOUNT_ELECTRONIC_STATEMENT_DELIVERY);
        accountActionsAndAccessBitsMapping.put("UPD_CIF", ACCOUNT_UPDATE_CIF);
    }

    //Request Types: cashSend, prepaid, payment/normal
    public static final String REQ_TYPE_PAYMENT = "payment";
    public static final String REQ_TYPE_CASHSEND = "cashSend";
    public static final String REQ_TYPE_PREPAID = "prepaid";
    private static final String REQ_TYPE_INTERACC_TRFR = "interacctrfr";

    //Access and account bits for cashSend, prepaid, payment
    private static final int CASHSEND_ACCT_TYPE_BIT = 3;
    private static final int CASHSEND_ACCT_BIT = 4096;
    private static final int PREPAID_ACCT_TYPE_BIT = 0;
    private static final int PREPAID_ACCT_BIT = 512;
    private static final int PAYMENT_ACCT_TYPE_BIT = 0;
    private static final int PAYMENT_ACCT_BIT = 96;

    private static final Map<String, Integer> accessLevelMap = new TreeMap<>();

    static {
        accessLevelMap.put("ACCOUNT_ALL_AVAILABLE_ACCOUNTS", ACCOUNT_ALL_AVAILABLE_ACCOUNTS);
        accessLevelMap.put("ACCOUNT_ALL_ACCOUNTS", ACCOUNT_ALL_ACCOUNTS);
        accessLevelMap.put("ACCOUNT_DEBIT", ACCOUNT_DEBIT);
        accessLevelMap.put("ACCOUNT_CREDIT", ACCOUNT_CREDIT);
        accessLevelMap.put("ACCOUNT_BALANCE", ACCOUNT_BALANCE);
        accessLevelMap.put("ACCOUNT_STATEMENT", ACCOUNT_STATEMENT);
        accessLevelMap.put("ACCOUNT_ACCOUNT_INFO", ACCOUNT_ACCOUNT_INFO);
        accessLevelMap.put("ACCOUNT_BILL_PAYMENT", ACCOUNT_BILL_PAYMENT);
        accessLevelMap.put("ACCOUNT_OWN_DEFINED", ACCOUNT_OWN_DEFINED);
        accessLevelMap.put("ACCOUNT_PAYMENT", ACCOUNT_PAYMENT);
        accessLevelMap.put("ACCOUNT_RECURRING_PAYMENT", ACCOUNT_RECURRING_PAYMENT);
        accessLevelMap.put("ACCOUNT_STOP_CHEQUE_PAYMENT", ACCOUNT_STOP_CHEQUE_PAYMENT);
        accessLevelMap.put("ACCOUNT_PREPAID", ACCOUNT_PREPAID);
        accessLevelMap.put("ACCOUNT_OVERDRAFT", ACCOUNT_OVERDRAFT);
        accessLevelMap.put("ACCOUNT_ARCHIVED_STATEMENT", ACCOUNT_ARCHIVED_STATEMENT);
        accessLevelMap.put("ACCOUNT_ATM_CARDLESS", ACCOUNT_ATM_CARDLESS);
        accessLevelMap.put("ACCOUNT_STOP_DEBIT_ORDER", ACCOUNT_STOP_DEBIT_ORDER);
        accessLevelMap.put("ACCOUNT_ELECTRONIC_STATEMENT_DELIVERY", ACCOUNT_ELECTRONIC_STATEMENT_DELIVERY);
        accessLevelMap.put("ACCOUNT_UPDATE_CIF", ACCOUNT_UPDATE_CIF);
        accessLevelMap.put("PAYMENT_TRAFFIC_FINE", 3);

        // User access bits
        accessLevelMap.put("USER_ALL", 0);
        accessLevelMap.put("USER_CIF_UPDATE", 1);
        accessLevelMap.put("USER_BDB_PAYROLL", 2);
        accessLevelMap.put("USER_ABSA_REWARDS", 4);
        accessLevelMap.put("USER_CASH_SEND_PLUS", 8);
    }

    static {
        accountAccessBitsToAccountTypeBitsMapping.put(CASHSEND_ACCT_BIT, CASHSEND_ACCT_TYPE_BIT);
        accountAccessBitsToAccountTypeBitsMapping.put(PREPAID_ACCT_BIT, PREPAID_ACCT_TYPE_BIT);
        accountAccessBitsToAccountTypeBitsMapping.put(PAYMENT_ACCT_BIT, PAYMENT_ACCT_TYPE_BIT);
    }

    /**
     * This method is useful for paybeneficiary, cashSend and airtime operations for step 1 of
     * onceoff and normal payments.
     *
     * @param list        is a list
     * @param requestType is a request type
     * @param isOperator  is a is operator
     * @return as List
     */
    static ArrayList<AccountObject> filterCustAccountsForOperatorAccess(ArrayList<AccountObject> list, String requestType, boolean isOperator) {
        ArrayList<AccountObject> filteredAccounts = new ArrayList<>();

        if (null != list && list.size() > 0) {
            String func = "ACCOUNT_PAYMENT";
            int accessBit = PAYMENT_ACCT_BIT;
            int acTypeBit = PAYMENT_ACCT_TYPE_BIT;

            if (REQ_TYPE_CASHSEND.equalsIgnoreCase(requestType)) {
                func = "ACCOUNT_ATM_CARDLESS";
                accessBit = CASHSEND_ACCT_BIT;
                acTypeBit = CASHSEND_ACCT_TYPE_BIT;
            } else if (REQ_TYPE_PREPAID.equalsIgnoreCase(requestType)) {
                func = "ACCOUNT_PREPAID";
                accessBit = PREPAID_ACCT_BIT;
                acTypeBit = PREPAID_ACCT_TYPE_BIT;
            } else if (REQ_TYPE_INTERACC_TRFR.equalsIgnoreCase(requestType)) {
                func = "ACCOUNT_DEBIT";
            }

            //Looping to check for isAllowed and balance check
            {
                for (int i = 0; i < list.size(); i++) {
                    AccountObject account = list.get(i);
                    if (account != null) {
                        crashAccountNumber = account.getAccountNumber();
                        String accountAccessBit = String.valueOf(accessBitToInt(account.getAccessBits()));
                        if (!isOperator) {
                            filteredAccounts.add(account);
                        } else if (isAllowed(func, accountAccessBit)) {
                            filteredAccounts.add(account);
                        }
                    } else {
                        String location = BMBApplication.getInstance().getTopMostActivity().getClass().getSimpleName();
                        new MonitoringInteractor().logTechnicalEvent(location, "", "Account is Null - Request Type: " + requestType);
                    }
                }
            }

            //Looping to check for filter based on individual accessBits and Accounttypebits
            if (filteredAccounts.size() > 0 && !REQ_TYPE_INTERACC_TRFR.equalsIgnoreCase(requestType)) {
                filteredAccounts = filterCustomerAccountList(filteredAccounts, accessBit, acTypeBit);
            }
        }
        return filteredAccounts;
    }

    public static int accessBitToInt(String str) {
        try {
            return Double.valueOf(str).intValue();
        } catch (Exception e) {
            String location = BMBApplication.getInstance().getTopMostActivity().getClass().getSimpleName();
            try {
                String encryptedAccountNumber = SymmetricCryptoHelper.getInstance().encryptString(crashAccountNumber, NEW_RELIC_SECURITY_KEY);
                new MonitoringInteractor().logTechnicalEvent(location, "", "Account " + encryptedAccountNumber + " is Null or: " + str);
            } catch (Exception ignored) {
            }
            return -2;
        }
    }

    /**
     * Checks boolean condition in method Allowed.
     *
     * @param function   is a function
     * @param accessBits is a access bits
     * @return true, if is allowed
     */
    private static boolean isAllowed(String function, String accessBits) {
        int accessLevel = accessBitToInt(accessBits);
        if (accessLevel == 0) {
            return true;
        } else if (accessBits.equals("-2")) {
            return false;
        } else {
            Integer functionLevel = accessLevelMap.get(function);
            if (functionLevel == null) {
                throw new RuntimeException("Function [" + function
                        + "] is not mapped ");
            } else {
                // bitwise compare below
                // For Example: Function is 2048
                // AccessBits is 64255
                // So doing bitwise comparison will result in :
                // 00000000000000000000100000000000 & (2048)
                // 00000000000000001111101011111111 = (64255)
                // 00000000000000000000100000000000
                // Which means true will be returned
                return (functionLevel & accessLevel) > 0;
            }
        }
    }

    /**
     * Filter customer account list.
     *
     * @param list           is a list
     * @param accessBits     is a access bits
     * @param typeAccessBits is a type access bits
     * @return as List
     */
    private static ArrayList<AccountObject> filterCustomerAccountList(
            ArrayList<AccountObject> list, int accessBits,
            int typeAccessBits) {
        ArrayList<AccountObject> filteredAccounts;

        if (typeAccessBits == ACCOUNT_ALL_ACCOUNTS) {
            filteredAccounts = filterAccountList(accessBits, list);
        } else {
            filteredAccounts = filterAccountListByAccountType(accessBits,
                    typeAccessBits, list);
        }

        return filteredAccounts;
    }

    /**
     * Filter account list by account type.
     *
     * @param accountBit           is a account bit
     * @param totalAccountTypeBits is a total account type bits
     * @param accountList          is a account list
     * @return as List
     */
    private static ArrayList<AccountObject> filterAccountListByAccountType(int accountBit,
                                                                           int totalAccountTypeBits,
                                                                           ArrayList<AccountObject> accountList) {
        ArrayList<AccountObject> filteredAccounts = new ArrayList<>();

        if (accountList != null && accountList.size() > 0) {
            for (int i = 0; i < accountList.size(); i++) {
                AccountObject accountDTO = accountList
                        .get(i);
                if (accountBit == ACCOUNT_PAYMENT) {
                    if (((accessBitToInt(accountDTO.getAccessBits()) & ACCOUNT_BILL_PAYMENT) == ACCOUNT_BILL_PAYMENT)
                            || ((accessBitToInt(accountDTO.getAccessBits()) & ACCOUNT_OWN_DEFINED) == ACCOUNT_OWN_DEFINED)
                            && (accessBitToInt(accountDTO.getAccessTypeBit()) & totalAccountTypeBits) != 0) {
                        filteredAccounts.add(accountDTO);
                    }
                } else if ((accessBitToInt(accountDTO.getAccessBits()) & accountBit) == accountBit
                        && (accessBitToInt(accountDTO.getAccessTypeBit()) & totalAccountTypeBits) != 0) {
                    filteredAccounts.add(accountDTO);
                }
            }
        }
        return filteredAccounts;
    }

    /**
     * Filter account list.
     *
     * @param accountListType is a account list type
     * @param accountList     is a account list
     * @return as List
     */
    static ArrayList<AccountObject> filterAccountList(int accountListType,
                                                      ArrayList<AccountObject> accountList) {
        // Get the accounts from the total list which conforms with the list
        // type (0 return all accounts)
        ArrayList<AccountObject> filteredAccounts = new ArrayList<>();
        if (accountList != null && accountList.size() > 0) {
            AccountObject accountDTO;
            if (accountListType == ACCOUNT_ALL_ACCOUNTS) {
                for (int i = 0; i < accountList.size(); i++) {
                    accountDTO = accountList.get(i);

                    if (accessBitToInt(accountDTO.getAccessBits()) != ACCOUNT_ALL_ACCOUNTS) {
                        filteredAccounts.add(accountDTO);
                    }
                }
            } else if (accountListType == ACCOUNT_ALL_AVAILABLE_ACCOUNTS) {
                for (int i = 0; i < accountList.size(); i++) {
                    accountDTO = accountList.get(i);

                    if (accessBitToInt(accountDTO.getAccessBits()) != ACCOUNT_ALL_ACCOUNTS
                            && accessBitToInt(accountDTO.getAccessBits()) != ACCOUNT_CREDIT) {
                        filteredAccounts.add(accountDTO);
                    }
                }
            } else if (accountListType == ACCOUNT_PAYMENT) {
                for (int i = 0; i < accountList.size(); i++) {
                    accountDTO = accountList.get(i);

                    if (((accessBitToInt(accountDTO.getAccessBits()) & ACCOUNT_BILL_PAYMENT) == ACCOUNT_BILL_PAYMENT)
                            || ((accessBitToInt(accountDTO.getAccessBits()) & ACCOUNT_OWN_DEFINED) == ACCOUNT_OWN_DEFINED)) {
                        filteredAccounts.add(accountDTO);
                    }
                }
            } else {
                for (int i = 0; i < accountList.size(); i++) {
                    accountDTO = accountList.get(i);
                    if ((accessBitToInt(accountDTO.getAccessBits()) & accountListType) == accountListType) {
                        filteredAccounts.add(accountDTO);
                    }
                }
            }
        }
        return filteredAccounts;
    }

    public static boolean isStampedStatementsAllowed(AccountObject accountDTO) {
        return isAccountTransactional(accountDTO) && !AccessPrivileges.getInstance().isOperator();
    }

    public static boolean isArchivedStatementsAllowed(AccountObject accountDTO) {
        if (accountDTO != null) {
            return (accessBitToInt(accountDTO.getAccessBits()) & ACCOUNT_ARCHIVED_STATEMENT) == ACCOUNT_ARCHIVED_STATEMENT;
        }
        return false;
    }

    public static ArrayList<AccountObject> getTransactionalAccounts(ArrayList<AccountObject> data) {
        ArrayList<AccountObject> transactionalAccounts = new ArrayList<>();

        if (data == null) {
            return null;
        }

        for (AccountObject accountObject : data) {
            if (isAccountTransactional(accountObject)) {
                transactionalAccounts.add(accountObject);
            }
        }
        return transactionalAccounts;
    }

    private static boolean isAccountTransactional(AccountObject accountObject) {
        if (accountObject != null) {
            return BMBConstants.AccountTypeEnum.savingsAccount.toString().equalsIgnoreCase(accountObject.getAccountType().trim()) ||
                    BMBConstants.AccountTypeEnum.currentAccount.toString().equalsIgnoreCase(accountObject.getAccountType().trim()) ||
                    BMBConstants.AccountTypeEnum.chequeAccount.toString().equalsIgnoreCase(accountObject.getAccountType().trim()) ||
                    BMBConstants.AccountTypeEnum.debitAccount.toString().equalsIgnoreCase(accountObject.getAccountType().trim());
        }
        return false;
    }

    public static ArrayList<AccountObject> getCiaAccounts(ArrayList<AccountObject> data) {
        ArrayList<AccountObject> ciaAccounts = new ArrayList<>();

        if (data == null) {
            return new ArrayList<>();
        }

        for (AccountObject accountObject : data) {
            if (BMBConstants.AccountTypeEnum.cia.toString().equalsIgnoreCase(accountObject.getAccountType().trim())) {
                ciaAccounts.add(accountObject);
            }
        }
        return ciaAccounts;
    }

    public static ArrayList<AccountObject> getTransactionalAndCreditAccounts(List<AccountObject> data) {
        ArrayList<AccountObject> accountArrayList = new ArrayList<>();

        if (data == null) {
            return null;
        }

        for (AccountObject retailAccount : data) {
            if (BMBConstants.AccountTypeEnum.savingsAccount.toString().equalsIgnoreCase(retailAccount.getAccountType().trim()) ||
                    BMBConstants.AccountTypeEnum.currentAccount.toString().equalsIgnoreCase(retailAccount.getAccountType().trim()) ||
                    BMBConstants.AccountTypeEnum.creditCard.toString().equalsIgnoreCase(retailAccount.getAccountType().trim()) ||
                    BMBConstants.AccountTypeEnum.chequeAccount.toString().equalsIgnoreCase(retailAccount.getAccountType().trim()) ||
                    BMBConstants.AccountTypeEnum.debitAccount.toString().equalsIgnoreCase(retailAccount.getAccountType().trim())) {
                accountArrayList.add(retailAccount);
            }
        }
        sortAccountsAccordingToType(accountArrayList);
        return accountArrayList;
    }

    public static ArrayList<AccountObject> getFlexiFuneralAccounts(List<AccountObject> data) {
        ArrayList<AccountObject> accountArrayList = new ArrayList<>();

        if (data == null) {
            return null;
        }

        for (AccountObject retailAccount : data) {
            if (BMBConstants.AccountTypeEnum.savingsAccount.toString().equalsIgnoreCase(retailAccount.getAccountType().trim()) ||
                    BMBConstants.AccountTypeEnum.currentAccount.toString().equalsIgnoreCase(retailAccount.getAccountType().trim()) ||
                    BMBConstants.AccountTypeEnum.chequeAccount.toString().equalsIgnoreCase(retailAccount.getAccountType().trim())) {
                accountArrayList.add(retailAccount);
            }
        }
        sortAccountsAccordingToType(accountArrayList);
        return accountArrayList;
    }

    public static ArrayList<AccountObject> getCashSendAccounts(List<AccountObject> accountObjectList) {
        ArrayList<AccountObject> accountArrayList = new ArrayList<>();

        if (accountObjectList == null) {
            return null;
        }

        for (AccountObject accountObject : accountObjectList) {
            if ((accessBitToInt(accountObject.getAccessBits()) & CASHSEND_ACCT_BIT) == CASHSEND_ACCT_BIT) {
                accountArrayList.add(accountObject);
            }
        }
        sortAccountsAccordingToType(accountArrayList);
        return accountArrayList;
    }

    private static void sortAccountsAccordingToType(ArrayList<AccountObject> accountArrayList) {
        Collections.sort(accountArrayList, (o1, o2) -> {
            if (o1.getAccountType().equalsIgnoreCase(o2.getAccountType())) {
                return 0;
            }
            if (o1.getAccountType().equalsIgnoreCase(BMBConstants.AccountTypeEnum.currentAccount.toString())) {
                return -1;
            }
            if (o2.getAccountType().equalsIgnoreCase(BMBConstants.AccountTypeEnum.currentAccount.toString())) {
                return 1;
            }
            return o1.getAccountType().compareTo(o2.getAccountType());
        });
    }
}
