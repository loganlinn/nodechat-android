# Android NodeChat #

### About ###

This is an Android client that will connect to the example node.js chat room [https://github.com/ry/node_chat](https://github.com/ry/node_chat) (chat room demos at [http://chat.nodejs.org/](http://chat.nodejs.org/), [http://twit.me/](http://twit.me/)). The application interfaces with a server running the node.js chat room in the same manner the browser version does.

I started this as a side-project to learn Android development and play with node.js -- feel free to contribute! If there is enough interest/contributions, I will publish to the Android Market. 

* * *

Licensed under the Apache License, Version 2.0 (http://www.apache.org/licenses/LICENSE-2.0.html)

### Getting Started ###

Install necessary packages

* Follow the [Android SDK Getting Started Guide](http://developer.android.com/sdk/index.html).  You will probably want do set up a device emulator and debugging tools (such as using "adb logcat" for viewing the device debugging and error log).

* Pull the read-only repository from github

     e.g. "git clone git://github.com/loganlinn/nodechat-android"

To build with Eclipse (3.5), do the following:

* Import the project into your Eclipse workspace

### To-do's/Known Issues ###
* Connecting to a server that is not running the chat room will likely cause force close
* Needs to support removing servers/clean-up server list

