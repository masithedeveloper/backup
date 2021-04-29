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
package styleguide.forms

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.text.*
import android.text.InputFilter.LengthFilter
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.TextViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import styleguide.forms.maskedEditText.MaskedEditText
import styleguide.forms.validation.ValidationRule
import styleguide.forms.validation.validate
import styleguide.utils.AnimationHelper
import styleguide.utils.CurrencyTextWatcher
import styleguide.utils.ViewAnimation
import styleguide.utils.extensions.removeStringCurrencyDefaultZero
import za.co.absa.presentation.uilib.R
import java.util.*

abstract class SelectorView<T : SelectorInterface> : ConstraintLayout, ItemSelectionInterface, View.OnClickListener, ValueSelectedObserver {

    protected var displayRecyclerView: RecyclerView? = null
    protected var selectedValueTextView: TextView? = null
    protected var valueEditText: MaskedEditText? = null
    var validators: MutableList<ValidationRule> = mutableListOf()
    private var parentConstraintLayout: ConstraintLayout? = null
    private var iconImageView: ImageView? = null
    private var animClick: Animation? = null
    private var selectorViewType: SelectorType? = null
    private var selectorViewAdapter: SelectorViewAdapter? = null
    private var isAnimating = false
    private var isCustomCurrency = false
    private var toolbarTitle: String? = null
    private var itemSelectionInterface: ItemSelectionInterface? = null
    private var dividerView: View? = null
    private var bottomDividerView: View? = null
    private var viewAnimationShortList: ViewAnimation? = null
    private var isViewEnabled = true
    private var contentDescription: List<String>? = null
    private var currency: String = "R"
    private var currencyTextWatcher: CurrencyTextWatcher? = null
    private var originalValue: String? = ""

    var descriptionTextView: TextView? = null
        protected set
    var titleTextView: TextView? = null
        protected set
    var itemList: SelectorList<T>? = null
        private set
    open var errorTextView: TextView? = null

    var previousIndex: Int = -1

    open var selectedIndex = -1
        set(value) {
            field = value
            if (itemList != null && selectedIndex > -1 && selectedIndex < itemList!!.size) {
                populateView(itemList!![value].displayValue ?: "")
            }
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        private const val SUPER_INSTANCE_STATE = "saved_instance_state_parcelable"
        private const val DESCRIPTION_VALUE = "description_value"
        private const val ERROR_VIEW_VALUE = "error_view_value"
        private const val VALUE_EDIT_TEXT_VALUE = "value_edit_text_value"
        private const val VALUE_SELECTED_INDEX = "value_selected_index"
        private const val CURRENCY_VALUE = "currency_value"
        private const val SELECTOR_LIST = "selector_list"
        private const val TOOLBAR_TITLE = "toolbar_title"
        private const val DEFAULT_MAX_LENGTH = -1
        private const val TWO_SECOND_DELAY = 2000
    }

    fun setItemSelectionInterface(itemSelectionInterface: ItemSelectionInterface) {
        this.itemSelectionInterface = itemSelectionInterface
    }

    override fun onSaveInstanceState(): Parcelable? {
        Bundle().apply {
            putParcelable(SUPER_INSTANCE_STATE, super.onSaveInstanceState())
            putInt(VALUE_SELECTED_INDEX, selectedIndex)
            if (descriptionTextView?.visibility == View.VISIBLE) {
                putString(DESCRIPTION_VALUE, descriptionTextView?.text.toString())
            }
            if (valueEditText != null) {
                putString(VALUE_EDIT_TEXT_VALUE, selectedValueUnmasked)
            }
            if (errorTextView?.visibility == View.VISIBLE) {
                putString(ERROR_VIEW_VALUE, errorTextView?.text.toString())
            }
            if (isCustomCurrency) {
                putString(CURRENCY_VALUE, currency)
            }
            itemList?.let {
                putSerializable(SELECTOR_LIST, it)
            }
            putString(TOOLBAR_TITLE, toolbarTitle)
            return this
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_INSTANCE_STATE))
        if (bundle.containsKey(CURRENCY_VALUE)) {
            setCurrency(bundle.getString(CURRENCY_VALUE).toString())
        }
        selectedIndex = bundle.getInt(VALUE_SELECTED_INDEX, -1)
        valueEditText?.setText(bundle.getString(VALUE_EDIT_TEXT_VALUE, ""))
        if (descriptionTextView != null && bundle.containsKey(DESCRIPTION_VALUE)) {
            setDescription(bundle.getString(DESCRIPTION_VALUE))
        }

