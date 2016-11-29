# Contacts
<img src="https://github.com/Mahendran-Sakkarai/contacts-dashboard/raw/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"/>

Will List the contact with total talktime, Name, Mobile Number, Email and Last spoken time.

# How it works
Implemented using [MVP Architecture](https://github.com/googlesamples/android-architecture.git). A datasource will provide a list of call logs to the presenter. The presenter will update the content to the recyclerview in the fragment.

The call log will contain the below fields.
* Name - Saved contact name else Unknown
* contactId - To get the stored primary key of the contact. To use to get the image at runtime.
* contactNumber - Phone number
* email - stored email else Not mentioned.
* lastContactTime - Last contacted time in millisecond. In Ui <img src="https://github.com/Mahendran-Sakkarai/contacts-dashboard/raw/master/app/src/main/res/drawable-mdpi/ic_date_range_black_24dp.png"/> is used to denote that.
* totalTalkTime - Hold the total time talked. In Ui <img src="https://github.com/Mahendran-Sakkarai/contacts-dashboard/raw/master/app/src/main/res/drawable-mdpi/ic_access_time_black_24dp.png"/> is used to denote that.

# Screenshots
<img src="https://github.com/Mahendran-Sakkarai/contacts-dashboard/raw/master/screenshots/screen_1.png"/>

# Icons
[Material Icons](https://material.io/icons/) by Google.
