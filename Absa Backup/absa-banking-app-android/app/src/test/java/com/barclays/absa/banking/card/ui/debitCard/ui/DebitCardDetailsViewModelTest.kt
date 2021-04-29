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
package com.barclays.absa.banking.card.ui.debitCard.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.barclays.absa.banking.boundary.model.Amount
import com.barclays.absa.banking.boundary.model.BranchDeliveryDetailsList
import com.barclays.absa.banking.boundary.model.CardReplacementAndFetchFees
import com.barclays.absa.banking.boundary.model.ClientContactInformationList
import com.barclays.absa.banking.boundary.model.debitCard.*
import com.barclays.absa.banking.card.services.card.DebitCardInteractor
import com.barclays.absa.banking.card.services.card.dto.BranchDeliveryDetailsListRequest
import com.barclays.absa.banking.card.services.card.dto.ClientContactInformationRequest
import com.barclays.absa.banking.card.services.card.dto.DebitCardProductTypeListRequest
import com.barclays.absa.banking.card.services.card.dto.DebitCardReplacementReasonListRequest
import com.barclays.absa.banking.card.ui.debitCard.services.CardReplacementAndFetchFeesExtendedResponseListener
import com.barclays.absa.banking.card.ui.debitCard.services.DebitCardReplacementConfirmationResponseListener
import com.barclays.absa.banking.card.ui.debitCard.services.DebitCardTypeExtendedResponseListener
import com.barclays.absa.banking.card.ui.debitCard.ui.ViewModelReflectionUtil.viewModelReflectionUtil
import com.barclays.absa.banking.framework.ExtendedRequest
import com.barclays.absa.banking.framework.ExtendedResponseListener
import com.nhaarman.mockito_kotlin.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

internal class DebitCardDetailsViewModelTest {
    private var debitCardViewModelUnderTest = DebitCardDetailsViewModel()
    private var debitCardInteractor = mock<DebitCardInteractor>()
    private var debitCardReplacementDetailsConfirmation = DebitCardReplacementDetailsConfirmation()
    private val debiCardViewModelSpy = spy(debitCardViewModelUnderTest)

