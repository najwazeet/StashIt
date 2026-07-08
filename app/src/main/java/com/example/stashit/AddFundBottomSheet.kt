package com.example.stashit

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.stashit.databinding.BottomsheetAddFundBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddFundBottomSheet(
    private val allocationName: String = "Tiket Pesawat",
    private val onSave: (amount: Long, dateLabel: String) -> Unit = { _, _ -> }
) : BottomSheetDialogFragment() {

    private var _binding: BottomsheetAddFundBinding? = null
    private val binding get() = _binding!!

    private var rawAmount: String = ""
    private var selectedDateLabel: String = "Hari ini"

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetAddFundBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvAllocationTarget.text = "untuk $allocationName"

        val sdf = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
        selectedDateLabel = "Hari ini, ${sdf.format(Calendar.getInstance().time)}"
        binding.btnDatePicker.text = selectedDateLabel

        setupNumpad()
        setupDatePicker()
        updateAmountDisplay()

        binding.btnSave.setOnClickListener {
            val amount = rawAmount.toLongOrNull() ?: 0L
            if (amount <= 0L) {
                Toast.makeText(requireContext(), "Masukkan nominal terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onSave(amount, selectedDateLabel)
            dismiss()
        }
    }

    private fun setupNumpad() {
        val numberKeys = mapOf(
            binding.key1 to "1", binding.key2 to "2", binding.key3 to "3",
            binding.key4 to "4", binding.key5 to "5", binding.key6 to "6",
            binding.key7 to "7", binding.key8 to "8", binding.key9 to "9",
            binding.key0 to "0", binding.key000 to "000"
        )

        numberKeys.forEach { (view, value) ->
            view.setOnClickListener {
                if (rawAmount.length < 12) {
                    rawAmount += value
                    updateAmountDisplay()
                }
            }
        }

        binding.keyBackspace.setOnClickListener {
            if (rawAmount.isNotEmpty()) {
                rawAmount = rawAmount.dropLast(1)
                updateAmountDisplay()
            }
        }
    }

    private fun setupDatePicker() {
        binding.btnDatePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    calendar.set(year, month, day)
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
                    selectedDateLabel = sdf.format(calendar.time)
                    binding.btnDatePicker.text = selectedDateLabel
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun updateAmountDisplay() {
        val display = if (rawAmount.isEmpty()) "0" else {
            val number = rawAmount.toLongOrNull() ?: 0L
            NumberFormat.getNumberInstance(Locale("in", "ID")).format(number)
        }
        binding.tvAmountDisplay.text = display
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}