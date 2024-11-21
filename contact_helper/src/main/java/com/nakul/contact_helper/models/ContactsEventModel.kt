package com.nakul.contact_helper.models

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class ContactsEventModel(val contacts: ArrayList<ContactModel>) : Parcelable