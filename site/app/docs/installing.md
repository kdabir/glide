# Installing

Glide can be installed in various ways. At it's heart it is a simple Java based command line application and 
a Gradle Plugin.  To make `glide` command available, the path to bin directory of glide installation should be in your `PATH`
environment variable.


## Mac OS X, Linux or any other \*Nix (including Cygwin)

If you use an OS that flaunts a decent Terminal app with `bash` or `zsh` you can choose one of the following three methods:

- ### Using SDKMAN!

    If you have [sdk](http://sdkman.io/) installed, you are just this command away from glide:

        $ sdk install glide


- ### Manually

    1. Download the [zip](https://dl.bintray.com/kdabir/glide/io/github/kdabir/glide/glide-runner/0.9.2/) and extract it lets say in `~`

    2. Add `~/glide-0.9.2/bin` to the `PATH` variable using `$ export PATH=$PATH:~/glide/install/bin`

    > *exports should go in `.bash_profile` or `.bashrc` or `.zshrc` whichever is applicable in your case.

## Windows

It's fairly straight forward to get glide running on Windows

1. Download the [zip](https://dl.bintray.com/kdabir/glide/io/github/kdabir/glide/glide-runner/0.9.2/) and extract it lets say in `C:\`

2. Add the path to the bin to this directory to `PATH` environment variable e.g. `C:\glide-0.3.3\bin` (make sure the path
    is valid)

----

## Verifying the Installation

Open a new Teminal (Command prompt) and verify installation using `glide -v`. If you see glide version, we are done.

**Congratulations, you have installed glide**


> The first run of glide application usually takes long time because it downloads latest Google App Egnine SDK and other dependencies
> so please grab a coffee and relax. Next run onwards, glide would start almost immediately.

----

# Installing from sources

Trust us, it is not at all that messy as one might think. In fact we recommend you use the source based mechanism to
install glide.

## Installer script

The best and easiest way to install glide from source is through the installer script:

    curl http://glide-gae.appspot.com/install | sh

> This script should work on Mac OS X, Linux, and other *NIX Operating Systems. For Windows, either use this script from
> Cygwin / Git Bash or follow the windows specific steps documented below.


Copy and paste this into Terminal and update `PATH` environment variables with the path shown at
the end of the script's output.


> You just need to have `java` and `git` on your machine (verify using `java -version` and `git --version`).


## Manually:

If you want to do it all manually, it's still simple.

- ### Mac OS X / Linux

    Let's say we use dir `~/` to carry out installation

    1. clone the git repo : `$ git clone git@github.com:kdabir/glide.git`

    2. `cd` into the directory and install Glide using: `$ cd glide` and `$ ./gradlew installDist`

    3. follow the on screen instructions to export `PATH` 

    4. open a new teminal or source profile again.  `$ glide -h`

    *exports should go in `.bash_profile` or `.bashrc` or `.zshrc` whichever is applicable in your case.


- ### Windows (with/without git)

    let's say we use folder `C:\` to carry out installation

    1. Open Command Prompt and clone the git repo `c:\> git clone git@github.com:kdabir/glide.git`.

        This should create a folder called `glide`

        > If you don't have git installed, [download](https://github.com/kdabir/glide/archive/master.zip) glide's zip and unzip it.

    2. `cd` into cloned directory `C:\> cd glide` and install Glide using `C:\glide\> gradlew.bat installDist`

    3. set the `PATH` environment variable according to on-screen instructins 

    4. Open a new Command Propmt and try `$ glide -h`

----

## FAQs

### I already have App Engine SDK installed, can glide use that?

Sure, that's great. Just make sure you set the `APPENGINE_HOME` environment variable to point to the installation

