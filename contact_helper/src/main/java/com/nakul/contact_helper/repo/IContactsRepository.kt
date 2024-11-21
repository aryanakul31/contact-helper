package com.nakul.contact_helper.repo

import com.nakul.contact_helper.models.ContactModel

interface IContactsRepository {
    fun updateLastSyncTime()
    fun lastSyncTime(): Long
    fun saveContacts(contacts: ArrayList<ContactModel>)
    fun readContacts(): List<ContactModel>
}