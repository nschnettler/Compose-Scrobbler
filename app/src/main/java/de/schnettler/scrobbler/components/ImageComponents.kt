package de.schnettler.scrobbler.components

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.*
import androidx.ui.core.ContentScale
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Image
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.*
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

@Composable
fun NetworkImageComponent(url: String) {
    var image by state<ImageAsset?> { null }
    var drawable by state<Drawable?> { null }
    onCommit(url) {
        val picasso = Picasso.get()
        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                // TODO(lmr): we could use the drawable below
                drawable = placeHolderDrawable
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                drawable = errorDrawable
                println("Error Loading Image")
                e?.printStackTrace()
            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                println("Bitmap Loaded")
                image = bitmap?.asImageAsset()
            }
        }
        picasso
            .load(url)
            .into(target)

        onDispose {
            image = null
            drawable = null
            picasso.cancelRequest(target)
        }
    }

    image?.let {asset ->
        Box(gravity = ContentGravity.Center) {
            Image(asset = asset, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
    }
}