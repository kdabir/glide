Glide
=====

This is source code repo of glide. For User guide check the [glide homepage](http://glide-gae.appspot.com).

[![Build Status](https://travis-ci.org/kdabir/glide.png)](https://travis-ci.org/kdabir/glide)

## Contributing
To start developing glide, you need to have `git` and `java` (1.7) available on your machine. It's advisable
to have `groovy` and `gradle` installed as well (both optional).

Run these commands to see what all is in your PATH and working.

    java -version
    git --version
    gradle --version
    groovy --version

Google App Engine Java sdk is require to run glide. glide's build script can download it for you by
`$ ./gradlew installGae`. Once Done, set the APPENGINE_HOME environment variable to point to the installation.


### Source code
Everything required to build and run glide is checked into the project git repo.

clone the git repo using `$ git clone git@github.com:kdabir/glide.git` or preferably fork the repo and clone the forked repo


### Building
`cd` into the directory do `$ ./gradlew build`

### Running
`$ ./gradlew run -Papp=~/path/to/glide/app`

`$ ./gradlew run` runs the default sample app

### Installing
After you have made modifications to glide source code you can install glide using `$ ./gradlew installApp`


[![githalytics.com alpha](https://cruel-carlota.pagodabox.com/53b148f82205c28cff2d3378e7108793 "githalytics.com")](http://githalytics.com/kdabir/glide)
