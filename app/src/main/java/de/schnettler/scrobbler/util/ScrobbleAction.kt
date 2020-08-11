package de.schnettler.scrobbler.util

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.ui.graphics.vector.VectorAsset

enum class ScrobbleAction(val asset: VectorAsset) {
    EDIT(Icons.Outlined.Edit),
    DELETE(Icons.Outlined.Delete),
    OPEN(Icons.Outlined.OpenInNew),
    SUBMIT(Icons.Outlined.CloudUpload)
}