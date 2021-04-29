/*
 * Copyright (c) 2020. Absa Bank Limited, All Rights Reserved.
 *
 * This code is confidential to Absa Bank Limited and shall not be disclosed
 * outside the Bank without the prior written permission of the Absa Legal
 *
 * In the event that such disclosure is permitted the code shall not be copied or distributed other than on a
 * need-to-know basis and any recipients may be required to sign a confidentiality undertaking in favor of Absa Bank Limited
 */
package com.barclays.absa.banking.recognition.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.barclays.absa.banking.recognition.services.BranchRecognitionRepositoryImpl
import com.barclays.absa.banking.recognition.services.dto.models.BranchRecognitionConfirmation
import com.nhaarman.mockito_kotlin.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.MockitoAnnotations

internal class BranchRecognitionActivityViewModelTest {
    val testSubject = BranchRecognitionActivityViewModel()
    private val branchRecognitionMockService = mock<BranchRecognitionRepositoryImpl>()

    @get: Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        testSubject.branchRecognitionRepository = branchRecognitionMockService
    }

    @Test
    fun shouldCallUploadBranchFeedbackWhenUploadBranchDataFunctionIsCalled() {
        val branchRecognitionConfirmation = BranchRecognitionConfirmation()

        testSubject.uploadBranchData(branchRecognitionConfirmation)
        verify(branchRecognitionMockService).uploadBranchFeedback(branchRecognitionConfirmation)
        verifyNoMoreInteractions(branchRecognitionMockService)
    }

    @Test
    fun shouldCallGetRecognitionMenuInfoWhenProvideInitialDataFunctionIsCalled() {
        testSubject.provideInitialData()
        verify(branchRecognitionMockService).getRecognitionMenuInfo()
        verifyNoMoreInteractions(branchRecognitionMockService)
    }
}