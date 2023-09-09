package com.jump.game.ui.lobby

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.jump.game.R
import com.jump.game.core.library.GameFragment
import com.jump.game.databinding.FragmentLobbyBinding

class FragmentLobby : GameFragment<FragmentLobbyBinding>(FragmentLobbyBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.play.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentJump)
        }

        binding.options.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentOptions)
        }

        binding.exit.setOnClickListener {
            requireActivity().finish()
        }

        binding.privacyText.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    ACTION_VIEW,
                    Uri.parse("https://www.google.com")
                )
            )
        }
    }
}