package com.nakul.contact_helper.service

import android.database.Cursor
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.Job


interface IContactService {

    @RequiresPermission("android.permission.READ_CONTACTS")
    fun initContactsSync() : Job

    /*Emit Contacts using EventBus*/
    fun emitContacts()

    /*Read all Contacts*/
    fun readForUpdatedContacts()

    /*Read value from Cursor. Handled: null in case of -1 */
    fun readValueFromCursor(cursor: Cursor, columnName: String): String?

    /*Read each User Contact*/
    fun readContact(id: String, name: String?, updatedAt: String?)

    /*Read and Save each Phone Number inside a Contact*/
    fun readAndSavePhoneNumber(name: String?, phoneNumValue: String, updatedAt: String?)

}