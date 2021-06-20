package com.michaelpohl.loopyplayer2.ui.licenses

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class Libraries(val libraries: List<Library>) : Parcelable {
    @Parcelize
    @JsonClass(generateAdapter = true)
    data class Library(
        val artifactId: ArtifactId? = null,
        val copyrightHolder: String? = null,
        val copyrightStatement: String? = null,
        val license : String? = null,
        val licenseUrl: String? = null,
        val normalizedLicense: String? = null,
        val url: String? = null,
        val libraryName: String? = null
    ) : Parcelable {
        @Parcelize
        @JsonClass(generateAdapter = true)
        data class ArtifactId(val name: String? = null, val group: String? = null, val version: String?) : Parcelable
    }
}

