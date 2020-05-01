package de.schnettler.scrobbler.components

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.*
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.Canvas
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Image
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.preferredHeightIn
import androidx.ui.unit.dp
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

@Composable
fun NetworkImageComponent(url: String,
                          modifier: Modifier = Modifier.fillMaxWidth() +
                                  Modifier.preferredHeightIn(maxHeight = 200.dp)) {
    // Source code inspired from - https://kotlinlang.slack.com/archives/CJLTWPH7S/p1573002081371500.
    // Made some minor changes to the code Leland posted.
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

    val theImage = image
    val theDrawable = drawable
    if (theImage != null) {
        // Box is a predefined convenience composable that allows you to apply common draw & layout
        // logic. In addition we also pass a few modifiers to it.

        // You can think of Modifiers as implementations of the decorators pattern that are
        // used to modify the composable that its applied to. In this example, we configure the
        // Box composable to have a max height of 200dp and fill out the entire available
        // width.
        Box(modifier = modifier,
            gravity = ContentGravity.Center
        ) {
            // Image is a pre-defined composable that lays out and draws a given [ImageAsset].
            Image(asset = theImage)
        }
    } else if (theDrawable != null) {
        Canvas(modifier = modifier) {
            theDrawable.draw(this.nativeCanvas)
        }
    }
}