/*
 *  Copyright (c) 2018 Absa Bank Limited, All Rights Reserved.
 *
 *  This code is confidential to Absa Bank Limited and shall not be disclosed
 *  outside the Bank without the prior written permission of the Absa Legal
 *
 *  In the event that such disclosure is permitted the code shall not be copied
 *  or distributed other than on a need-to-know basis and any recipients may be
 *  required to sign a confidentiality undertaking in favor of Absa Bank
 *  Limited
 *
 */
package com.barclays.absa.banking.framework;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.ref.WeakReference;

public class AbstractPresenterTest {

    @Mock
    private BaseView viewMock;

    @Mock
    private WeakReference<BaseView> viewMockWeakReference;

    private AbstractPresenter testSubject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        testSubject = new AbstractPresenter(viewMockWeakReference);
    }

//    @Test
//    public void shouldShowProgressIndicator() {
//        doReturn(viewMock).when(viewMockWeakReference).get();
//        testSubject.showProgressIndicator();
//        verify(viewMock).showProgressDialog();
//    }
//
//    @Test
//    public void shouldDismissProgressIndicator() {
//        doReturn(viewMock).when(viewMockWeakReference).get();
//        testSubject.dismissProgressIndicator();
//        verify(viewMock).dismissProgressDialog();
//    }
}