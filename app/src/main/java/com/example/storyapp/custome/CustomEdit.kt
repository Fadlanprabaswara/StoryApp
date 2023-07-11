package com.example.storyapp.custome

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class CustomEdit : TextInputEditText {

    private var errorBg: Drawable? = null
    private var defaultBg: Drawable? = null
    private var isError: Boolean = false

    companion object {
        const val Email_From = 0x00000021
        const val Password_From = 0x00000081
    }

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    //bagian from akan merah ketika tidak sesuai

    private fun init() {
        errorBg = ContextCompat.getDrawable(context, R.drawable.bg_error)
        defaultBg = ContextCompat.getDrawable(context, R.drawable.bg_default)

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ErrorState(p0.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                ErrorState(p0.toString())
            }
        }

        addTextChangedListener(textWatcher)
    }


    //logika untuk eror paswword dan login

    private fun ErrorState(input: String) {
        when (inputType) {
            Email_From -> {
                if (!Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    error = context.getString(R.string.email_validation)
                    isError = true
                } else {
                    isError = false
                }
            }
            Password_From -> {
                isError = if (input.length < 8) {
                    setError(context.getString(R.string.password_length), null)
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        background = if (isError) {
            errorBg
        } else {
            defaultBg
        }
    }

}