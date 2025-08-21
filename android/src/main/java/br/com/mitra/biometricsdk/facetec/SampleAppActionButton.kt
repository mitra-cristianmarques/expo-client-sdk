package br.com.mitra.biometricsdk.facetec

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import br.com.mitra.biometricsdk.R

class SampleAppActionButton : AppCompatButton {
    var enabledBackgroundColor: Int = Color.parseColor("#417FB2")
    var disabledBackgroundColor: Int = Color.parseColor("#66417FB2")
    var highlightedBackgroundColor: Int = Color.parseColor("#396E99")
    var titleTextColor: Int = Color.WHITE
    var titleLetterSpacing: Float = 0.05f
    var titleTypeface: Typeface? = Typeface.create("sans-serif-medium", Typeface.NORMAL)

    var mBackgroundDrawable: Drawable? = null
    var mTextColor: Int = 0
    var mBackgroundColor: Int = 0
    var mBorderColor: Int = 0
    var mBorderWidth: Int = 0
    var mCornerRadius: Int = 0
    var mTextSize: Int = 0
    var mTypeface: Typeface? = null

    var mStateTransitionTime: Int = 200

    var isHighlighted: Boolean = false
    var isSetup: Boolean = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @SuppressLint("ClickableViewAccessibility")
    fun setupButton(activity: SampleAppActivity) {
        if (isSetup) {
            return
        }
        isSetup = true

        mBackgroundColor = if (this.isEnabled()) enabledBackgroundColor else disabledBackgroundColor
        mTextColor = titleTextColor
        mBorderColor = Color.TRANSPARENT
        mBorderWidth = 0
        mCornerRadius = 8
        mTextSize = 20
        mTypeface = titleTypeface

        mBackgroundDrawable = ContextCompat.getDrawable(activity, R.drawable.sample_button_bg)
        if (mBackgroundDrawable != null && mBackgroundDrawable is GradientDrawable) {
            mBackgroundDrawable = mBackgroundDrawable.mutate()
            (mBackgroundDrawable as GradientDrawable).setColor(mBackgroundColor)
            (mBackgroundDrawable as GradientDrawable).setStroke(mBorderWidth, mBorderColor)
            (mBackgroundDrawable as GradientDrawable).setCornerRadius(
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    mCornerRadius.toFloat(),
                    Resources.getSystem().getDisplayMetrics()
                )
            )
        }
        setBackground(mBackgroundDrawable)

        this.setTypeface(mTypeface)
        this.setLetterSpacing(titleLetterSpacing)

        this.setOnClickListener(OnClickListener { view: View? ->
            // Set button background and text to normal color
            setHighlighted(false, true)
            when (getTag().toString()) {
                "livenessCheckButton" -> activity.onLivenessCheckPressed(this)
                "enrollUserButton" -> activity.onEnrollUserPressed(this)
                "verifyUserButton" -> activity.onVerifyUserPressed(this)
                "photoIDMatchButton" -> activity.onPhotoIDMatchPressed(this)
                "photoIDScanOnlyButton" -> activity.onPhotoIDScanOnlyPressed(this)
                "auditTrailButton" -> activity.onViewAuditTrailPressed(this)
                "designShowcaseButton" -> activity.onThemeSelectionPressed(this)
                else -> {}
            }
        })

        this.setOnTouchListener(OnTouchListener { v: View?, event: MotionEvent? ->
            if (!isEnabled()) {
                return@setOnTouchListener true
            }
            // If finger is pressed down within the button's bounds
            if (event!!.getAction() == MotionEvent.ACTION_DOWN) {
                // Set button background and text to highlight color
                setHighlighted(true, false)
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getX() < 0 || event.getX() > getWidth() || event.getY() < 0 || event.getY() > getHeight()) {
                // Set button background and text to normal color
                setHighlighted(false, true)
                // No further action
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                performClick()
            }
            true
        })

        updateButtonStyle(false)
    }

    fun updateButtonStyle(animated: Boolean) {
        if (!isSetup) {
            return
        }

        val transitionTime = if (animated) mStateTransitionTime else 0

        val backgroundColorFrom = mBackgroundColor
        val backgroundColorTo: Int
        if (!this.isEnabled()) {
            // Setup disabled config
            backgroundColorTo = disabledBackgroundColor
        } else if (isHighlighted) {
            // Setup highlighted config
            backgroundColorTo = highlightedBackgroundColor
        } else {
            // Setup normal/enabled config
            backgroundColorTo = enabledBackgroundColor
        }

        // Animate background color change
        val buttonBackgroundColorAnim =
            ValueAnimator.ofObject(ArgbEvaluator(), backgroundColorFrom, backgroundColorTo)
        buttonBackgroundColorAnim.setDuration(transitionTime.toLong())
        buttonBackgroundColorAnim.addUpdateListener(AnimatorUpdateListener { animator: ValueAnimator? ->
            if (mBackgroundDrawable == null) {
                return@addUpdateListener
            }
            mBackgroundColor = animator!!.getAnimatedValue() as Int
            (mBackgroundDrawable as GradientDrawable).setColor(mBackgroundColor)
            this.setBackground(mBackgroundDrawable)
            this.postInvalidate()
        })
        buttonBackgroundColorAnim.start()
    }

    fun setEnabled(enabled: Boolean, animated: Boolean) {
        if (this.isEnabled() == enabled) {
            return
        }
        super.setEnabled(enabled)

        updateButtonStyle(animated)
    }

    fun setHighlighted(highlighted: Boolean, animated: Boolean) {
        if (isHighlighted == highlighted || !this.isEnabled()) {
            return
        }
        isHighlighted = highlighted

        updateButtonStyle(animated)
    }
}
