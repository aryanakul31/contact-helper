package com.nakul.contact_helper.service

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.nakul.contact_helper.models.ContactModel
import com.nakul.contact_helper.models.ContactsEventModel
import com.nakul.contact_helper.repo.ContactsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactService : Service(), IContactService {

    companion object {
        const val CONTACT_SERVICE_TAG = "Contact Service"
        const val CONTACTS_SYNCED_FILTER = "SYNCED_CONTACTS"

        @RequiresPermission("android.permission.READ_CONTACTS")
        fun initService(context: Context) {
            context.startService(Intent(context, ContactService::class.java))
        }
    }

    private val contactsRepository by lazy { ContactsRepository(context = applicationContext) }
    private val contacts = ArrayList<ContactModel>()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) initContactsSync()
        else stopSelf()


        return super.onStartCommand(intent, flags, startId)
    }

    override fun initContactsSync() = CoroutineScope(Dispatchers.IO).launch {
        contacts.addAll(contactsRepository.readContacts())
        emitContacts()
        Log.i(CONTACT_SERVICE_TAG, "Last Synced Count: ${contacts.size}")

        readForUpdatedContacts()

        contactsRepository.saveContacts(contacts)
        emitContacts()
        Log.i(CONTACT_SERVICE_TAG, "Updated Synced Count: ${contacts.size}")

        stopSelf()
    }

    override fun emitContacts() {
        val intent = Intent(CONTACTS_SYNCED_FILTER)
        intent.putExtra("data", ContactsEventModel(contacts))
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    /*Read all updated Contacts*/
    override fun readForUpdatedContacts() {
        val projection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP,
        )

        val filter =
            "${contactsRepository.lastSyncTime()}<${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP}" //Filter to get updated/modified contacts only

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI, projection, filter, null, null
        ) ?: return

        if (cursor.count < 0) {
            cursor.close()
            return
        }

        while (cursor.moveToNext()) {
            val id = readValueFromCursor(cursor, ContactsContract.Contacts._ID) ?: continue
            val name =
                readValueFromCursor(cursor, ContactsContract.Contacts.DISPLAY_NAME) ?: continue
            val isPhoneAvailable =
                readValueFromCursor(cursor, ContactsContract.Contacts.HAS_PHONE_NUMBER) ?: continue
            val updatedAt = readValueFromCursor(
                cursor, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
            ) ?: continue

            if (isPhoneAvailable.toInt() == 0) continue //Check if contact has phone number

            readContact(id = id, name = name, updatedAt = updatedAt)
        }

        cursor.close()
    }

    /*Read value from Cursor. Handled: null in case of -1 */
    override fun readValueFromCursor(cursor: Cursor, columnName: String): String? {
        val idIndex = cursor.getColumnIndex(columnName)
        if (idIndex < 0) return null

        return cursor.getString(idIndex)
    }

    /*Read each User Contact*/
    override fun readContact(id: String, name: String?, updatedAt: String?) {
        val cursorPhone = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
            arrayOf(id),
            null
        )

        if (cursorPhone == null || cursorPhone.count <= 0) {
            cursorPhone?.close()
            return
        }

        while (cursorPhone.moveToNext()) {
            val phoneNumValue =
                readValueFromCursor(cursorPhone, ContactsContract.CommonDataKinds.Phone.NUMBER)
                    ?: continue

            readAndSavePhoneNumber(
                name = name, phoneNumValue = phoneNumValue, updatedAt = updatedAt
            )
        }
        cursorPhone.close()
    }

    /*Read and Save each Phone Number inside a Contact*/
    override fun readAndSavePhoneNumber(name: String?, phoneNumValue: String, updatedAt: String?) {
        val phoneNumberParsed = try {
            val formattedNumber = "+" + phoneNumValue.replace(Regex("\\D"), "")
            PhoneNumberUtil.getInstance().parse(formattedNumber, null)
        } catch (e: Exception) {
            null
        }

        if (phoneNumberParsed == null) return

        val contact = ContactModel(
            name = name,
            phone = phoneNumValue,
            updatedAt = updatedAt,
        )
        contacts.add(contact)
    }

}