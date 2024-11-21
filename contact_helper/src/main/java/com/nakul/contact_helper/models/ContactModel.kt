package com.nakul.contact_helper.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ContactModel(
    var phone: String? = null,
    var updatedAt: String? = null,
    var name: String? = null,
    var id: String? = null,
):Parcelable