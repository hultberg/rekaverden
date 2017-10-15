# Changelog

## 1.3.0

* Simplify UserHandler getUser methods.
* Add MySQLHandler.select(PreparedStatement)
* Add UserHandler.changeStatus(User, int) to change a users access level and save it.
* Improve implementation of UserHandler.updateUser(User)
* Allow to copy a user via new constructor `new User(User)`
* Added command /tp, /tphere, /changestatus and /reg
* Added a base command class to check permissions.
* New users are now by default guests.

### 1.2.6

* Return minecart and remove when player exists it.
* Add access check for /i command.
* Fix NullPointerException when checking for block owner

### 1.2.5

* Added User.hasAccessLevel
* Added constants for access levels
* Refactored some access level checks
* Change entity explode event listener to only cancel event when creepers explode
* Disallow users from placing TNT.

### 1.2.3

* Fixed home command to respond with "Please provide the name of the home."
* Fixed admins being unable to check permissions on chests/furnace they did not own.
* Fixed admin-stick on chests.
* Fixed selecting area on blocks admins do not own.
