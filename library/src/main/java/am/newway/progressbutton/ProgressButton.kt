package am.newway.progressbutton

import am.newway.progressbutton.databinding.ProgressButtonBinding
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout


class ProgressButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var binding: ProgressButtonBinding =
        ProgressButtonBinding.inflate(LayoutInflater.from(context), this, false)

    var maxProgress = 0
        set(value) {
            field = value
            onePercent =
                if (value > 0) (binding.root.layoutParams.width).toDouble() / value else 0.0
        }

    private var onePercent = 0.0

    private val fixedWidth = 300.dpToPx(context)
    private val fixedHeight = 70.dpToPx(context)
    private var finishState: FinishState = FinishState.BASIC

    init {
        context.obtainStyledAttributes(attrs, R.styleable.ProgressButton, 0, 0).apply {
            val text =
                getString(R.styleable.ProgressButton_text)?.takeIf { it.isNotBlank() }
                    ?: "Loading..."

            val textAppearance = getResourceId(R.styleable.ProgressButton_style, -1)

            val textSize = getDimension(
                R.styleable.ProgressButton_textSize,
                binding.basicText.textSize.toSp(context)
            )

            val cornerRadius = getDimension(
                R.styleable.ProgressButton_cornerRadius,
                binding.basicView.radius.toSp(context)
            )

            val strokeWidth = getDimension(
                R.styleable.ProgressButton_strokeWidth,
                binding.basicView.strokeWidth.toSp(context)
            ).toInt()

            val basicColor = getColor(R.styleable.ProgressButton_basicColor, -1)
            val accentColor = getColor(R.styleable.ProgressButton_accentColor, -1)
            finishState = getInt(R.styleable.ProgressButton_finishState, 0).toFinishState()

            with(binding) {
                basicText.text = text
                accentText.text = text

                basicText.textSize = textSize
                accentText.textSize = textSize

                basicView.radius = cornerRadius
                basicView.strokeWidth = strokeWidth

                if (textAppearance != -1) {
                    basicText.setTextAppearance(textAppearance)
                    accentText.setTextAppearance(textAppearance)
                }

                basicColor.takeIf { it != -1 }?.let {
                    basicText.setBackgroundColor(it)
                    accentText.setTextColor(it)
                    basicView.strokeColor = it
                }

                accentColor.takeIf { it != -1 }?.let {
                    basicText.setTextColor(it)
                    basicLayout.setBackgroundColor(it)
                }
            }
        }.recycle()

        addView(binding.root)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val desiredWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> minOf(fixedWidth, widthSize)
            MeasureSpec.UNSPECIFIED -> fixedWidth
            else -> fixedWidth
        }
        val desiredHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(fixedHeight, heightSize)
            MeasureSpec.UNSPECIFIED -> fixedHeight
            else -> fixedHeight
        }

        with(binding) {
            root.layoutParams = binding.root.layoutParams.apply {
                width = desiredWidth
                height = desiredHeight
            }
            basicText.layoutParams = binding.basicText.layoutParams.apply {
                width = desiredWidth
                height = desiredHeight
            }
            accentText.layoutParams = binding.accentText.layoutParams.apply {
                width = desiredWidth
                height = desiredHeight
            }
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        maxProgress = maxProgress
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)

        if (maxProgress == 0)
            maxProgress = 100
    }

    fun setProgress(progress: Int) {
        if (progress > maxProgress || progress < 0) return

        (context as Activity).runOnUiThread {
            val width = onePercent * progress
            binding.basicLayer.layoutParams.also {
                it.width = width.toInt()
                binding.basicLayer.layoutParams = it
            }
        }
    }

    fun cancelProgress() {
        when (finishState) {
            FinishState.BASIC -> setProgress(0)
            FinishState.ACCENT -> setProgress(maxProgress)
        }
    }

    fun setOnClickListener(click: () -> Unit) {
        binding.root.setOnClickListener {
            click()
        }
    }

    private fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

    private fun Number.toSp(context: Context): Float {
        return this.toFloat() / context.resources.displayMetrics.density
    }

    private fun Int.toFinishState(): FinishState {
        return when (this) {
            1 -> FinishState.ACCENT
            else -> FinishState.BASIC
        }
    }

    enum class FinishState {
        BASIC, ACCENT
    }
}