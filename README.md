# ThreePhasesBottomSheet
A bottom sheet sample that's similar to how Google Maps treat it

This is based mainly on these 2 repositories:

- https://github.com/Flipboard/bottomsheet
- https://github.com/chrisbanes/cheesesquare

and was asked about on StackOverflow here:

http://stackoverflow.com/q/34160423/878126

known issues:
--

first method, of using AppBarLayout,CoordinatorLayout , etc : 

-  Tested on version 6, 4.2 and 4.4 . Not sure about the others.
-  doesn't handle well orientation changes. Handled in the sample by re-showing the bottom sheet, but it doesn't restore its state.
-  rare issue of being able to scroll inside the bottom sheet's content while it's still collapsed (at the bottom). Not sure if still exist.
-  can have weird issues (like showing in full-screen) in case the keyboard was shown before peeking. This is handled in the sample by just hiding the bottom sheet when the keyboard appears.
-  for some reason, in the past commits, I've caused a weird bug that doesn't let to scroll to the bottom. It was ok before... Need to investigate it further.
-  can't handle situations that the bottom sheet is larger in its peeked state, compared to when it's in expanded state.

Second method, of using just a NestedScrollView :
- None, except it's quite hard to customize.
