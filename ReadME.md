# Android Contact Helper Library

This library simplifies the process of syncing phone contacts with a server or local storage, providing efficient and secure data handling.

## Features

* **Phone Number Validation:** Validates phone number format using the libphonenumber library, ensuring only valid numbersare processed.
* **Secure Storage:** Stores synced contacts and last sync time in encrypted preferences using androidx.security.crypto, protecting sensitive data.
* **Local Broadcast Communication:** Emits synced contacts using LocalBroadcastManager for seamless integration with your app's components.
* **Efficient Data Handling:** Uses data classes and interfaces for structured data representation and interaction, promoting code clarity.
* **Background Processing:** Utilizes coroutines for efficient background operations, preventing UI thread blocking.

## Prerequisites

* **Contact Permission:** Inside AndroidManifest.xml file, add below permission to access and sync contacts.

```manifest
    <uses-permission android:name="android.permission.READ_CONTACTS" />
```

## Installation

Add the following dependency to your module-level `build.gradle.kts` file:

```groovy
    implementation 'com.github.aryanakul31:contact-helper:1.0.0'
```

## Usage

1. **Request and Handle Contact Permission:**
    * Request the `android.permission.READ_CONTACTS` permission at runtime using the standard Android permission request flow.
    * Handle the permission request result in your activity or fragment, proceeding with contact sync only if the permission is granted.

2. **Initialize Contact Sync:**
    * Call `ContactService.initService(this)` To initiate the contact sync process. This will trigger the service to read contacts, validate phone numbers, and store them securely.

    ```kotlin
        ContactService.initService(this)
    ```

3. **Setup Broadcast Receiver:**
    * Create a `BroadcastReceiver` to listen for the `ContactService.CONTACTS_SYNCED_FILTER` intent, which is broadcast when the sync process is complete.
    * Register the receiver in your activity or fragment using `LocalBroadcastManager.getInstance(this).registerReceiver()`.
    * Handle the received contacts in the `onReceive()` method of the broadcast receiver. The contacts will be available as an `ArrayList<ContactModel>` in the intent extras.

**Example:**

```kotlin 
    ContactService.initService( this) //In your activity or fragment
    
    //Create and initiate BroadcastReceiver
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) 
        { 
            val contacts = intent.getParcelableArrayListExtra<ContactModel>(ContactService.CONTACTS_SYNCED_FILTER)  // Process the received contacts 
        }
    }

    //Register on onStart()-Activity or onAttach()-Fragment
    LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, IntentFilter(ContactService.CONTACTS_SYNCED_FILTER))

    //UnRegister on onStop()-Activity or onDetach()-Fragment
    LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
    
``` 

## Data Structures

* **`ContactModel`:** Represents a contact with properties for `phone`, `updatedAt`, `name`, and `id`.
* **`ContactsEventModel`:** Used for communication between the `ContactService` and the broadcast receiver, containing an `ArrayList<ContactModel>`.

## Interfaces and Classes

* **`IContactRepository`:** Interface for contact data operations, providing methods for reading, saving, and accessing last sync time.
* **`ContactsRepository`:** Implementation of `IContactRepository` using encrypted preferences for secure storage of contact data.
* **`IContactService`:** Interface for contact sync operations, defining methods for initializing sync, reading contacts, and emitting events.
* **`ContactService`:** Implementation of `IContactService` using coroutines for background processing and LocalBroadcastManager for communication.


## License
[MIT License](https://github.com/aryanakul31/contact-helper/blob/main/LICENSE)