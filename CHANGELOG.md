# Changelog

## 2.0.0

* Use Paper (formerly PaperSpigot) API.
* Introduce new block type handling, blocks are now stored in the database.

### 1.3.5

* Deny builders to place water and lava.
* Deny builders placing and removing bedrock.
* Deny builders to ignite blocks.

### 1.3.4

* Allow to configure motd.
* Allow to configure default access level.

### 1.3.3

* Log block changes in new Threads.
* Showing blocklog is now done own thread, preventing lag.
* Remove setting whole Plugin in block listener

### 1.3.2

* Deny /spawn, /tp, /tphere, /group and /home-commands access when user is restricted.
* Fix possible NullPointerException in player interact event.

### 1.3.1

* Added command /restrict, this will restrict a user from building and interacting in-game.
* Determine interaction and building rights based on separate boolean values, instead of checking if user is guest.

## 1.3.0

* Simplify UserHandler getUser methods.
* Add MySQLHandler.select(PreparedStatement)
* Add UserHandler.changeStatus(User, int) to change a users access level and save it.
* Improve implementation of UserHandler.updateUser(User)
* Allow to copy a user via new constructor `new User(User)`
* Added command /tp, /tphere, /changestatus and /reg
* Added a base command class to check permissions.
* New users are now by default guests.
* Upgrade to spigot and bukkit api v1.12.2

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
