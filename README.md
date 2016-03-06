# What is this about?

This is the main repository of the AppHunt Android app - [LINK](https://play.google.com/store/apps/details?id=com.apphunt.app).
The project is almost abandoned. It had a great success story but our time split up so now the maintaining part is really 
taking too much time for a single person to handle.

# What functionality it includes?

* Submitting apps
* Creating app collections
* Vote, favourite, install apps
* Search apps and collections
* Review recent events
* Receive notifications - server ones and reminding ones
* Many many more. Install the app to review

# Main Architecture


## API

The app has an API class using Volley. When you make a request you call the  API to make a request. It adds a request to the Volley queue
and when the request is done it posts an event using the Otto event bus. The components which are listening for the event will receive it
and visuallize the data.

## FragmentManager

Fragments get unsubscribed from the event bus when they are being replaced or become hidden from another fragment which is placed in 
the backstack over them.

## Custom Views

Vote buttons and several other components are custom views. They extend LinearLayout and subscribe to the bus in onAttachToWindow. That is how
when you vote an app in the details screen, it is voted also in the list of apps screen.

## Login

When the user opens the app he is automatically logged in as an anonymous user. Afterwards he can login with G+, Facebook, Twitter.

# For more info

Contact me at: gmirchev90@gmail.com Thanks!

Feel free to use this code as you want! I hope there will be a person that will find the app helpful and will continue supporting it!
