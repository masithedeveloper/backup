/*
 * Copyright (c) 2020 Absa Bank Limited, All Rights Reserved.
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
package com.barclays.absa.banking.virtualpayments.scan2Pay.services.factory

import com.entersekt.scan2pay.*

object ScanToPayMockFactory {

    fun getMockAuthMetaData(): String = """{"meta":{"flow_id":"04a81b59-3a41-4e7c-9107-eab8cb90a474","amount":5.45,"currency_code":"ZAR","cards":[{"index":1,"card_number":"403822******4030","card_type":"PULL.VISA","product_name":"VISA","accepted_by_merchant":true},{"index":2,"card_number":"448385******2757","card_type":"PULL.VISA_ELECTRON","product_name":"VISA","accepted_by_merchant":true},{"index":3,"card_number":"455027******0024","card_type":"PUSH.VISA_DIRECT","product_name":"VISA"},{"index":4,"card_number":"448385******1065","card_type":"PUSH.VISA_DIRECT","product_name":"VISA"},{"index":5,"card_number":"455027******8011","card_type":"PUSH.VISA_DIRECT","product_name":"VISA"},{"index":6,"card_number":"478769******1016","card_type":"PUSH.VISA_DIRECT","product_name":"VISA"},{"index":7,"card_number":"478769******6027","card_type":"PUSH.VISA_DIRECT","product_name":"VISA"}]},"display":[{"name":"merchant_name","value":"Scan to Pay"}]}"""

    fun getPullPaymentByUniqueCode(uniqueCode: String): PullPayment = when (uniqueCode) {
        // 1. Dynamic QR (fixed amount as R5.45) - masterpass
        "7654116538" -> object : PullPayment {
            override val amount: Amount = Amount.Fixed(5.45)
            override val currencyCode: String = "ZAR"
            override val description: String = "Please complete payment"
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 2. Static QR (amount must be entered and sent) - masterpass
        "9210592955" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton
            override val currencyCode: String = "ZAR"
            override val description: String = "Please complete payment"
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 3. Dynamic QR (amount editable) - masterpass
        "6012187472" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.PartialPayment(6.48)
            override val currencyCode: String = "ZAR"
            override val description: String = "Please pay..."
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 4. Static QR with Tip required - masterpass
        "8613356146" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton
            override val currencyCode: String = "ZAR"
            override val description: String = "Complete payment"
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.InputRequired(0.0)
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 5. Dynamic QR with Tip required - masterpass
        "9601244684" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.PartialPayment(10.85)
            override val currencyCode: String = "ZAR"
            override val description: String = "Please pay"
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.InputRequired(0.0)
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 6. Static QR with extra reference required - masterpass
        "1357998132" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton
            override val currencyCode: String = "ZAR"
            override val description: String = "S2P Testing"
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = true
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 7. Zapper – StdKeySerializers.Dynamic QR with tip (partial payment allowed, amount is R10.00
        // END_SYS_ERROR
        // Transaction could not be completed due to a system error, please try again later.

        // 8. Zapper – Static QR with tip
        // END_SYS_ERROR
        // Transaction could not be completed due to a system error, please try again later.

        // 9. SnapScan – Dynamic QR (amount – R2.56)
        """https://pos-staging.snapscan.io/qr/wggznD9t""" -> object : PullPayment {
            override val amount: Amount = Amount.Fixed(2.56)
            override val currencyCode: String = "ZAR"
            override val description: String = "Oltio Staging Amount Editable"
            override val merchantName: String = "SnapScan - Oltio Staging Amount Editable"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 10. SnapScan – Static QR with Tip
        """https://pos-staging.snapscan.io/qr/OtZ-SVTS""" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton                    // com.entersekt.scan2pay.Amount$InputRequired$InputRequiredInstance@d8a6477
            override val currencyCode: String = "ZAR"
            override val description: String = "Oltio Staging Tippable"
            override val merchantName: String = "SnapScan - Oltio Staging Tippable"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.InputRequired(0.00)
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 11. Masterpass ecommerce Test site
        "5756685239" -> object : PullPayment {
            override val amount: Amount = Amount.Fixed(50.0)
            override val currencyCode: String = "ZAR"
            override val description: String = "Charity Donation"
            override val merchantName: String = "Masterpass Charity"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 12. SBSA merchant – Dynamic QR (amount is R3.75) - masterpass
        "8656849931" -> object : PullPayment {
            override val amount: Amount = Amount.Fixed(3.75)
            override val currencyCode: String = "ZAR"
            override val description: String = "Please Pay This"
            override val merchantName: String = "SBSA Test Store"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        //13. Nedbank merchant – Static QR - masterpass
        "6546589595" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton       // com.entersekt.scan2pay.Amount$InputRequired$InputRequiredInstance@30cee3a
            override val currencyCode: String = "ZAR"
            override val description: String = "Please Pay This"
            override val merchantName: String = "Nedbank Test Store"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 14. Absa merchant – Dynamic QR (amount is R5.75) - masterpass
        "6400594798" -> object : PullPayment {
            override val amount: Amount = Amount.Fixed(5.75)
            override val currencyCode: String = "ZAR"
            override val description: String = "Please Pay This"
            override val merchantName: String = "Absa Test Store"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }


        // 15. Capitec merchant – Static QR - masterpass
        "9632567392" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton            // com.entersekt.scan2pay.Amount$InputRequired$InputRequiredInstance@1aaab8c
            override val currencyCode: String = "ZAR"
            override val description: String = "Please Pay This"
            override val merchantName: String = "Capitec Test Store"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 16. FNB merchant – Static QR
        "0005UMPQR0102115204451153037105802ZA5902MS6012JOHANNESBURG62720119STATIC_1808281042360520200426180828104236600706200426150121606000425" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton            // com.entersekt.scan2pay.Amount$InputRequired$InputRequiredInstance@1aaab8c
            override val currencyCode: String = "ZAR"
            override val description: String = "Transaction Details"
            override val merchantName: String = "FNB (UMPQR) - MS"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 17. Masterpass – “Offline” QR with dynamic amount set at R12.34
        // 18. Masterpass – EMVQR with Masterpass QR
        "00020126260008za.co.mp01104203211774" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton            // com.entersekt.scan2pay.Amount$InputRequired$InputRequiredInstance@1aaab8c
            override val currencyCode: String = "ZAR"
            override val description: String = "Test Variable QR"
            override val merchantName: String = "ATestAccount"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // 19. Reversal State Test. (Long merchant response, then reversed)
        "2695118334" -> object : PullPayment {
            override val amount: Amount = Amount.Fixed(10.0)
            override val currencyCode: String = "ZAR"
            override val description: String = "Transaction Details"
            override val merchantName: String = "MP Long Poll Reverse"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // Random QR code that takes 25 seconds
        "0954922658" -> object : PullPayment {
            override val amount: Amount = Amount.Fixed(10.0)
            override val currencyCode: String = "ZAR"
            override val description: String = "Transaction Details"
            override val merchantName: String = "MP long Poll 200"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        // Personal test examples
        //*******************************************************************************************************************
        "123" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.PartialPayment(100.00)
            override val currencyCode: String = "ZAR"
            override val description: String = "Please complete payment"
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.Fixed(100.00)
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        "1234" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton
            override val currencyCode: String = "ZAR"
            override val description: String = "Please complete payment"
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.Fixed(100.00)
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        "12345" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.PartialPayment(100.00)
            override val currencyCode: String = "ZAR"
            override val description: String = "Please complete payment"
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.InputRequired(0.2)
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        "123456" -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.singleton
            override val currencyCode: String = "ZAR"
            override val description: String = "Please complete payment"
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.InputRequired(0.3)
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }

        //*******************************************************************************************************************

        else -> object : PullPayment {
            override val amount: Amount = Amount.InputRequired.PartialPayment(6.48)
            override val currencyCode: String = "ZAR"
            override val description: String = "Please pay..."
            override val merchantName: String = "Scan to Pay"
            override val payerInputRequired: Boolean = false
            override val payerReferenceRequired: Boolean = false
            override val pinRequired: Boolean = false
            override val sourceOfFunds: List<SourceOfFunds> = emptyList()
            override val tip: Tip = Tip.None
            override val title: String = "Payment Confirmation"
            override val interactionID: String = ""
            override fun authorize(authorization: Authorization) {}
            override fun reject() {}
        }
    }
}