package com.dicoding.mycustomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import org.w3c.dom.Attr

class MyButton : AppCompatButton {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var txtColor: Int = 0
    private var enabledBackground: Drawable
    private var disableBackground: Drawable

    init {
        txtColor =
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }
}