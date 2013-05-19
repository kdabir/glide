Glide
=====
Glide makes it incredibly easy to develop apps that harness the power of Google App Engine for Java
using expressiveness of groovy and sweetness of Gaelyk's syntactic sugar.
To cut the long story short, with glide you can create awesome apps on Google App Engine in a snap

[![Build Status](https://travis-ci.org/kdabir/glide.png)](https://travis-ci.org/kdabir/glide)

---------
### Installing

To install glide, you need to have `java` and `git` on your machine. Just verify using `java -version` and `git --version`.


##### Mac OS X / Linux

Let's say we use dir `~/` to carry out installation

1. Download Google App Engine for Java unzip it and set* the `APPENGINE_HOME` environment variable to point to the extracted directory. `export APPENGINE_HOME=~/appengine-java-sdk-1.7.3`

2. clone the git repo : `$ git clone git@github.com:kdabir/glide.git`

3. `cd` into the directory and install Glide using: `$ cd glide` and `$ ./gradlew installApp`

4. add `~/glide/install/bin` to  the `PATH` variable using `$ export PATH=$PATH:~/glide/install/bin`

5. open a new teminal or source profile again.  `$ glide -h`

*exports should go in `.bash_profile` or `.bashrc` or `.zshrc` whichever is applicable in your case.


##### Windows
let's say we use folder `C:\` to carry out installation

1. Downlaod the Google App Engine for Java. Unzip it and Set the Environment Variable `APPENGINE_HOME` to point to this folder. In our example it would look something like  `APPENGINE_HOME=C:\appengine-java-sdk-1.7.3`

2. Open Command Prompt and clone the git repo `c:\> git clone git@github.com:kdabir/glide.git`. This should create a folder called `glide`

3. `cd` into cloned directory `C:\> cd glide` and install Glide using `C:\glide\> gradlew.bat installApp`

4. set the `PATH` environment vairable to include to the `C:\glide\install\bin`

5. Open a new Command Propmt and try `$ glide -h`


Congratulations, you have installed glide.

---------

###Running

`cd` into directory containing your web content (optionally including any groovy scripts and gtpl templates) and fire glide.

`$ cd my_app`

`$ glide`

Your app should be running on `localhost:8080`

Yes! no verbose config, nothing else. Your web content is being served.

------------
### Why Glide?
Glide provides a dead simple way to create simple apps that run on google app engine. GAE has everything 
that is required to deploy a scalable webapp like static files server, memcache, taskqueue, email, 
xmpp, nosql and the list goes on. But java webapp directory strucutre and verbose config files make it 
difficult to jumpstart. 

With glide, you are not enforced to write a single config file or maintain a specific dir structure. Don't believe, 
just see the [samples](https://github.com/kdabir/glide/tree/master/samples) dir.
