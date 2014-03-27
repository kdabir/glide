Glide
=====

This is source code repo of glide. For User guide check the [glide homepage](http://glide-gae.appspot.com).

[![Build Status](https://travis-ci.org/kdabir/glide.png)](https://travis-ci.org/kdabir/glide)

## Contributing
To start developing glide, you need to have `git` and `java` (at least 1.7) available on your machine. It's advisable
to have `groovy` and `gradle` installed as well (both optional).

Run this command to see what all is in your PATH and working.

    curl -sL https://raw.github.com/kdabir/dq/master/bin/groovy_dev/dq.sh | sh

### Source code

Everything required to build and run glide is checked into the project git repo. fork the repo, make changes, send PR.

### Building

`cd` into the directory do `$ ./gradlew build`

### Running

You can directly run the glide app without installing glide using:

`$ ./gradlew run -Papp=~/path/to/glide/app`

`$ ./gradlew run` runs the default sample app

### Installing

After you have made modifications to glide source code you can install glide using `$ ./gradlew installApp`


[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/53b148f82205c28cff2d3378e7108793 "githalytics.com")](http://githalytics.com/kdabir/glide)
