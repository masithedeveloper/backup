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

package com.barclays.absa.banking.atmAndBranchLocator.ui

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Paint
import android.graphics.Point
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.barclays.absa.banking.R
import com.barclays.absa.banking.atmAndBranchLocator.services.dto.AtmBranchDetails
import com.barclays.absa.banking.databinding.AtmBranchLocatorActivityBinding
import com.barclays.absa.banking.framework.BaseActivity
import com.barclays.absa.banking.framework.utils.AppConstants
import com.barclays.absa.banking.framework.utils.IntentUtil
import com.barclays.absa.banking.framework.utils.TelephoneUtil
import com.barclays.absa.utils.AnalyticsUtil
import com.barclays.absa.utils.PermissionHelper
import com.barclays.absa.utils.viewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import styleguide.content.LineItemView
import styleguide.utils.ImageUtils
import styleguide.utils.extensions.toFormattedCellphoneNumber
import styleguide.utils.extensions.toTitleCase
import java.util.*
import kotlin.math.pow
import kotlin.math.sqrt

class AtmBranchLocatorActivity : BaseActivity(), OnMapReadyCallback, BranchLocatorRecyclerViewAdapter.ItemClickedInterface {

    private lateinit var myLocation: LatLng
    private lateinit var map: GoogleMap
    private lateinit var atmBranchLocatorViewModel: AtmBranchLocatorViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: AtmBranchLocatorActivityBinding
    private lateinit var branchLocatorDescriptorIcon: BitmapDescriptor
    private lateinit var atmLocatorDescriptorIcon: BitmapDescriptor
    private var numberOfAttempts = 0
    private var lastClickedMarker: Marker? = null
    private var currentLatitude: Double = 0.00
    private var currentLongitude: Double = 0.00
    private var destinationLocation: LatLng = LatLng(0.0, 0.0)
    private var isMarkerClicked: Boolean = false
    private var isItemInListClicked = false
    private var isCameraAutoMove = true
    private var isBranchTabSelected: Boolean = false
    private var isForceClosed = false
    private var isMyLocationButtonClicked = false
    private var atmBranchLocatorResultsList = ArrayList<AtmBranchDetails>()
    private var atmLocatorResultsList = ArrayList<AtmBranchDetails>()
    private var branchLocatorResultsList = ArrayList<AtmBranchDetails>()
    private lateinit var openListBottomSheet: BottomSheetBehavior<View>
    private lateinit var atmBottomSheet: BottomSheetBehavior<View>
    private lateinit var branchBottomSheet: BottomSheetBehavior<View>

    companion object {
        const val BRANCH = "BRANCH"
        const val ATM = "ATM"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.atm_branch_locator_activity, null, false)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setToolBarBack(R.string.atm_and_branch_locator) {
            AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_ATMAndBranchLocatorScreen_BackButtonClicked")
            finish()
        }

        AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_ATMAndBranchLocatorScreen_ScreenDisplayed")
        AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_ATMAndBranchLocatorScreen_ATMTabSelected")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionHelper.requestLocationAccessPermission(this)
        } else {
            branchLocatorDescriptorIcon = BitmapDescriptorFactory.fromBitmap(ImageUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_branch_locator_pin))
            atmLocatorDescriptorIcon = BitmapDescriptorFactory.fromBitmap(ImageUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_atm_locator_pin))

            map = googleMap
            map.setMinZoomPreference(13f)
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            showProgressDialog()
            getCurrentLocation()
            setUpOnCameraMoveStartedListener()
            setUpOnCameraIdleListener()
            setUpOnMarkerClickListener()
            setUpViewModels()
            setUpObservers()
            setUpOnClickListeners()

            initialiseBottomSheets()
            bottomSheetSetUp(openListBottomSheet, getString(R.string.open_the_list), binding.atmBranchLocatorOpenListConstraintLayout.openListTextView)
            bottomSheetSetUp(atmBottomSheet, getString(R.string.swipe_up), binding.atmBranchLocatorAtmDetailsConstraintLayout.atmSwipeUpTextView)
            bottomSheetSetUp(branchBottomSheet, getString(R.string.swipe_up), binding.atmBranchLocatorBranchDetailsConstraintLayout.branchSwipeUpTextView)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
                .addOnSuccessListener {
                    if (it != null) {
                        dismissProgressDialog()
                        myLocation = LatLng(it.latitude, it.longitude)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 14f))
                    } else {
                        if (numberOfAttempts < 5) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                getCurrentLocation()
                                numberOfAttempts++
                            }, 5000)
                        } else {
                            showGenericErrorMessageThenFinish()
                        }
                    }
                }
    }

    private fun initialiseBottomSheets() {
        openListBottomSheet = BottomSheetBehavior.from(binding.atmBranchLocatorOpenListConstraintLayout.openListConstraintLayout)
        atmBottomSheet = BottomSheetBehavior.from(binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsConstraintLayout)
        branchBottomSheet = BottomSheetBehavior.from(binding.atmBranchLocatorBranchDetailsConstraintLayout.branchDetailsConstraintLayout)
    }

    private fun bottomSheetSetUp(bottomSheet: BottomSheetBehavior<View>, collapsedStateText: String, textView: TextView) {
        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) {
            }

            override fun onStateChanged(p0: View, state: Int) {
                if (state == BottomSheetBehavior.STATE_COLLAPSED) {
                    textView.text = collapsedStateText
                } else {
                    textView.text = ""
                }
            }
        })
    }

    private fun setUpOnMarkerClickListener() {
        map.setOnMarkerClickListener {
            clearSelectedMarker()
            destinationLocation = it.position!!
            isMarkerClicked = true
            isCameraAutoMove = true
            lastClickedMarker = it
            isItemInListClicked = true

            if (!isBranchTabSelected) {
                for (item in atmLocatorResultsList) {
                    if (it.id == item.marker?.id) {
                        binding.apply {
                            atmBranchLocatorAtmDetailsConstraintLayout.atmDetails.branchOrAtmTextView.text = item.address.toTitleCase()
                            atmBranchLocatorAtmDetailsConstraintLayout.atmDetails.atmOrBranchImageView.setImageResource(R.drawable.ic_atm_locator)
                        }

                        expandAtmOrBranchBottomSheet(binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsConstraintLayout, atmBottomSheet, BottomSheetBehavior.STATE_EXPANDED)

                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(item.marker?.position, 17f))
                        it.setIcon(BitmapDescriptorFactory.fromBitmap(ImageUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_atm_locator_pin_large)))
                        break
                    }
                }
            } else {
                for (item in branchLocatorResultsList) {
                    if (it.id == item.marker?.id) {
                        if (item.isBranch) {
                            displayBranchDetails(item)
                        }
                        break
                    }
                }
            }
            true
        }
    }

    private fun setUpOnCameraIdleListener() {
        map.setOnCameraIdleListener {
            if (!isMarkerClicked) {
                val distanceMoved = calculateDistance(currentLatitude, currentLongitude, map.cameraPosition.target.latitude, map.cameraPosition.target.longitude)
                if (distanceMoved > 0.02) {
                    showProgressDialog()
                    atmBranchLocatorResultsList.clear()
                    atmLocatorResultsList.clear()
                    branchLocatorResultsList.clear()
                    atmBranchLocatorViewModel.fetchBranchLocations(map.cameraPosition.target.latitude.toString(), map.cameraPosition.target.longitude.toString())
                }
            } else {
                isMarkerClicked = false
            }
        }
    }

    private fun setUpOnCameraMoveStartedListener() {
        map.setOnCameraMoveStartedListener {
            if (!isItemInListClicked) {
                clearSelectedMarker()

                when {
                    branchBottomSheet.state == BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        collapseAtmOrBranchBottomSheet(binding.atmBranchLocatorBranchDetailsConstraintLayout.branchDetailsConstraintLayout, branchBottomSheet)
                    }
                    atmBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED -> {
                        collapseAtmOrBranchBottomSheet(binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsConstraintLayout, atmBottomSheet)
                    }
                    else -> {
                        hideAtmAndBranchConstraintLayout()
                    }
                }
            } else {
                isItemInListClicked = false
            }
        }
    }

    private fun offsetMarkerPosition(marker: Marker) {
        val projection: Projection = map.projection
        val markerPosition: LatLng = marker.position
        val markerPoint: Point = projection.toScreenLocation(markerPosition)
        val offset = ((binding.atmAndBranchTabLayout.bottom / 8).toDouble() - 5) * 2.0.pow((map.cameraPosition.zoom - 13).toDouble())
        val targetPoint = Point(markerPoint.x, (markerPoint.y + offset).toInt())
        val targetPosition: LatLng = projection.fromScreenLocation(targetPoint)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(targetPosition, 17f))
    }

    private fun clearSelectedMarker() {
        if (lastClickedMarker != null) {
            if (isBranchTabSelected) {
                lastClickedMarker!!.setIcon(branchLocatorDescriptorIcon)
            } else {
                lastClickedMarker!!.setIcon(atmLocatorDescriptorIcon)
            }
            lastClickedMarker = null
        }
    }

    private fun setUpViewModels() {
        atmBranchLocatorViewModel = viewModel()
    }

    private fun setUpOnClickListeners() {

        map.setOnMyLocationButtonClickListener {
            openListBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            isMyLocationButtonClicked = true
            false
        }

        binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsCloseImageView.setOnClickListener {
            clearSelectedMarker()
            collapseAtmOrBranchBottomSheet(binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsConstraintLayout, atmBottomSheet)
            isMyLocationButtonClicked = false
        }

        binding.atmBranchLocatorBranchDetailsConstraintLayout.branchDetailsCloseImageView.setOnClickListener {
            clearSelectedMarker()
            collapseAtmOrBranchBottomSheet(binding.atmBranchLocatorBranchDetailsConstraintLayout.branchDetailsConstraintLayout, branchBottomSheet)
            isMyLocationButtonClicked = false
        }

        binding.atmBranchLocatorBranchDetailsConstraintLayout.atmBranchLocatorCallOptionActionButtonView.setOnClickListener {
            TelephoneUtil.call(this, binding.atmBranchLocatorBranchDetailsConstraintLayout.atmBranchLocatorCallOptionActionButtonView.captionTextView.text.toString())
        }

        binding.atmBranchLocatorBranchDetailsConstraintLayout.atmBranchLocatorWebsiteOptionActionButtonView.setOnClickListener {
            val openUrl = Intent(Intent.ACTION_VIEW).setData(Uri.parse(AppConstants.ABSA_ONLINE_URL))
            startActivityIfAvailable(openUrl)
        }

        binding.atmBranchLocatorBranchDetailsConstraintLayout.getDirectionsButton.setOnClickListener {
            getDirections(it)
        }

        binding.atmBranchLocatorAtmDetailsConstraintLayout.atmGetDirectionsButton.setOnClickListener {
            getDirections(it)
        }

        binding.atmBranchLocatorOpenListConstraintLayout.openListImageView.setOnClickListener {
            if (openListBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                openListBottomSheet.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }

        binding.atmBranchLocatorBranchDetailsConstraintLayout.branchOpenListImageView.setOnClickListener {
            if (branchBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                branchBottomSheet.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        }

        binding.atmBranchLocatorAtmDetailsConstraintLayout.atmOpenListImageView.setOnClickListener {
            if (atmBottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                atmBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        binding.atmAndBranchTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {

                hideAtmAndBranchConstraintLayout()

                if (p0?.position == 0) {
                    AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_ATMAndBranchLocatorScreen_ATMTabSelected")
                    map.clear()
                    lastClickedMarker = null
                    for (item in atmLocatorResultsList) {
                        val location = LatLng(item.latitude.toDouble(), item.longitude.toDouble())
                        item.marker = map.addMarker(MarkerOptions().position(location).title(item.address).icon(atmLocatorDescriptorIcon))
                    }

                    isBranchTabSelected = false
                    val sortedList = atmLocatorResultsList.sortedWith(compareBy { it.distance })
                    val adapter = BranchLocatorRecyclerViewAdapter(sortedList, this@AtmBranchLocatorActivity)
                    binding.atmBranchLocatorOpenListConstraintLayout.branchListRecyclerView.adapter = adapter
                } else {
                    AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_ATMAndBranchLocatorScreen_BranchTabSelected")
                    map.clear()
                    lastClickedMarker = null
                    for (item in branchLocatorResultsList) {
                        val location = LatLng(item.latitude.toDouble(), item.longitude.toDouble())
                        item.marker = map.addMarker(MarkerOptions().position(location).title(item.address).icon(branchLocatorDescriptorIcon))
                    }
                    isBranchTabSelected = true
                    val sortedList = branchLocatorResultsList.sortedWith(compareBy { it.distance })
                    val adapter = BranchLocatorRecyclerViewAdapter(sortedList, this@AtmBranchLocatorActivity)
                    binding.atmBranchLocatorOpenListConstraintLayout.branchListRecyclerView.adapter = adapter
                }
            }
        })
    }

    private fun getDirections(view: View) {
        AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_ATMAndBranchLocatorScreen_GetDirectionsButtonClicked")
        preventDoubleClick(view)
        try {
            val navigation = Intent(Intent.ACTION_VIEW).setData(Uri.parse("geo:" + destinationLocation.latitude + "," + destinationLocation.longitude + "?q=" + destinationLocation.latitude + "," + destinationLocation.longitude))
            val isIntentAvailable = IntentUtil.isIntentAvailable(this, navigation)
            if (isIntentAvailable) {
                startActivity(navigation)
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"))
                startActivity(intent)
            }
        } catch (ex: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"))
            startActivity(intent)
        }
    }

    private fun hideAtmAndBranchConstraintLayout() {
        branchBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        atmBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.apply {
            binding.atmBranchLocatorBranchDetailsConstraintLayout.branchDetailsConstraintLayout.visibility = View.GONE
            binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsConstraintLayout.visibility = View.GONE
        }
        binding.atmBranchLocatorOpenListConstraintLayout.openListConstraintLayout.visibility = View.VISIBLE
        isForceClosed = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::atmBranchLocatorViewModel.isInitialized) {
            atmBranchLocatorViewModel.atmBranchLocatorExtendedResponse.removeObservers(this)
        }
    }

    private fun setUpObservers() {
        atmBranchLocatorViewModel.atmBranchLocatorExtendedResponse = MutableLiveData()
        atmBranchLocatorViewModel.atmBranchLocatorExtendedResponse.observe(this, { atmBranchLocatorResponse ->

            if (atmBranchLocatorResponse.atmBranchDetails.isEmpty()) {
                binding.atmBranchLocatorOpenListConstraintLayout.openListConstraintLayout.visibility = View.GONE
            } else {
                binding.atmBranchLocatorOpenListConstraintLayout.openListConstraintLayout.visibility = View.VISIBLE
            }

            atmBranchLocatorResponse.atmBranchDetails.let { list ->
                if (atmBranchLocatorResultsList.isEmpty()) {
                    addNonDuplicateItemToList(list)
                    currentLatitude = map.cameraPosition.target.latitude
                    currentLongitude = map.cameraPosition.target.longitude
                }
                map.clear()
                lastClickedMarker = null
                if (!isBranchTabSelected) {
                    atmLocatorResultsList.forEach { item ->
                        val location = LatLng(item.latitude.toDouble(), item.longitude.toDouble())
                        item.marker = map.addMarker(MarkerOptions().position(location).title(item.address).icon(atmLocatorDescriptorIcon))
                    }
                    val sortedList = atmLocatorResultsList.sortedWith(compareBy { it.distance })
                    val adapter = BranchLocatorRecyclerViewAdapter(sortedList, this@AtmBranchLocatorActivity)
                    binding.atmBranchLocatorOpenListConstraintLayout.branchListRecyclerView.adapter = adapter
                } else {
                    branchLocatorResultsList.forEach { item ->
                        val location = LatLng(item.latitude.toDouble(), item.longitude.toDouble())
                        item.marker = map.addMarker(MarkerOptions().position(location).title(item.address).icon(branchLocatorDescriptorIcon))
                    }
                    val sortedList = branchLocatorResultsList.sortedWith(compareBy { it.distance })
                    val adapter = BranchLocatorRecyclerViewAdapter(sortedList, this@AtmBranchLocatorActivity)
                    binding.atmBranchLocatorOpenListConstraintLayout.branchListRecyclerView.adapter = adapter
                }
                dismissProgressDialog()
                binding.atmBranchLocatorOpenListConstraintLayout.openListConstraintLayout.visibility = View.VISIBLE
                openListBottomSheet.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            }
        })
    }

    private fun addNonDuplicateItemToList(atmBranchDetails: List<AtmBranchDetails>) {
        atmBranchDetails.forEach { item ->
            var isDuplicate = false

            for (atmBranchItem in atmBranchLocatorResultsList) {
                if (atmBranchItem.address.equals(item.address, true)) {
                    isDuplicate = true
                    break
                }
            }
            if (!isDuplicate) {
                if (ATM.equals(item.outletType, true) && atmLocatorResultsList.size < 20) {
                    atmLocatorResultsList.add(item)
                } else if (BRANCH.equals(item.outletType, true) && branchLocatorResultsList.size < 20) {
                    item.isBranch = true
                    branchLocatorResultsList.add(item)
                }
                atmBranchLocatorResultsList.add(item)
            }
        }
    }

    private fun calculateDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
        return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
    }

    override fun onBackPressed() {
        isForceClosed = true
        clearSelectedMarker()
        when {
            branchBottomSheet.state == BottomSheetBehavior.STATE_HALF_EXPANDED || branchBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED -> {
                collapseAtmOrBranchBottomSheet(binding.atmBranchLocatorBranchDetailsConstraintLayout.branchDetailsConstraintLayout, branchBottomSheet)
                isMyLocationButtonClicked = false
            }
            atmBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED -> {
                collapseAtmOrBranchBottomSheet(binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsConstraintLayout, atmBottomSheet)
                isMyLocationButtonClicked = false
            }
            binding.atmBranchLocatorOpenListConstraintLayout.openListConstraintLayout.visibility == View.GONE -> {
                super.onBackPressed()
                AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_ATMAndBranchLocatorScreen_BackButtonClicked")
            }
            openListBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED -> openListBottomSheet.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsConstraintLayout.visibility == View.GONE && binding.atmBranchLocatorBranchDetailsConstraintLayout.branchDetailsConstraintLayout.visibility == View.GONE -> {
                super.onBackPressed()
                AnalyticsUtil.trackAction("ATM Branch Locator", "ATMAndBranchLocator_ATMAndBranchLocatorScreen_BackButtonClicked")
            }
            else -> hideAtmAndBranchConstraintLayout()
        }
    }

    private fun collapseAtmOrBranchBottomSheet(view: View, bottomSheet: BottomSheetBehavior<View>) {
        openListBottomSheet.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        binding.atmBranchLocatorOpenListConstraintLayout.openListConstraintLayout.visibility = View.VISIBLE
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        view.visibility = View.GONE
    }

    private fun expandAtmOrBranchBottomSheet(view: View, bottomSheet: BottomSheetBehavior<View>, setState: Int) {
        openListBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        binding.atmBranchLocatorOpenListConstraintLayout.openListConstraintLayout.visibility = View.GONE
        view.visibility = View.VISIBLE
        bottomSheet.state = setState
    }

    override fun itemClicked(atmBranchResults: AtmBranchDetails) {
        lastClickedMarker = atmBranchResults.marker
        isItemInListClicked = true
        isMarkerClicked = true

        if (BRANCH.equals(atmBranchResults.outletType, true)) {
            displayBranchDetails(atmBranchResults)
        } else {
            destinationLocation = atmBranchResults.marker?.position!!
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(atmBranchResults.marker?.position, 17f))
            atmBranchResults.marker?.setIcon(BitmapDescriptorFactory.fromBitmap(ImageUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_atm_locator_pin_large)))
            binding.apply {
                atmBranchLocatorAtmDetailsConstraintLayout.atmDetails.branchOrAtmTextView.text = atmBranchResults.address.toTitleCase()
                atmBranchLocatorAtmDetailsConstraintLayout.atmDetails.atmOrBranchImageView.setImageResource(R.drawable.ic_atm_locator)

                expandAtmOrBranchBottomSheet(binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsConstraintLayout, atmBottomSheet, BottomSheetBehavior.STATE_EXPANDED)
            }
            isCameraAutoMove = true
        }
    }

    private fun displayBranchDetails(atmBranchResults: AtmBranchDetails) {
        destinationLocation = atmBranchResults.marker?.position!!
        offsetMarkerPosition(atmBranchResults.marker as Marker)
        atmBranchResults.marker?.setIcon(BitmapDescriptorFactory.fromBitmap(ImageUtils.getBitmapFromVectorDrawable(this, R.drawable.ic_branch_locator_pin_large)))
        isCameraAutoMove = true
        binding.atmBranchLocatorBranchDetailsConstraintLayout.atmBranchLocatorItem.branchOrAtmTextView.text = atmBranchResults.address.toTitleCase()

        if (atmBranchResults.telephoneNumber.isEmpty() || atmBranchResults.telephoneNumber.length < 9) {
            binding.atmBranchLocatorBranchDetailsConstraintLayout.atmBranchLocatorCallOptionActionButtonView.setCaptionText(getString(R.string.atm_branch_locator_default_contact_number))
        } else {
            binding.atmBranchLocatorBranchDetailsConstraintLayout.atmBranchLocatorCallOptionActionButtonView.setCaptionText(formatTelephoneNumber(atmBranchResults.telephoneNumber))
        }

        if (atmBranchResults.weekdayHours.isEmpty() || atmBranchResults.weekdayHours == " - ") {
            binding.atmBranchLocatorBranchDetailsConstraintLayout.operatingHoursConstraintLayout.visibility = View.GONE
        } else {
            binding.apply {
                binding.atmBranchLocatorBranchDetailsConstraintLayout.branchDetailsConstraintLayout.visibility = View.GONE
                binding.atmBranchLocatorAtmDetailsConstraintLayout.atmDetailsConstraintLayout.visibility = View.GONE
            }
            binding.atmBranchLocatorBranchDetailsConstraintLayout.operatingHoursLinearLayout.removeAllViewsInLayout()
            val weekDays = resources.getStringArray(R.array.atmBranchLocatorWeekDays)
            weekDays.forEach {
                addOperatingHoursToView(it, atmBranchResults.weekdayHours)
            }

            val weekendDays = resources.getStringArray(R.array.atmBranchLocatorWeekendDays)
            var saturdayOperatingHours = atmBranchResults.weekendHours
            if (atmBranchResults.weekendHours.startsWith("Saturday", true)) {
                saturdayOperatingHours = atmBranchResults.weekendHours.replaceFirst("Saturday", "")
            }
            addOperatingHoursToView(weekendDays[0], saturdayOperatingHours)
            addOperatingHoursToView(weekendDays[1], getString(R.string.atm_branch_locator_closed))
            binding.atmBranchLocatorBranchDetailsConstraintLayout.operatingHoursConstraintLayout.visibility = View.VISIBLE
        }
        binding.apply {
            binding.atmBranchLocatorBranchDetailsConstraintLayout.scrollView.scrollTo(0, 0)
            binding.atmBranchLocatorBranchDetailsConstraintLayout.atmBranchLocatorWebsiteOptionActionButtonView.captionTextView.paintFlags = binding.atmBranchLocatorBranchDetailsConstraintLayout.atmBranchLocatorWebsiteOptionActionButtonView.captionTextView.paintFlags or Paint.UNDERLINE_TEXT_FLAG

            expandAtmOrBranchBottomSheet(binding.atmBranchLocatorBranchDetailsConstraintLayout.branchDetailsConstraintLayout, branchBottomSheet, BottomSheetBehavior.STATE_HALF_EXPANDED)
        }
    }

    private fun addOperatingHoursToView(day: String, hours: String) {
        val lineItemView = LineItemView(this)
        lineItemView.getLabelTextView().text = day
        lineItemView.getContentTextView().text = hours
        binding.atmBranchLocatorBranchDetailsConstraintLayout.operatingHoursLinearLayout.addView(lineItemView)
    }

    private fun formatTelephoneNumber(unformattedTelephoneNumber: String): String {
        return unformattedTelephoneNumber.replace("-", " ").toFormattedCellphoneNumber()
    }
}
