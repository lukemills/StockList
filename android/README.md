# StockList Android App

### Primary Contributor: Luke Mills

### Required Android Version
Android 7.1.2 Nougat is required to run the StockList Android application

### Running the App on an Android Virtual Device

To run this app on a virtual device, Android Studio must be downloaded and
the virtual device configured.

To download Android Studio, please visit:
https://developer.android.com/studio/index.html

For instruction on configuring a virtual Android device, please visit:
https://developer.android.com/studio/run/emulator.html

### Using the App
The StockList Android application currently supports a sole user (i.e.
new user account creation was not implemented as authentication was not
a goal of the project this semester), thus on launch the user will enter
his/her inventories directly. 

There are four lists which can be selected from the landing splash screen,
and should have names other than "List 1", "List 2", "List 3", "List 4"
if the server is not down.

NOTE: If the server appears to be down (i.e. data is not loaded on
application start), please contact Colin Mawhinney, as he is responsible
for server operation.

Clicking on the name of a list will bring the one to the inventory overview
for that list (in the InStock childlist to start). Navigation is 
straighforward (swiping left and right moves between child list fragments).


Opening the drawer on the left hand side of the app enables one to access
the other parent lists.

To add a new item, the floating action button (circular button with a plus
sign) may be clicked, and the user must fill in the product's information
and click "Add product." This will add the product to the Child list in
which one was on clicking the floating action button.

To edit an item, move an item to another child list within the given parent
list, or delete an item, the item should be clicked on in the child list it
presently resides. This will launch the detail activity, where all the above
can be accomplished.
	* Quick item increment/decrement can be accomplished by clicking the 
	buttons labeled with plus/minus signs
	* Item transition to a new child list can be accomplished via clicking
	the arrow icon at the top of the app, and selecting a destination list
	to which the item will be moved
	* Item deletion can be completed by clicking the trash can icon at the
	top of the app and confirming intent to delete.
	* Item detail edit of any field can be accomplished by clicking the
	gear icon, chaning/adding relevant field, and confirming these changes

Quick decrementation can also be accomplished via clicking the button with the
minus sign labeling it within a given product's card in the inventory overview
of the given child list in which the item resides.

### On Synchronization with the Server
It should be noted that the app will only pull data from the back end on
launch of the application. When editing a product's information, adding a new
product, or moving a product to a different lists, such changes will indeed
be posted to the database at that very moment, and the changes will be reflected
in the data for the item stored on the back end provided the user has an internet
connection.

Failure for the API to return/failure of sustaining a connection to make the
request will result in the Android app not being updated with the back end.
