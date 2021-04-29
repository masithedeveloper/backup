package com.money.randing.ui.people.detail

import android.content.ClipData
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.money.randing.NavGraphDirections
import com.money.randing.R
import com.money.randing.constant.K
import com.money.randing.domain.model.Movement
import com.money.randing.domain.model.Person
import com.money.randing.error.AppException
import com.money.randing.ui.common.SwipeItemCallback
import com.money.randing.ui.common.showSnackBar
import com.money.randing.util.ImagePicker
import com.money.randing.util.showGeneralDialog
import com.money.randing.util.toCurrencyString
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.money.randing.databinding.FragmentPersonDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.absoluteValue

@AndroidEntryPoint
class PersonDetailFragment : Fragment() {

    private var _binding: FragmentPersonDetailBinding? = null
    private val binding get() = _binding!!
    private val args: PersonDetailFragmentArgs by navArgs()
    private val viewModel: PersonDetailViewModel by viewModels()
    private lateinit var movementAdapter: MovementAdapter
    private var uiListenersJob: Job? = null
    private lateinit var imagePicker: ImagePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        movementAdapter = MovementAdapter().apply {
            setOnClickListener(::navigateToEditMovement)
            setOnLongClickListener(::confirmMovementCanBePaid)
        }
        imagePicker = ImagePicker(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMovements.adapter = movementAdapter

        // Swipe to delete
        val itemCallback = SwipeItemCallback<Movement>(requireContext()).apply {
            adapter = this@PersonDetailFragment.movementAdapter
            delegate = viewModel
        }
        val swipeTouchHelper = ItemTouchHelper(itemCallback)
        swipeTouchHelper.attachToRecyclerView(binding.rvMovements)

        // Scroll to top
        movementAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (itemCount == 1) binding.rvMovements.scrollToPosition(0)
            }
        })

        // Actions
        binding.btnNewMovement.setOnClickListener { navigateToNewMovement() }

        binding.iconButtonShare.setOnClickListener { shareSummary() }

        binding.iconButtonEdit.setOnClickListener { navigateToEditPerson() }

        binding.iconButtonSettle.setOnClickListener { settleAccount() }

        // Observers
        uiListenersJob = viewLifecycleOwner.lifecycleScope.launch {
            launch {
                renderPerson()
            }
            launch {
                renderBalance()
            }
            launch {
                renderMovementList()
            }
        }

        viewModel.lastItemRemoved.observe(viewLifecycleOwner) { event ->
            event.getContent()?.let { item ->
                val contextView = binding.btnNewMovement
                val message = resources.getQuantityString(R.plurals.items_deleted, 1, 1)
                Snackbar.make(contextView, message, Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) {
                        viewModel.undoItemRemoval(item)
                    }
                    .show()
            }
        }
    }

    private suspend fun renderPerson() {
        viewModel.requestPerson(args.personId).collect { person ->
            person?.let {
                binding.tvName.text = person.name
                binding.iconButtonDelete.setOnClickListener {
                    showDeleteDialog(person)
                }
                binding.imageCard.setOnClickListener { changePersonImage(person) }
            }

            if (person?.picture != null) {
                binding.image.setImageBitmap(person.picture)
            } else {
                val d =
                    ResourcesCompat.getDrawable(
                        requireContext().resources,
                        R.drawable.avatar_placeholder,
                        null
                    )
                binding.image.setImageDrawable(d)
            }
        }
    }

    private suspend fun renderBalance() {
        viewModel.requestBalance(args.personId).collect { balance ->
            binding.tvTotal.text = balance?.absoluteValue.toCurrencyString()
            if (balance != null) {
                binding.tvTotalLabel.text = when {
                    balance > 0 -> {
                        getString(R.string.owe_me)
                    }
                    balance < 0 -> {
                        getString(R.string.i_owe)
                    }
                    else -> ""
                }
            } else {
                binding.tvTotalLabel.text = ""
            }
        }
    }

    private suspend fun renderMovementList() {
        viewModel.requestMovements(args.personId).collect { data ->
            val isEmpty = data.isEmpty()
            binding.rvMovements.isVisible = !isEmpty
            binding.tvEmpty.isVisible = isEmpty
            movementAdapter.submitList(data)
        }
    }

    private fun navigateToNewMovement() {
        findNavController().navigate(NavGraphDirections.actionGlobalCreateMovementFragment(args.personId.toString()))
    }

    private fun shareSummary() {
        val bitmap = binding.contentContainer.drawToBitmap()
        lifecycleScope.launch(Dispatchers.IO) {
            cacheBitmap(bitmap)?.let {
                shareImageUri(it)
            }
        }
    }

    private fun cacheBitmap(bitmap: Bitmap): Uri? {
        val imagePath = File(requireContext().cacheDir, "images")
        var uri: Uri? = null
        try {
            imagePath.mkdirs()
            val file = File(imagePath, "shared_image.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream)
            uri = FileProvider.getUriForFile(requireContext(), K.FILE_PROVIDER_AUTHORITIES, file)
        } catch (e: IOException) {
            Log.d(
                "cacheBitmapToGetUri",
                "IOException while trying to write file for sharing: ${e.message}"
            )
        }

        return uri
    }

    private fun shareImageUri(uri: Uri) {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, uri)
            clipData = ClipData.newUri(requireContext().contentResolver, null, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    private fun navigateToEditPerson() {
        findNavController().navigate(PersonDetailFragmentDirections.actionGlobalCreatePersonFragment(args.personId.toString()))
    }

    private fun showDeleteDialog(person: Person) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.delete)
            .setMessage(getString(R.string.delete_person, person.name))
            .setNeutralButton(R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.delete_all) { _, _ ->
                lifecycleScope.launch {
                    uiListenersJob?.cancel()
                    viewModel.deletePerson(person.id)
                    findNavController().popBackStack()
                }
            }
            .setNegativeButton(R.string.delete_history_only) { _, _ ->
                lifecycleScope.launch {
                    val itemsDeleted = viewModel.deleteHistory(person.id)
                    val message = resources.getQuantityString(
                        R.plurals.items_deleted,
                        itemsDeleted,
                        itemsDeleted
                    )
                    Snackbar.make(binding.coordinatorLayout, message, Snackbar.LENGTH_LONG).show()
                }
            }
            .show()
    }

    private fun navigateToEditMovement(movement: Movement) {
        if (movement.type.isSettled) {
            showSnackBar(getString(R.string.can_not_edit))
        } else {
            val action =
                PersonDetailFragmentDirections.actionGlobalCreateMovementFragment(
                    args.personId.toString(),
                    movement.id.toString()
                )
            findNavController().navigate(action)
        }
    }

    private fun confirmMovementCanBePaid(movement: Movement) {
        if (movement.type.isLoan) {
            showGeneralDialog(
                R.string.pay_loan_title,
                getString(R.string.pay_loan_confirm_message),
                R.string.pay_loan,
                onConfirm = {
                    viewModel.payLoan(movement)
                }
            )
        } else {
            showSnackBar(getString(R.string.can_not_pay_loan))
        }
    }

    private fun settleAccount() {
        lifecycleScope.launch {
            viewModel.requestBalance(args.personId).first()?.let { balance ->
                if (balance != 0.0) {
                    showGeneralDialog(
                        R.string.settle_account_title,
                        getString(R.string.settle_account_confirm_message),
                        R.string.settle_account_button,
                        R.string.cancel,
                        onConfirm = {
                            viewModel.settleAccount(
                                args.personId,
                                balance,
                                getString(R.string.settle_account_description)
                            )
                        }
                    )
                } else {
                    showSnackBar(getString(R.string.nothing_to_settle))
                }
            }
        }
    }

    private fun changePersonImage(person: Person, requestPermissionRationale: Boolean = true) {
        lifecycleScope.launch {
            try {
                val image = imagePicker.pickImage(requestPermissionRationale)
                if (image != null) {
                    viewModel.updatePerson(
                        person.copy(picture = image)
                    )
                }
            } catch (e: Exception) {
                when (e) {
                    AppException.ShouldRequestPermissionRationaleException -> {
                        showGeneralDialog(
                            R.string.permission_alert_title,
                            getString(R.string.gallery_permission_reason),
                            onConfirm = { changePersonImage(person, false) }
                        )
                    }
                    else -> {
                        showGeneralDialog(
                            R.string.permission_alert_title,
                            getString(R.string.gallery_permission_reason),
                        )
                    }
                }

            }
        }
    }
}