package uz.gita.memorygame.presentation.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.gita.memorygame.R
import uz.gita.memorygame.databinding.DialogWinBinding

class WinDialog : DialogFragment(R.layout.dialog_win) {

    private val viewBinding by viewBinding(DialogWinBinding::bind)
    private var menuListener: (() -> Unit)? = null
    private var restartListener: (() -> Unit)? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.attributes?.windowAnimations = R.style.WinDialog
        dialog?.let {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog?.window?.setLayout(width, height)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(viewBinding) {
        isCancelable = false
        buttonMenu.setOnClickListener {
            menuListener?.invoke()
            dismiss()
        }
        buttonRestart.setOnClickListener {
            restartListener?.invoke()
            dismiss()
        }
    }

    fun setOnClickMenuListener(block: () -> Unit) {
        menuListener = block
    }

    fun setOnClickRestartListener(block: () -> Unit) {
        restartListener = block
    }

}