** Version 1.7.101 (Beta) **

* Fix dupe bug when dank null leaves inventory (Fixes #308)

--------------------------------------------------------------------------------

** Version 1.7.100 (Beta) **

* Re-work remove dank-null from dock. Prevents sync issues. (And simplify code)

--------------------------------------------------------------------------------

** Version 1.7.99 (Beta) **

* Fix dupe with non-initialized dank null (#303)

--------------------------------------------------------------------------------

** Version 1.7.98 (Beta) **

* Fix canceling a non-canceling event
* Fix Crafting Dupe with non vanilla tables

--------------------------------------------------------------------------------

** Version 1.7.97 (Beta) **

* Change default extraction mode to keep 1 instead of keep all
* Fix a bug with stack selection

--------------------------------------------------------------------------------

** Version 1.7.96 (Beta) **

* Add a defensive check for #284 (crash on opening dank null) 
* Reworking the release process to be more streamlined and better visibility

--------------------------------------------------------------------------------

** Version 1.7.95 (Beta) **

* Pickup into dank nulls now work for all inventory instead of just first one
with the item.
* Fix a visual glitch with inventory contents on servers.
* Fix a dupe **THIS FIX WILL CLEAR THE INVENTORY OF ALL DANK NULLS**
* Re-adding voiding on pickup. It will only void after all dank nulls have had a
chance to absorb the item.
* Re-adding extraction limitations. Remember placement is an extraction option
first so if you can't extract you can't place.

--------------------------------------------------------------------------------

** Version 1.7.94 (Beta) **

* Fix crash when picking up item not in dank null.

--------------------------------------------------------------------------------

** Version 1.7.93 (Alpha) **

* Fixing picking up items only working for first slot

--------------------------------------------------------------------------------

** Version 1.7.92 (Beta) **

* Fix deletion of items on pickup if they aren't in a dank null
* Dank Null will not update properly update VISUALLY when adding or removing
 items by hand. Still determining cause.

--------------------------------------------------------------------------------

** Version 1.7.91 (Release) **

* Update to newer Forge
* Jar is now signed by cjm721's signature
* Simplify placement code to fix several bugs, this removed some small features
such as setting banner items and slab placement

--------------------------------------------------------------------------------

** See Curseforge for Previous Change Logs **