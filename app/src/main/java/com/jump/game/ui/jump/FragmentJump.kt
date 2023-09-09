package com.jump.game.ui.jump

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.jump.game.R
import com.jump.game.core.library.GameFragment
import com.jump.game.databinding.FragmentJumpBinding
import com.jump.game.domain.DB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FragmentJump : GameFragment<FragmentJumpBinding>(FragmentJumpBinding::inflate) {
    private val viewModel: JumpViewModel by viewModels()
    private val sp by lazy {
        DB(requireContext())
    }
    private var moveScope = CoroutineScope(Dispatchers.Default)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setButtons()

        binding.symbol.setImageResource(
            when (sp.getSelectedSymbol()) {
                1 -> R.drawable.symbol01
                2 -> R.drawable.symbol02
                3 -> R.drawable.symbol03
                4 -> R.drawable.symbol04
                5 -> R.drawable.symbol05
                else -> R.drawable.symbol06
            }
        )

        binding.home.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.restart.setOnClickListener {
            findNavController().popBackStack()
            findNavController().navigate(R.id.action_fragmentMain_to_fragmentJump)
        }

        viewModel.platforms.observe(viewLifecycleOwner) {
            binding.platformLayout.removeAllViews()
            it.forEach { platform ->
                val platformView = ImageView(requireContext())
                val layoutParams = when (platform.platformLength) {
                    1 -> ViewGroup.LayoutParams(dpToPx(110), dpToPx(50))
                    2 -> ViewGroup.LayoutParams(dpToPx(190), dpToPx(50))
                    else -> ViewGroup.LayoutParams(dpToPx(360), dpToPx(50))
                }
                platformView.layoutParams = layoutParams
                platformView.setImageResource(
                    when (platform.platformLength) {
                        1 -> R.drawable.platform01
                        2 -> R.drawable.platform03
                        else -> R.drawable.platform02
                    }
                )
                platformView.x = platform.x
                platformView.y = platform.y
                binding.platformLayout.addView(platformView)
            }
        }

        viewModel.playerXY.observe(viewLifecycleOwner) {
            binding.symbol.x = it.x
            binding.symbol.y = it.y

            if (it.y >= xy.y && viewModel.gameState) {
                viewModel.stop()
                viewModel.stopAnother()
                viewModel.gameState = false
                findNavController().navigate(FragmentJumpDirections.actionFragmentJumpToDialogOver(viewModel.scores.value!!))
            }
        }

        viewModel.scores.observe(viewLifecycleOwner) {
            binding.score.text = it.toString()
        }

        startAction = {
            lifecycleScope.launch {
                binding.apply {
                    if (viewModel.platforms.value!!.isEmpty()) {
                        viewModel.initPlatforms(
                            platform1Y.y,
                            platform2Y.y,
                            platform3Y.y,
                            platform4Y.y,
                            platform5Y.y,
                            platform6Y.y,
                            xy.x.toInt(),
                            binding.symbol.height,
                            binding.symbol.width
                        )
                    }
                }
                if (viewModel.gameState) {
                    viewModel.start(
                        dpToPx(110),
                        dpToPx(190),
                        dpToPx(360),
                        xy.x.toInt(),
                        dpToPx(50),
                        binding.symbol.width,
                        binding.symbol.height
                    )
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setButtons() {
        binding.buttonLeft.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.isGoingLeft = true
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.isGoingLeft = true
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    viewModel.isGoingLeft = false
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }

        binding.buttonRight.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            viewModel.isGoingRight = true
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            viewModel.isGoingRight = true
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    viewModel.isGoingRight = false
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }

        binding.buttonUp.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    moveScope.launch {
                        while (true) {
                            if (viewModel.isStopped) {
                                viewModel.jump()
                            }
                            delay(2)
                        }
                    }
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    moveScope.launch {
                        while (true) {
                            if (viewModel.isStopped) {
                                viewModel.jump()
                            }
                            delay(2)
                        }
                    }
                    true
                }

                else -> {
                    moveScope.cancel()
                    moveScope = CoroutineScope(Dispatchers.Default)
                    false
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopAnother()
    }
}