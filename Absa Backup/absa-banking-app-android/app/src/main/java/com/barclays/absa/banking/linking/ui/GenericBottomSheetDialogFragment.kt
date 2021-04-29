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
 */

package com.barclays.absa.banking.linking.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import com.barclays.absa.banking.R
import com.barclays.absa.banking.databinding.LinkingFailureStateFragmentBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class GenericBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: LinkingFailureStateFragmentBinding
    private lateinit var baseActivity: BaseActivity
    lateinit var genericBottomSheetContent: GenericBottomSheetContent

    companion object {
        const val GENERIC_BOTTOM_SHEET_DATA = "genericBottomSheetData"

        @JvmStatic
        var toolbarActionOnClickListener: View.OnClickListener? = null

        @JvmStatic
        var onDismissListener: DialogInterface.OnDismissListener? = null

        @JvmStatic
        fun newInstance(genericBottomSheetContent: GenericBottomSheetContent): GenericBottomSheetDialogFragment =
                GenericBottomSheetDialogFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(GENERIC_BOTTOM_SHEET_DATA, genericBottomSheetContent)
                    }
                }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        baseActivity = context as BaseActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LinkingFailureStateFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        genericBottomSheetContent = arguments?.getSerializable(GENERIC_BOTTOM_SHEET_DATA) as GenericBottomSheetContent? ?: GenericBottomSheetContent()

        initDataForScreen()

        binding.failureActionTextView.setOnClickListener(toolbarActionOnClickListener)
        dialog?.setOnDismissListener(onDismissListener)
    }

    private fun initDataForScreen() {
        with(genericBottomSheetContent) {
            binding.apply {
                bottomSheetTitleTextView.setTextIfNotEmpty(toolbarTitle)
                failureActionTextView.setTextIfNotEmpty(toolbarActionTitle)
                failureTitleTextView.setTextIfNotEmpty(contentTitleText)
                failureDescriptionTextView.setTextIfNotEmpty(contentText)

                failureImageView.visibility = if (bottomSheetImage == -1) View.GONE else View.VISIBLE
                bottomSheetTitleTextView.visibility = if (toolbarTitle == -1) View.GONE else View.VISIBLE
                failureTitleTextView.visibility = if (contentTitleText == -1) View.GONE else View.VISIBLE
                failureDescriptionTextView.visibility = if (contentText == -1) View.GONE else View.VISIBLE

                contactDetailsContactView.visibility = if (contactNumber == -1) View.GONE else View.VISIBLE

                if (contactDetailsContactView.isVisible) {
                    ConstraintLayout.LayoutParams(failureActionTextView.layoutParams).apply {
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topMargin = resources.getDimensionPixelSize(R.dimen.small_space)
                        failureActionTextView.layoutParams = this
                    }

                    ConstraintLayout.LayoutParams(failureDescriptionTextView.layoutParams).apply {
                        topToBottom = R.id.failureActionTextView
                        topMargin = resources.getDimensionPixelSize(R.dimen.medium_space)
                        failureDescriptionTextView.layoutParams = this
                    }

                    contactDetailsContactView.setContact(getString(contactName), getString(contactNumber))
                }

                if (failureImageView.isVisible) {
                    failureImageView.setImageResource(bottomSheetImage)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val bottomSheet = dialog?.findViewById<View>(R.id.design_bottom_sheet)
        bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT

        view?.post {
            val parent = view?.parent as View
            val params = parent.layoutParams as CoordinatorLayout.LayoutParams
            val behavior = params.behavior
            val bottomSheetBehavior = behavior as? BottomSheetBehavior<*>
            view?.let { bottomSheetBehavior?.peekHeight = it.measuredHeight - (baseActivity.toolBar?.height ?: resources.getDimension(R.dimen._50sdp).toInt()) }
        }
    }

    private fun TextView.setTextIfNotEmpty(stringResource: Int) {
        if (stringResource != -1) {
            this.setText(stringResource)
        }
    }
}