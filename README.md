# ThreePhasesBottomSheet
A bottom sheet sample that's similar to how Google Maps treat it.
Example animation:

![enter image description here](https://raw.githubusercontent.com/AndroidDeveloperLB/ThreePhasesBottomSheet/master/device-2016-01-16-175728.gif)

This is based mainly on these 2 repositories:

- https://github.com/Flipboard/bottomsheet
- https://github.com/chrisbanes/cheesesquare

and was asked about on StackOverflow here:

http://stackoverflow.com/q/34160423/878126

It might be possible to use this repository instead of the bottomSheet repo:

https://github.com/umano/AndroidSlidingUpPanel

known issues:
--

first method, of using AppBarLayout,CoordinatorLayout , etc : 

-  Tested on version 6, 4.2 and 4.4 . Not sure about the others.
-  doesn't handle well orientation changes. Handled in the sample by re-showing the bottom sheet, but it doesn't restore its state.
-  rare issue of being able to scroll inside the bottom sheet's content while it's still collapsed (at the bottom). Not sure if still exist.
-  can have weird issues (like showing in full-screen) in case the keyboard was shown before peeking. This is handled in the sample by just hiding the bottom sheet when the keyboard appears.
-  can't handle situations that the bottom sheet is larger in its peeked state, compared to when it's in expanded state.
- when you press the back button to go from a scrolled expanded bottom sheet state, to the state of peek, it has some weird issues. Need to handle this, or make it work differently. In the sample, I chose to just dismiss the bottom sheet for this case.

Second method, of using just a NestedScrollView :

- doesn't snap for scrolling of the bottom sheet, when it's in expanded mode. not an issue, just a missing feature. 
- pressing the back button when the bottom sheen is expaneded can cause weird UI issues.
- might have other issues.
