# Contacts
<img src="https://github.com/Mahendran-Sakkarai/contacts-dashboard/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"/>

Will List the contact with total talktime, Name, Mobile Number, Email and Last spoken time.

Implemented using [MVP Architecture](https://github.com/googlesamples/android-architecture.git) with Cursor Loader. Used CursorLoader to load the contacts in seperate thread without affecting the UI thread.

[Download](https://github.com/Mahendran-Sakkarai/contacts-dashboard/raw/master/app/app-release.apk) the apk.

# How it works
Once the view started a method will be triggered to get the data. First a cursor loader will be called to get the list of contacts. Using the contacts another cursor loader will get the phonenumbers. Again with the phone number a cursor loader will get the call logs. Updating the call log list with the information like last spoken time and total talk time. By Iterating through the call logs will remove the call log who never spoken. Then with the updated call logs list a method will fetch the email id and profile picture. Once everything is done a call back will pass the updated list to the recyclerview.

The call log will contain the below fields.
* name - Saved contact name
* contactId - To get the stored primary key of the contact.
* contactNumber - Phone number
* email - stored email else Not mentioned.
* lastContactTime - Last contacted time in millisecond. In Ui <img src="https://github.com/Mahendran-Sakkarai/contacts-dashboard/raw/master/app/src/main/res/drawable-mdpi/ic_date_range_black_24dp.png"/> is used to denote that.
* totalTalkTime - Hold the total time talked. In Ui <img src="https://github.com/Mahendran-Sakkarai/contacts-dashboard/raw/master/app/src/main/res/drawable-mdpi/ic_access_time_black_24dp.png"/> is used to denote that.
* bitmap - To hold the profile picture of the selected contact.

# Screenshots
<img src="https://github.com/Mahendran-Sakkarai/contacts-dashboard/raw/master/screenshots/screen_1.png"/>

# Icons
[Material Icons](https://material.io/icons/) by Google.

# Key
Generated a testing key to build the release apk. Key store name, Key and Key password is `testing`.
