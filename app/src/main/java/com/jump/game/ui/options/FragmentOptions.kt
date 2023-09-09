package com.jump.game.ui.options

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.jump.game.R
import com.jump.game.databinding.FragmentOptionsBinding
import com.jump.game.domain.DB
import com.jump.game.ui.other.ViewBindingFragment

class FragmentOptions: ViewBindingFragment<FragmentOptionsBinding>(FragmentOptionsBinding::inflate) {
    private val sp by lazy {
        DB(requireContext())
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSelections()

        binding.select1.setOnClickListener {
            sp.selectSymbol(1)
            setSelections()
        }

        binding.select2.setOnClickListener {
            sp.selectSymbol(2)
            setSelections()
        }

        binding.select3.setOnClickListener {
            sp.selectSymbol(3)
            setSelections()
        }

        binding.select4.setOnClickListener {
            sp.selectSymbol(4)
            setSelections()
        }

        binding.select5.setOnClickListener {
            sp.selectSymbol(5)
            setSelections()
        }

        binding.select6.setOnClickListener {
            sp.selectSymbol(6)
            setSelections()
        }

        binding.ok.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setSelections() {
        binding.apply {
            if (sp.getSelectedSymbol() == 1) {
                binding.select1.setImageDrawable(null)
            } else {
                binding.select1.setImageResource(R.drawable.select)
            }

            if (sp.getSelectedSymbol() == 2) {
                binding.select2.setImageDrawable(null)
            } else {
                binding.select2.setImageResource(R.drawable.select)
            }

            if (sp.getSelectedSymbol() == 3) {
                binding.select3.setImageDrawable(null)
            } else {
                binding.select3.setImageResource(R.drawable.select)
            }

            if (sp.getSelectedSymbol() == 4) {
                binding.select4.setImageDrawable(null)
            } else {
                binding.select4.setImageResource(R.drawable.select)
            }

            if (sp.getSelectedSymbol() == 5) {
                binding.select5.setImageDrawable(null)
            } else {
                binding.select5.setImageResource(R.drawable.select)
            }

            if (sp.getSelectedSymbol() == 6) {
                binding.select6.setImageDrawable(null)
            } else {
                binding.select6.setImageResource(R.drawable.select)
            }
        }
    }
}