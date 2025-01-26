package org.koishi.launcher.h2co3.ui.download

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.databinding.ViewModScreenshotBinding
import org.koishi.launcher.h2co3core.mod.RemoteMod.Screenshot
import org.koishi.launcher.h2co3core.util.StringUtils
import org.koishi.launcher.h2co3library.component.view.H2CO3LauncherTextView


class RemoteModScreenshotAdapter(
    val context: Context,
    private val screenshotList: List<Screenshot>
) :
    RecyclerView.Adapter<RemoteModScreenshotAdapter.ScreenshotViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScreenshotViewHolder {
        return ScreenshotViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.view_mod_screenshot,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = screenshotList.size

    override fun onBindViewHolder(holder: ScreenshotViewHolder, position: Int) {
        holder.setScreenshot(screenshotList[position])
    }

    class ScreenshotViewHolder(val binding: ViewModScreenshotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setScreenshot(screenshot: Screenshot) {
            binding.retry.setOnClickListener { loadScreenshotImage(screenshot.imageUrl) }

            binding.screenshot.setImageDrawable(null)
            loadScreenshotImage(screenshot.imageUrl)

            binding.title.setVisibleIfNotBlank(screenshot.title)
            binding.description.setVisibleIfNotBlank(screenshot.description)
        }

        private fun loadScreenshotImage(imageUrl: String) {
            binding.apply {
                setLoading(true)
                Glide.with(screenshot)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .fitCenter()
                    .addListener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ): Boolean {
                            setLoading(false)
                            setFailed()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            setLoading(false)
                            return false
                        }
                    })
                    .into(screenshot)
            }
        }

        private fun setLoading(loading: Boolean) {
            binding.loading.visibility = if (loading) View.VISIBLE else View.GONE
            if (loading) binding.retry.visibility = View.GONE
        }

        private fun setFailed() {
            binding.retry.visibility = View.VISIBLE
        }

        private fun H2CO3LauncherTextView.setVisibleIfNotBlank(text: String?) {
            visibility = if (StringUtils.isNotBlank(text)) View.VISIBLE else View.GONE
            this.text = text
        }
    }
}