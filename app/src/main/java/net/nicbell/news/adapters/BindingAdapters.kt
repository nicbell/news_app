package net.nicbell.news.adapters

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

@BindingAdapter("imageUrl")
fun setImageUrl(view: ImageView, oldImageUrl: String?, newImageUrl: String?) {
    if (newImageUrl != null && !TextUtils.equals(oldImageUrl, newImageUrl) && !TextUtils.isEmpty(newImageUrl)) {
        val picasso = Picasso.get()
        //picasso.setIndicatorsEnabled(true)

        picasso.load(newImageUrl)
        .fit()
            .centerCrop()
            .into(view)
    } else {
        view.setImageURI(null)
    }
}