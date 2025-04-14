# Roboloco's Tunable Constants<a name="introduction"></a>
Ever thought "man, deploying code to the bot takes FOREVER to complete? In the time we build, we could be doing so many other cool stuff!" Then you realize that half of the time you are building is because you changed the kP value of one of your subsystems: an insignificant change that wastes minutes of everyone's time by forcing a rebuild for a deploy. So you, being a curious and investigative FRC programmer, decide to take matters into your own hands to try to speed up the time it takes to rebuild by finding a solution yourself: one that speeds up not just PID tuning, but updating small values in code in general.

Then, you stumble onto Robot Preferences. It seems like the perfect solution! You can dynamically update values with ease! Or so you think... until you realize the work that goes into using it. You have to initialize a constant, then whenever it changes(or perhaps arbitrarily if you are feeling lazy), you have to not only poll that constant value, but update every place in the code that uses it! Insane! For every PID controller, you have to call setPID, and that can be hard to keep track of. Not only that, but the values are STRING INDEXED, meaning that you'll have no clue if you make a typo!

So now you have the monumental task of transforming your Constants files to use this new system. If only there was an easy tool to just convert everything for you.

That's what Cha- I mean Roboloco's Tunable Constants vendordep is for! It is a lightweight vendordep that allows for the easy conversion of preexisting constants files into files tunable with Robot Preferences. Not only that, but it also handles automatically running a customizable reload function for a subsystem whenever a Preferences value changes. It is very simple to set up, and great for speeding up development time for new teams!
# Table of contents
- [Roboloco's Tunable Constants](#robolocos-tunable-constants)
- [Table of contents](#table-of-contents)
  - [Installation ](#installation-)
  - [Usage ](#usage-)

## Installation <a name="installation"></a>
1. Download the vendordep json from the [releases](https://github.com/Advay17/TunableConstants/releases) and add it to your vendordeps folder in your target project
2. Add the following line of code to build.gradle: `annotationProcessor "com.roboloco.tune:tune-annotation-processor:{version}"`, where `{version}` is the version of the vendordep you are using(i.e. 0.0.3, don't include the curley braces!!).
3. After building, the extension should be installed locally!

## Usage <a name="usage"></a>
-Add the @IsTunableConstants
