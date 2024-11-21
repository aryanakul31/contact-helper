package com.nakul.contact_helper.repo

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nakul.contact_helper.models.ContactModel

private const val CONTACTS_SHARED_PREF = "contacts_shared_pref"
private const val KEY_LAST_SYNC_TIME = "last_sync_time"
private const val KEY_CONTACTS = "contacts"

class ContactsRepository(context: Context) : IContactsRepository {

    init {
        initSharedPref(context = context)
    }

    /**Shared Pref*/
    private var _sharedPref: SharedPreferences? = null
    private val sharedPref get() = _sharedPref!!

    private fun initSharedPref(context: Context) {
        val mainKey =
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            CONTACTS_SHARED_PREF,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        _sharedPref = sharedPreferences
    }


    override fun updateLastSyncTime() {
        sharedPref.edit().putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis()).apply()
    }

    override fun lastSyncTime(): Long = sharedPref.getLong(KEY_LAST_SYNC_TIME, 0)

    private val gson by lazy { Gson() }

    override fun saveContacts(contacts: ArrayList<ContactModel>) {
        val json = gson.toJson(contacts)
        sharedPref.edit().putString(KEY_CONTACTS, json).apply()
        updateLastSyncTime()
    }

    override fun readContacts(): List<ContactModel> {
        val json = sharedPref.getString(KEY_CONTACTS, null) ?: return emptyList()
        val type = object : TypeToken<List<ContactModel>>() {}.type
        return gson.fromJson(json, type)
    }

}