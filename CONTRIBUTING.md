# Contributing to Glide

First off, thanks for checking out Glide.
  
You can contribute to Glide in many ways like: 

- Asking questions (yeah, we mean it)
- Reporting issues
- Creating feature requests
- Updating documentation
- Fixing an issue or implementing a feature
- Providing links of the projects that use Glide
- Just spreading the word about glide
- Or just hack glide your own way


## Reporting Issues 

- Provide steps to reproduce the issue
- Provide relevant sections from `build.gradle`, `glide.groovy` and any other relevant code snippets. (or entire files, if you can)

## Developing

Glide is developed using Groovy/Java and built with Gradle. There are unit and integration tests that verify the correctness 
of builds. If you are adding a feature, please add corresponding tests as well. Need help? feel free to ask.


### Prerequisites

To hack glide, a Mac/Linux is preferred OS. However, Windows is supported too (after all Glide is 100% JVM
 based `¯\_(ツ)_/¯` ).  If you don't want to setup development environment locally, you can use various cloud based 
 editors like [Cloud9](https://c9.io), [Koding](https://www.koding.com) etc.

You need to have `git` and `java` (at least 1.7) available on your machine. It's advisable to have `groovy` and `gradle` 
(both optional) installed via [sdkman](http://sdkman.io).

Instead of typing long commands on command-line, We have created some shortcuts in Makefile, using which is completely 
optional. If you want to use it, make sure `make` is installed. Also `Makefile` is documented just enough to understand 
the tasks available

For consistent file formatting, the `.editorconfig` is also checked in. Please use editors that support it (like 
IntelliJ IDEA, Atom, SublimeText). We highly recommend using IntelliJ IDEA Community or Ultimate edition.

### Building 

This repo contains various gradle (sub)projects including the glide-gradle-plugin, glide cli, documentation website, and samples.
you can check available tasks using `./gradlew tasks`

### Integration Tests

Before running your first integration test, make sure you run `make intSetup` once. This creates symlink to Gradle downloads
  so that libraries need not be downloaded again. 

Then run `./gradlew intTest`

### Using locally built version

sandbox project can be used to test locally built glide. 

run `make p`


### Publishing

Check if you have keys for Bintray, SDKMAN and Gradle plugin portal.

To Check, run
  
    gradle hasKeys
  
To set keys, edit the file at:
  
     ~/.gradle/gradle.properties
  
Following keys must be present `bintrayUser`, `bintrayKey`, `gradle.publish.key`, `gradle.publish.secret`,  
`sdkmanConsumerKey`, `sdkmanConsumerToken`

Gradle tasks `releaseFilters`, `releasePlugin`, `releaseRunner` can be run from on top level project to publish respective artifact  


## Spreading the word

- Star the repo
- Tweet about it (#GlideAppEngine #glide #gae)
- in every other possible way.