        if (itemList != null) {
            setList(itemList, bundle.getString(TOOLBAR_TITLE))
        } else if (bundle.containsKey(SELECTOR_LIST)) {
            val serializable = bundle.getSerializable(SELECTOR_LIST)
            if (serializable is SelectorList<*>) {
                setList(serializable as SelectorList<*>?, bundle.getString(TOOLBAR_TITLE))
            }
        }
    }

    open fun init(context: Context, attributes: AttributeSet?) {
        init(context, attributes, IntArray(0), null)
    }

    fun init(context: Context, attributes: AttributeSet?, styleableResource: IntArray, inputViewAttributes: InputViewAttributes?) {
        initViews()
        setOnClickListener(this)
        if (inputViewAttributes == null) {
            return
        }
        val typedArray = context.obtainStyledAttributes(attributes, styleableResource)
        inputViewAttributes.apply {
            setTitle(titleResourceId, typedArray)
            setHint(hintResourceId, typedArray)
            setDescription(descriptionId, typedArray)
            setError(errorTextId, typedArray)
            setInputType(inputType, typedArray)
            if (maskId != -1) {
                setMask(maskId, typedArray)
            }
            setSelectorViewType(selectorViewType, typedArray)
            if (isEditableId != -1 && typedArray.hasValue(isEditableId)) {
                setValueEditable(isEditableId, typedArray)
            }
            if (allowedCharacters != -1) {
                setAllowedCharacters(allowedCharacters, typedArray)
            }
            setIcon(imageResourceId, typedArray)
            setMaxLength(maxLength, typedArray)
        }
        typedArray.recycle()
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        setViewEnabled(enabled)
    }

    fun setViewEnabled(enabled: Boolean) {
        isViewEnabled = enabled
        if (!isViewEnabled) {
            valueEditText?.setTextColor(ContextCompat.getColor(context, R.color.input_view_text_color))
            if (selectorViewType === SelectorType.LONG_LIST || selectorViewType === SelectorType.SEARCHABLE_LONG_LIST) {
                setImageViewImage(R.drawable.ic_arrow_right_dark_disabled)
            }
        } else {
            valueEditText?.setTextColor(ContextCompat.getColor(context, R.color.graphite_light_theme_item_color))
            if (selectorViewType === SelectorType.LONG_LIST || selectorViewType === SelectorType.SEARCHABLE_LONG_LIST) {
                setImageViewImage(R.drawable.ic_arrow_right_dark)
            }
        }
    }

    open fun initViews() {
        animClick = AnimationUtils.loadAnimation(context, R.anim.click_c)
        parentConstraintLayout = findViewById(R.id.parent_constraint_layout)
        displayRecyclerView = findViewById(R.id.recycler_view)
        selectedValueTextView = findViewById(R.id.selected_value_text_view)
        descriptionTextView = findViewById(R.id.description_text_view)
        titleTextView = findViewById(R.id.title_text_view)
        valueEditText = findViewById(R.id.value_edit_text)
        errorTextView = findViewById(R.id.error_text_view)
        iconImageView = findViewById(R.id.icon_view)
        dividerView = findViewById(R.id.divider_view)
        bottomDividerView = findViewById(R.id.bottom_divider)
        valueEditText?.isSaveEnabled = false
        descriptionTextView?.isSaveEnabled = false

        setImeOptions(EditorInfo.IME_ACTION_NEXT)
    }

    private fun setAllowedCharacters(index: Int, typedArray: TypedArray) {
        val allowedCharacters = typedArray.getString(index)
        if (allowedCharacters != null) {
            valueEditText?.apply {
                if (inputType == InputType.TYPE_CLASS_NUMBER || inputType == InputType.TYPE_NUMBER_FLAG_DECIMAL || inputType == InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL) {
                    keyListener = DigitsKeyListener.getInstance(allowedCharacters)
                } else {
                    val allowedCharactersInputFilter = InputFilter { source: CharSequence, start: Int, end: Int, _: Spanned?, _: Int, _: Int ->
                        var i = start
                        while (i < end) {
                            if (!allowedCharacters.contains(source[i].toString())) {
                                return@InputFilter ""
                            }
                            i++
                        }
                        null
                    }
                    val otherFilters = filters
                    val inputFilters = otherFilters?.let { mergeInputFilters(it, arrayOf(allowedCharactersInputFilter)) }
                    filters = inputFilters
                }
            }
        }
    }

    private fun setMask(resourceId: Int, typedArray: TypedArray) {
        val maskText = typedArray.getString(resourceId)
        maskText?.let { applyMask(it) }
    }

    fun applyMask(maskString: String) {
        valueEditText?.setMask(maskString)
    }

    fun setInputType(inputType: Int) {
        valueEditText?.inputType = inputType
    }

    private fun setIcon(resourceId: Int, typedArray: TypedArray) {
        val imageResourceId = typedArray.getResourceId(resourceId, -1)
        if (imageResourceId != -1) {
            setIconViewImage(imageResourceId)
        }
    }

    private fun setInputType(index: Int, typedArray: TypedArray) {
        val inputType = typedArray.getInt(index, InputType.TYPE_CLASS_TEXT)
        valueEditText?.inputType = inputType
        valueEditText?.typeface = Typeface.SANS_SERIF
    }

    private fun setMaxLength(index: Int, typedArray: TypedArray) {
        val maxLength = typedArray.getInt(index, DEFAULT_MAX_LENGTH)
        if (valueEditText != null && maxLength > DEFAULT_MAX_LENGTH) {
            val existingInputFilters = valueEditText!!.filters
            val newFilters = arrayOf<InputFilter>(LengthFilter(maxLength))
            val inputFilters = existingInputFilters?.let { mergeInputFilters(it, newFilters) }
            valueEditText!!.filters = inputFilters
        }
    }

    private fun mergeInputFilters(firstInputFilters: Array<InputFilter>, secondInputFilters: Array<InputFilter>): Array<InputFilter> {
        val inputFilters = ArrayList<InputFilter>()
        Collections.addAll(inputFilters, *firstInputFilters)
        Collections.addAll(inputFilters, *secondInputFilters)
        return inputFilters.toTypedArray()
    }

    private fun setError(resourceId: Int, typedArray: TypedArray) {
        val errorText = typedArray.getString(resourceId)
        if (errorText != null) {
            errorTextView?.text = errorText
        } else {
            errorTextView?.visibility = View.GONE
        }
    }

    private fun setDescription(resourceId: Int, typedArray: TypedArray) {
        val descriptionText = typedArray.getString(resourceId)
        if (!descriptionText.isNullOrEmpty()) {
            descriptionTextView?.text = descriptionText
            descriptionTextView?.visibility = View.VISIBLE
        } else {
            descriptionTextView?.visibility = View.GONE
        }
    }

    private fun setHint(resourceId: Int, typedArray: TypedArray) {
        val hintText = typedArray.getString(resourceId)
        valueEditText?.hint = hintText
    }

    fun setSelectorViewType(selectorViewType: SelectorType?) {
        this.selectorViewType = selectorViewType
    }

    private fun setSelectorViewType(resourceId: Int, typedArray: TypedArray) {
        val enumValue = typedArray.getInt(resourceId, 0)
        selectorViewType = SelectorType.values()[enumValue]
        when (selectorViewType) {
            SelectorType.NONE -> {
                iconImageView?.visibility = View.GONE
                setValueEditable(true)
            }
            SelectorType.SHORT_LIST -> {
                setValueEditable(false)
                setImageViewImage(R.drawable.ic_arrow_right_dark)
                rotateArrowDown()
            }
            SelectorType.LONG_LIST -> {
                setValueEditable(false)
                setImageViewImage(R.drawable.ic_arrow_right_dark)
            }
            SelectorType.SEARCHABLE_LONG_LIST -> {
                setValueEditable(false)
                setImageViewImage(R.drawable.ic_arrow_right_dark)
            }
            SelectorType.DATE -> {
            }
            SelectorType.CURRENCY_DECIMAL -> {
                setValueEditable(true)
                setUpCurrencyInput(true)
            }
            SelectorType.CURRENCY -> {
                setValueEditable(true)
                setUpCurrencyInput(false)
            }
            SelectorType.CONTACT -> {
                setValueEditable(true)
                if (iconImageView != null) {
                    findViewById<View>(R.id.contactPlaceHolder).visibility = View.INVISIBLE
                    setImageViewImage(R.drawable.ic_beneficiary_dark_add)
                }
            }
            SelectorType.GENERIC_SELECTOR -> {
                setValueEditable(false)
                setImageViewImage(R.drawable.ic_arrow_right_dark)
            }
        }
    }

    private fun setUpCurrencyInput(allowDecimals: Boolean) {
        iconImageView?.visibility = View.GONE
        valueEditText?.apply {
            if (allowDecimals) {
                inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            } else {
                inputType = InputType.TYPE_CLASS_NUMBER
                keyListener = DigitsKeyListener.getInstance("0123456789 $currency")
            }
            currencyTextWatcher.let { removeTextChangedListener(it) }
            currencyTextWatcher = CurrencyTextWatcher(valueEditText, currency)
            addTextChangedListener(currencyTextWatcher)
        }
    }

    private fun setTitle(resourceId: Int, typedArray: TypedArray) {
        if (resourceId != -1) {
            val title = typedArray.getString(resourceId)
            if (title != null) {
                titleTextView?.text = title
                titleTextView?.visibility = View.VISIBLE
            } else {
                titleTextView?.visibility = View.GONE
            }
        }
    }

    fun setTitleText(titleText: String?) {
        if (titleText != null) {
            titleTextView?.text = titleText
            titleTextView?.visibility = View.VISIBLE
        } else {
            titleTextView?.visibility = View.GONE
        }
    }

    fun setTitleText(@StringRes titleTextResId: Int) {
        setTitleText(context.getString(titleTextResId))
    }

    fun setTitleTextStyle(@StyleRes textStyle: Int) {
        titleTextView?.let { TextViewCompat.setTextAppearance(it, textStyle) }
    }

    fun setHintText(hintText: String?) {
        valueEditText?.hint = hintText
    }

    fun setHintText(@StringRes hintTextResId: Int) {
        setHintText(context.getString(hintTextResId))
    }

    private fun setValueEditable(resourceId: Int, typedArray: TypedArray) {
        val isEditable = typedArray.getBoolean(resourceId, false)
        setValueEditable(isEditable)
    }

    fun setValueEditable(isEditable: Boolean) {
        valueEditText?.let { maskedEditText ->
            if (!isEditable) {
                maskedEditText.setOnClickListener(this)
                maskedEditText.isLongClickable = false
            }
            maskedEditText.isFocusable = isEditable
            maskedEditText.isFocusableInTouchMode = isEditable

            if (isEditable) {
                setFocusAndSelectionBehaviour()
            }
        }
    }

    private fun setFocusAndSelectionBehaviour() {
        valueEditText?.onFocusChangeListener = OnFocusChangeListener { _: View?, hasFocus: Boolean ->
            valueEditText?.setSelection(valueEditText?.text!!.length)
            if (hasFocus) {
                valueEditText?.setTextColor(ContextCompat.getColor(context, R.color.dark_grey_light_theme_text_color))
                bottomDividerView?.setBackgroundColor(ContextCompat.getColor(context, R.color.bright_purple))
            } else {
                bottomDividerView?.setBackgroundColor(ContextCompat.getColor(context, R.color.silver))
            }
        }
    }

    fun showDescription(isShown: Boolean) {
        descriptionTextView?.visibility = if (isShown) View.VISIBLE else View.GONE
        if (isShown) {
            errorTextView?.visibility = View.GONE
        }
    }

    fun hasError(): Boolean {
        return errorTextView?.visibility == View.VISIBLE
    }

    fun hideError() {
        showError(false)
    }

    @JvmOverloads
    fun showError(isShown: Boolean = true) {
        errorTextView?.visibility = if (isShown) View.VISIBLE else View.GONE
        if (isShown) {
            descriptionTextView?.visibility = View.GONE
        }
    }

    fun setImageViewVisibility(visibility: Int) {
        iconImageView?.visibility = visibility
    }

    fun setImageViewImage(@DrawableRes image: Int) {
        iconImageView?.setImageResource(image)
    }

    fun setDescription(description: String?) {
        errorTextView?.visibility = View.GONE
        descriptionTextView?.visibility = View.VISIBLE
        descriptionTextView?.text = description
    }

    fun setError(error: String?) {
        descriptionTextView?.visibility = View.GONE
        errorTextView?.visibility = View.VISIBLE
        errorTextView?.text = error
        valueEditText?.requestFocus()
        focusAndShakeError()
    }

    fun focusAndShakeError() {
        AnimationHelper.shakeShakeAnimate(this)
        focusBottomOfView()
    }

    fun setError(@StringRes error: Int) {
        setError(context.getString(error))
    }

    fun clearError() {
        errorTextView?.visibility = View.GONE
    }

    fun clearDescription() {
        descriptionTextView?.visibility = View.GONE
    }

    private fun focusBottomOfView() {
        if (rootView.findViewById<View>(R.id.scrollView) is ScrollView) {
            val scrollView = rootView.findViewById<ScrollView>(R.id.scrollView)
            scrollView.post { scrollView.scrollTo(0, this@SelectorView.y.toInt()) }
        } else if (rootView.findViewById<View>(R.id.scrollView) is NestedScrollView) {
            val nestedScrollView: NestedScrollView = rootView.findViewById(R.id.scrollView)
            nestedScrollView.post { nestedScrollView.scrollTo(0, this@SelectorView.y.toInt()) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun setList(objects: SelectorList<*>?, toolbarTitle: String? = "") {
        this.toolbarTitle = toolbarTitle
        itemList = objects as SelectorList<T>
        setIconViewImage(-1)
        setUpAdapter()
    }

    private fun setUpAdapter() {
        if (selectorViewType == SelectorType.SHORT_LIST) {
            dividerView?.visibility = GONE
        }

        if (selectorViewType == SelectorType.LONG_LIST || selectorViewType == SelectorType.SEARCHABLE_LONG_LIST) {
            parentConstraintLayout?.visibility = GONE
        } else {
            selectorViewAdapter = if (contentDescription.isNullOrEmpty()) {
                SelectorViewAdapter(adapterStringList, this)
            } else {
                SelectorViewAdapter(adapterStringList, this, contentDescription)
            }

            parentConstraintLayout?.visibility = VISIBLE
            viewAnimationShortList = ViewAnimation(parentConstraintLayout)

            displayRecyclerView?.layoutManager = LinearLayoutManager(context)
            displayRecyclerView?.adapter = selectorViewAdapter
            selectorViewAdapter?.notifyDataSetChanged()

            parentConstraintLayout?.viewTreeObserver?.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewAnimationShortList?.collapseView(0)
                    displayRecyclerView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    fun setList(objects: SelectorList<*>?, toolbarTitle: String?, contentDescriptions: List<String>?) {
        contentDescription = contentDescriptions
        setList(objects, toolbarTitle)
    }

    fun setContentDescriptionsForList(contentDescriptions: List<String>?) {
        contentDescription = contentDescriptions
    }

    fun setIconViewImage(resourceId: Int) {
        iconImageView?.setImageResource(if (resourceId == -1) R.drawable.ic_arrow_right_dark else resourceId)
    }

    private fun rotateArrowDown() {
        iconImageView?.rotation = 90f
    }

    private fun showDialogFragment() {
        ListItemDisplayDialogFragment.valueSelectedObserver = this
        itemList?.let {
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            val dialogFragment = ListItemDisplayDialogFragment.newInstance(it, selectedIndex, toolbarTitle.toString())
            dialogFragment.show(fragmentManager, ListItemDisplayDialogFragment::class.java.simpleName)
        }
    }

    private fun showSearchableDialogFragment() {
        SearchableListItemDialogFragment.valueSelectedObserver = this
        itemList?.let {
            val fragmentManager = (context as AppCompatActivity).supportFragmentManager.beginTransaction()
            val dialogFragment = SearchableListItemDialogFragment.newInstance(it, selectedIndex, toolbarTitle.toString())
            dialogFragment.show(fragmentManager, SearchableListItemDialogFragment::class.java.simpleName)
        }
    }

    fun findItemIndex(item: String): Int {
        itemList?.let { itemList ->
            itemList.forEachIndexed { index, it ->
                if (it.displayValue == item) {
                    return index
                }
            }
        }
        return -1
    }

    fun addValueViewTextWatcher(textWatcher: TextWatcher?) {
        valueEditText?.addTextChangedListener(textWatcher)
    }

    fun removeValueViewTextWatcher(textWatcher: TextWatcher?) {
        valueEditText?.removeTextChangedListener(textWatcher)
    }

    fun setValueViewFocusChangedListener(focusChangedListener: OnFocusChangeListener?) {
        valueEditText?.setSelection(valueEditText?.text!!.length)
        valueEditText?.onFocusChangeListener = focusChangedListener
    }

    fun setValueViewEditorActionListener(viewEditorActionListener: OnEditorActionListener?) {
        valueEditText?.setOnEditorActionListener(viewEditorActionListener)
    }

    fun focusValueView() {
        valueEditText?.requestFocus()
    }

    private fun populateView(friendlyName: String?) {
        selectedValueTextView?.let { it.text = friendlyName } ?: valueEditText?.setText(friendlyName)
    }

    fun setSelectedValueCursorPosition(position: Int) {
        valueEditText?.setSelection(position)
    }

    fun clear() {
        selectedValue = ""
    }

    fun clearSelectedIndexAndValue() {
        selectedIndex = -1
        selectedValue = ""
    }

    open var selectedValue: String
        get() {
            selectedValueTextView?.let { return it.text.toString() } ?: valueEditText?.let { return it.text.toString() }
            return ""
        }
        set(value) {
            populateView(value)
            originalValue = value
        }

    fun setCurrency(currencySymbol: String) {
        isCustomCurrency = true
        currency = currencySymbol
        setUpCurrencyInput(true)
    }

    val selectedValueUnmasked: String
        get() = if (valueEditText != null) {
            if ((selectorViewType === SelectorType.CURRENCY || selectorViewType === SelectorType.CURRENCY_DECIMAL) && valueEditText!!.unMaskedText.isNotEmpty()) {
                valueEditText!!.unMaskedText.removeStringCurrencyDefaultZero(currency)
            } else {
                valueEditText!!.unMaskedText
            }
        } else ""

    val selectedItem: T?
        get() = if (selectedIndex > -1 && selectedIndex < itemList?.size!!) {
            itemList!![selectedIndex]
        } else null

    private val adapterStringList: List<String>
        get() {
            val tempList: MutableList<String> = ArrayList()
            itemList?.let { itemList ->
                itemList.forEach {
                    tempList.add(it.displayValue ?: "")
                }
            }
            return tempList
        }

    override fun onValueSelected(index: Int) {
        selectedIndex = index
        clearError()
        itemSelectionInterface?.onItemClicked(index)
        previousIndex = index
        ListItemDisplayDialogFragment.valueSelectedObserver = null
        SearchableListItemDialogFragment.valueSelectedObserver = null
    }

    override fun onItemClicked(index: Int) {
        selectedIndex = index
        itemSelectionInterface?.onItemClicked(index)
        previousIndex = index
        if (displayRecyclerView != null && !isAnimating) {
            hideRecyclerView()
        }
    }

    fun simulateIndexSelected(index: Int) {
        onValueSelected(index)
    }

    fun preventDoubleClick(view: View) {
        view.isClickable = false
        view.postDelayed({ view.isClickable = true }, TWO_SECOND_DELAY.toLong())
    }

    override fun onClick(v: View) {
        if (!isViewEnabled) {
            return
        }
        preventDoubleClick(v)
        triggerListActivity()
    }

    fun triggerListActivity() {
        if (selectorViewType === SelectorType.LONG_LIST) {
            showDialogFragment()
        } else if (selectorViewType === SelectorType.SEARCHABLE_LONG_LIST) {
            showSearchableDialogFragment()
        } else if (selectorViewType === SelectorType.SHORT_LIST && selectorViewAdapter != null && !isAnimating) {
            if (parentConstraintLayout?.visibility == View.GONE) {
                showRecyclerView()
            } else {
                hideRecyclerView()
            }
        }
    }

    override fun setOnClickListener(onClickListener: OnClickListener?) {
        valueEditText?.setOnClickListener(onClickListener)
        super.setOnClickListener(onClickListener)
    }

    private fun hideRecyclerView() {
        val animation = viewAnimationShortList?.collapseView(500)
        animation?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                isAnimating = true
            }

            override fun onAnimationEnd(animation: Animation) {
                isAnimating = false
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun showRecyclerView() {
        selectorViewAdapter?.notifyDataSetChanged()
        viewAnimationShortList?.expandView(500)?.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                isAnimating = true
                hideErrorAndDescriptionView()
            }

            override fun onAnimationEnd(animation: Animation) {
                isAnimating = false
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun hideErrorAndDescriptionView() {
        descriptionTextView?.visibility = View.GONE
        errorTextView?.visibility = View.GONE
    }

    fun setImeOptions(imeFlag: Int) {
        valueEditText?.imeOptions = imeFlag
    }

    fun setImageViewOnClickListener(iconViewClickListener: OnClickListener) {
        iconImageView?.apply {
            isClickable = true
            setOnClickListener { view: View? ->
                startAnimation(animClick)
                iconViewClickListener.onClick(view)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setImageViewOnTouchListener(onTouchListener: OnTouchListener) {
        iconImageView?.apply {
            isClickable = true
            setOnTouchListener { view: View?, event: MotionEvent? ->
                startAnimation(animClick)
                onTouchListener.onTouch(view, event)
            }
        }
    }

    fun hasValueChanged(): Boolean {
        val nonNullOriginalValue = originalValue ?: ""
        return nonNullOriginalValue != selectedValue
    }

    open val editText: EditText?
        get() = valueEditText

    fun setEditTextContentDescription(contentDescription: String) {
        editText?.contentDescription = contentDescription
    }

    fun optionalValidate(): Boolean = if (this.selectedValue.isBlank()) true else this.validate(validators)

    open fun validate(): Boolean {
        return this.validate(validators)
    }

    fun validateIfVisible(): Boolean = if (visibility == View.VISIBLE) validate() else true
}