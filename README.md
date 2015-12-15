# ThreePhasesBottomSheet
A bottom sheet sample that's similar to how Google Maps treat it

This is based mainly on these 2 repositories:

- https://github.com/Flipboard/bottomsheet
- https://github.com/chrisbanes/cheesesquare

and was asked about on StackOverflow here:

http://stackoverflow.com/q/34160423/878126

known issues:
--

-  Tested on version 6, 4.2 and 4.4 . Not sure about the others.
-  doesn't handle well orientation changes
-  rare issue of being able to scroll inside the bottom sheet's content while it's still collapsed (at the bottom)
-  can have weird issues (like showing in full-screen) in case the keyboard was shown before peeking.