    @Captor
    private lateinit var debitCardTypeListCaptor: ArgumentCaptor<DebitCardTypeExtendedResponseListener>
    @Captor
    private lateinit var replacementFeeCaptor: ArgumentCaptor<CardReplacementAndFetchFeesExtendedResponseListener>
    @Captor
    private lateinit var replacementCaptor: ArgumentCaptor<DebitCardReplacementConfirmationResponseListener>

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        debitCardViewModelUnderTest.debitCardInteractor = debitCardInteractor
    }

    @Test
    fun shouldCallInteractorFetchCardTypeList_whenCardTypeSelectedIsCalled() {
        val productType = DebitCardProductType()
        debitCardViewModelUnderTest.cardTypeSelected(productType)

        val extendedResponseListenerReflection = viewModelReflectionUtil<ExtendedResponseListener<DebitCardTypeList>>(debitCardViewModelUnderTest, "debitCardTypeExtendedResponseListener")
        verify(debitCardInteractor).fetchCardTypeList(productType, extendedResponseListenerReflection)
        verifyNoMoreInteractions(debitCardInteractor)
    }

    @Test
    fun shouldCallInteractorFetchReplacementFee_whenFetchReplacementFeeIsCalled() {
        debitCardViewModelUnderTest.fetchReplacementFee(debitCardReplacementDetailsConfirmation)
        val extendedResponseListenerReflection = viewModelReflectionUtil<ExtendedResponseListener<CardReplacementAndFetchFees>>(debitCardViewModelUnderTest, "cardReplacementAndFetchFeesExtendedResponseListener")
        verify(debitCardInteractor).fetchReplacementFee(debitCardReplacementDetailsConfirmation, extendedResponseListenerReflection)
        verifyNoMoreInteractions(debitCardInteractor)
    }

    @Test
    fun shouldCallInteractorReplaceCard_whenDebitCardReplacementIsCalled() {
        debitCardViewModelUnderTest.debitCardReplacement(debitCardReplacementDetailsConfirmation)
        val extendedResponseListenerReflection = viewModelReflectionUtil<ExtendedResponseListener<DebitCardReplacementConfirmation>>(debitCardViewModelUnderTest, "debitCardReplacementConfirmationResponseListener")
        verify(debitCardInteractor).replaceCard(debitCardReplacementDetailsConfirmation, extendedResponseListenerReflection)
        verifyNoMoreInteractions(debitCardInteractor)
    }

    @Test
    fun shouldCallInteractorFetchDebitCardData_whenFetchDebitCardDataIsCalled() {
        val arrayList = ArrayList<ExtendedRequest<*>>().apply {
            add(ClientContactInformationRequest(viewModelReflectionUtil<ExtendedResponseListener<ClientContactInformationList>>(debitCardViewModelUnderTest, "clientContactInformationListExtendedResponseListener")))
            add(DebitCardReplacementReasonListRequest(viewModelReflectionUtil<ExtendedResponseListener<DebitCardReplacementReasonList>>(debitCardViewModelUnderTest, "debitCardReasonListExtendedResponseListener")))
            add(DebitCardProductTypeListRequest(viewModelReflectionUtil<ExtendedResponseListener<DebitCardProductTypeList>>(debitCardViewModelUnderTest, "debitCardProductTypeListExtendedResponseListener")))
            add(BranchDeliveryDetailsListRequest(viewModelReflectionUtil<ExtendedResponseListener<BranchDeliveryDetailsList>>(debitCardViewModelUnderTest, "branchDeliveryDetailsListExtendedResponseListener")))
        }

        debitCardViewModelUnderTest.fetchDebitCardData()
        verify(debitCardInteractor).fetchDebitCardData(Mockito.anyList())

        arrayList.forEachIndexed { index, extendedRequest ->
            val arrayListReflection = viewModelReflectionUtil<ArrayList<ExtendedRequest<*>>>(debitCardViewModelUnderTest, "debitCardRequestList")
            Assert.assertSame(extendedRequest.responseClass, arrayListReflection[index].responseClass)
        }
        verifyNoMoreInteractions(debitCardInteractor)
    }

    @Test
    fun shouldReturnDebitCard_whenServiceCallIsComplete() {
        val replacementExtendedResponseListener = DebitCardReplacementConfirmationResponseListener(debitCardViewModelUnderTest)

        debitCardInteractor.replaceCard(debitCardReplacementDetailsConfirmation, replacementExtendedResponseListener)
        verify(debitCardInteractor).replaceCard(any(), capture(replacementCaptor))

        Assert.assertEquals(replacementCaptor.value, replacementExtendedResponseListener)
        verifyNoMoreInteractions(debitCardInteractor)
    }

    @Test
    fun shouldReturnReplacementFee_whenServiceCallIsComplete() {
        val replacementFeeExtendedResponseListener = CardReplacementAndFetchFeesExtendedResponseListener(debitCardViewModelUnderTest)
        val cardReplacementAndFetchFees = CardReplacementAndFetchFees().apply {
            cardDeliveryFee = Amount("50.00")
            replacementFee = Amount("180.00")
        }

        debitCardInteractor.fetchReplacementFee(debitCardReplacementDetailsConfirmation, replacementFeeExtendedResponseListener)
        verify(debitCardInteractor).fetchReplacementFee(any(), capture(replacementFeeCaptor))
        debiCardViewModelSpy.cardReplacementAndFetchFeesExtendedResponse.value = cardReplacementAndFetchFees

        Assert.assertEquals(replacementFeeCaptor.value, replacementFeeExtendedResponseListener)
        Assert.assertNotNull(debiCardViewModelSpy.cardReplacementAndFetchFeesExtendedResponse.value)
        verifyNoMoreInteractions(debitCardInteractor)
    }

    @Test
    fun shouldReturnCardTypeList_whenServiceCallIsComplete() {
        val cardTypeExtendedResponseListener = DebitCardTypeExtendedResponseListener(this.debitCardViewModelUnderTest)

        val productType = DebitCardProductType().apply {
            productDesc = "Cheques"
            productType = "CQ"
            productCode = "11003"
        }

        val debitCardTypeList = DebitCardTypeList().apply {
            debitCardTypeList = arrayListOf(DebitCardType().apply {
                brandName = "Platinum Debit Card"
                brandNumber = "2811"
                brandType = "L"
            }, DebitCardType().apply {
                brandName = "Vanilla DT Chip Flag"
                brandNumber = "2157"
                brandType = "L"
            })
        }

        debitCardInteractor.fetchCardTypeList(productType, cardTypeExtendedResponseListener)
        verify(debitCardInteractor).fetchCardTypeList(any(), capture(debitCardTypeListCaptor))
        debiCardViewModelSpy.debitCardTypeExtendedResponse.value = debitCardTypeList

        Assert.assertEquals(debitCardTypeListCaptor.value, cardTypeExtendedResponseListener)
        Assert.assertNotNull(debiCardViewModelSpy.debitCardTypeExtendedResponse.value)
        Assert.assertNotNull(debiCardViewModelSpy.debitCardTypeExtendedResponse.value?.debitCardTypeList)
        Assert.assertTrue(debiCardViewModelSpy.debitCardTypeExtendedResponse.value?.debitCardTypeList?.isNotEmpty() == true)
        Assert.assertEquals("2811", debitCardTypeList.debitCardTypeList[0].brandNumber)
        Assert.assertEquals(2, debiCardViewModelSpy.debitCardTypeExtendedResponse.value?.debitCardTypeList?.size)
        verifyNoMoreInteractions(debitCardInteractor)
    }
}