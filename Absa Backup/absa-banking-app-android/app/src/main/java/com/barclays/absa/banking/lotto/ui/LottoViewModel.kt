/*
 * Copyright (c) 2019 Absa Bank Limited, All Rights Reserved.
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

package com.barclays.absa.banking.lotto.ui

import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.cashSend.ui.AccountObjectWrapper
import com.barclays.absa.banking.framework.dagger.getServiceInterface
import com.barclays.absa.banking.home.ui.IHomeCacheService
import com.barclays.absa.banking.lotto.services.*
import com.barclays.absa.banking.lotto.services.dto.LottoGameRules
import com.barclays.absa.banking.presentation.shared.BaseViewModel
import com.barclays.absa.utils.AbsaCacheManager
import lotto.LottoBoard
import styleguide.forms.SelectorList

class LottoViewModel : BaseViewModel() {

    companion object {
        const val GAME_TYPE_LOTTO = "Lotto"
        const val GAME_TYPE_POWERBALL = "Powerball"
        const val GAME_SUBTYPE_LOTTO_PLUS1 = "LottoPlus1"
        const val GAME_SUBTYPE_LOTTO_PLUS2 = "LottoPlus2"
        const val GAME_SUBTYPE_POWERBALL_PLUS = "PowerballPlus"
        const val LOTTO_TERMS_AND_CONDITIONS_PDF_URL_ENGLISH = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/Offers/terms-and-conditions/lotto-and-powerball-terms-and-conditions.pdf"
        const val LOTTO_TERMS_AND_CONDITIONS_PDF_URL_AFRIKAANS = "https://www.absa.co.za/content/dam/south-africa/absa/pdf/Offers/terms-and-conditions/lotto-en-powerball-bepalingsen-voorwaardes.pdf"
    }

    var currentDrawResult: LottoDrawResult = LottoDrawResult()
    var currentTicket: LottoTicket? = null

    var originalDrawResults = mutableListOf<LottoDrawResult>()
    var lottoDrawResultsLiveData = MutableLiveData<LottoDrawResultsResponse>()
    val lottoBoardList = MutableLiveData<ArrayList<LottoBoard>>()
    var selectedLottoBoard = MutableLiveData<LottoBoard>()
    var termsAcceptanceStateLiveData = MutableLiveData<Boolean>()
    var termsAcceptedLiveData = MutableLiveData<Boolean>()
    var serviceAvailabilityLiveData = MutableLiveData<LottoServiceAvailabilityResponse>()
    var lottoGameRulesListLiveData = MutableLiveData<MutableList<LottoGameRules>>()
    var lottoPurchaseResponseLiveData = MutableLiveData<LottoTicketPurchaseResponse>()
    var lottoTicketHistoryLiveData = MutableLiveData<MutableList<LottoTicketHistory>>()

    var currentGameRules = LottoGameRules()
    var currentGameSubType: String? = null
    var purchaseData = LottoTicketPurchaseDataModel()
    var lottoTicketHistory = mutableListOf<LottoTicketHistory>()

    var lastFromDate: String = ""
    var lastToDate: String = ""

    var lottoService: LottoService = LottoInteractor()
    private val lottoAcceptTermsResponseListener = LottoAcceptTermsResponseListener(this)
    private val termsAcceptanceResponseListener = LottoTermsAcceptanceResponseListener(this)
    private val lottoServiceAvailabilityResponseListener = LottoServiceAvailabilityResponseListener(this)
    private val lottoFetchGameRulesResponseListener = LottoFetchGameRulesResponseListener(this)
    private val lottoTicketHistoryResponseListener = LottoTicketHistoryResponseListener(this)
    private val lottoDrawResultsResponseListener = LottoDrawResultsResponseListener(this)
    private val lottoTicketPurchaseResponseListener = LottoTicketPurchaseResponseListener(this)
    private val homeCacheService: IHomeCacheService = getServiceInterface()

    fun loadTermsAcceptanceData() {
        lottoService.checkLottoTermsAcceptance(termsAcceptanceResponseListener)
    }

    fun checkServiceAvailability(gameType: String) {
        lottoService.checkLottoServiceAvailability(gameType, lottoServiceAvailabilityResponseListener)
    }

    fun acceptLottoTerms() {
        lottoService.acceptLottoTermsAndConditions(lottoAcceptTermsResponseListener)
    }

    fun fetchGameRules() {
        lottoService.fetchLottoGameRules(lottoFetchGameRulesResponseListener)
    }

    fun fetchTicketHistory(fromDate: String, toDate: String) {
        lottoService.fetchTicketHistory(fromDate, toDate, lottoTicketHistoryResponseListener)
    }

    fun fetchLottoDrawResults(drawType: String, fromDate: String, toDate: String) {
        this.lastFromDate = fromDate
        this.lastToDate = toDate
        lottoService.fetchDrawResults(drawType, fromDate, toDate, lottoDrawResultsResponseListener)
    }

    fun fetchLastLottoDrawResultsRange(drawType: String) {
        lottoService.fetchDrawResults(drawType, lastFromDate, lastToDate, lottoDrawResultsResponseListener)
    }

    fun purchaseLottoTicket() {
        lottoService.purchaseLottoTickets(purchaseData, lottoTicketPurchaseResponseListener)
    }

    fun getLottoSourceAccounts(): SelectorList<AccountObjectWrapper> {
        val accountObjectWrappers = SelectorList<AccountObjectWrapper>()

        homeCacheService.getLottoSourceAccountList().forEach { sourceAccount ->
            val accountObject = AbsaCacheManager.getTransactionalAccounts().find { sourceAccount.accountNumber == it.accountNumber }
            accountObject?.let { accountObjectWrappers.add(AccountObjectWrapper(it)) }
        }
        return accountObjectWrappers
    }
}
