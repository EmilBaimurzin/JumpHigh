package com.jump.game.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jump.game.R
import com.jump.game.core.library.ViewBindingDialog
import com.jump.game.databinding.DialogOverBinding

class DialogOver: ViewBindingDialog<DialogOverBinding>(DialogOverBinding::inflate) {
    private val args: DialogOverArgs by navArgs()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Dialog_No_Border)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.setCancelable(false)
        dialog!!.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                findNavController().popBackStack(R.id.fragmentMain, false, false)
                true
            } else {
                false
            }
        }
        binding.score.text = args.scores.toString()

        binding.home.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
        }
        binding.restart.setOnClickListener {
            findNavController().popBackStack(R.id.fragmentMain, false, false)
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentJump)
        }
    }
}