# RoboVM ContractR sample port to [Intel Multi-OS Engine](http://software.intel.com/en-us/multi-os-engine)

Sample app for iOS and Android which lets you add Clients, Tasks and keep
track of the time you are working on these tasks and how much money your work
is generating.

This sample gives an idea of how to share code between an iOS and Android app
using native UI in both apps. The iOS and Android projects are using a shared
core project which holds the Model part of the Model View Controller pattern.

## iOS app

The iOS app is built using native UI components and APIs available through the
Cocoa bindings in the Multi-OS Engine (the original version used RoboVM). This
project depends on the core project which holds all domain objects and services
for managing clients, tasks and the work performed.
The ported app is under the moe directory.
For reference, the original RoboVM version can be found in the ios directory.

## Android app

The Android app is built using standard Android components such as view XMLs,
Fragments and ActionBar.


## Running the Code

To try them out, you need to execute the following steps:
* Install Android Studio
* Install the Multi-OS Engine from here: http://software.intel.com/en-us/multi-os-engine
* Use the "Import Project" function of Android Studio to open the projects
* Once opened, you can run the projects using the Multi-OS Engine Application run configuration
* You can also open the associated Xcode project to see the Objective-C sources by right clicking on the project and selecting "MOE Actions" -> "Open Project in Xcode" in Android Studio
* You can also launch the project from Xcode

If you have any questions, you may [contact us directly on our website](http://www.migeran.com/contact/) or on the [Multi-OS Engine forum](https://software.intel.com/en-us/forums/multi-os-engine).

### About Migeran

Migeran is a software development company. Our first product, Migeran for iOS was acquired by Intel Corporation, and it is now part of the Intel Multi-OS Engine. We are now Intel development, training and consulting partners for the Multi-OS Engine.

* Check out our website: http://www.migeran.com
* Follow us on Twitter: http://twitter.com/migeran
* Like us on Facebook: https://www.facebook.com/migeranltd
