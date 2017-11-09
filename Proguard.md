# Proguard

All the entry points (which are defined in the manifest) are listed under : 
build/intermediates/proguard-rules

The rules are defined into two file : 
- proguard-android.txt
- proguard-android-optimize.txt

4 steps :
- shrink : analyse and delete the code that is not used
- Optimized : optimise the code left (only if proguard-android-optimize.txt is selected)
	- deeper shrink : detect dead code inside methods, unused paramter and calls
	- inline members, downgrade methods visibility
	You cannot limit this behavior to certain class only
- Obfuscate : rename all classes, members, parameters that are not define as entry points.
- Preverify : to be disabled on Android

A mapping.txt file is generated. it is required to read obfuscated crash reporting stacktraces.