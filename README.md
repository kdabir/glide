Glide
=====
Glide makes it incredibly easy to develop apps that harness the power of Google App Engine for Java
using expressiveness of groovy and sweetness of Gaelyk's syntactic sugar.
To cut the long story short, with glide you can create awesome apps on Google App Engine in a snap

---------
### Installing

To install glide, you need to have java and git on your machine.
Just verify using `java -version` and `git --version`.


##### Mac OS X / Linux

Let's say we use dir `~/` to carry out installation

1. Download Google App Engine for Java unzip it and set* the `APPENGINE_HOME` environment variable to point to the extracted directory.

	`export APPENGINE_HOME=~/appengine-java-sdk-1.7.3`

2. clone the git repo :

    `$ git clone git@github.com:kdabir/glide.git`

3. `cd` into the directory and install Glide using:

    `$ cd glide`

    `$ ./gradlew installApp`

4. set* the `PATH` include to the `~/glide/build/install/bin`

	`$ export PATH=$PATH:~/glide/build/install/bin`

5. a new teminal or source profile again.
    `$ glide -h`

*exports should go in `.bash_profile` or `.bashrc` or `.zshrc` whichever is applicable in your case.


##### Windows
let's say we use folder `C:\` to carry out installation

1. Downlaod the Google App Engine for Java.
Unzip it and Set the Environment Variable `APPENGINE_HOME` to point to this folder.
In our example it would look something like  `APPENGINE_HOME=C:\appengine-java-sdk-1.7.3`

2. Open Command Prompt and clone the git repo :

    `c:\> git clone git@github.com:kdabir/glide.git`

	This should create a folder called `glide`

3. `cd` into clone directory and install Glide using:

    `C:\> cd glide`

    `C:\glide\> gradlew.bat installApp`

4. set the `PATH` environment vairable to include to the `C:\glide\build\install\bin`

5. Open a new Command Propmt and try
    `$ glide -h`


Congratulations, you have installed glide.

