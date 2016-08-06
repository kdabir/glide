## How to...


###  Add third-party libraries

As long as library is supported on AppEngine, we can just add `dependencies` in `build.gradle` just like any other Gradle project.   

    dependencies {
      compile 'com.foo:foo:1.2.3'
    }
  
  
> repositories like `mavenCentral` and `jcenter` are already configured for project by Glide.
  
  
### Open project in IntelliJ IDEA

Add following lines to `build.gradle`, and get all the IDE goodness for our groovlets in `app` dir  
 
    apply plugin: 'idea'
    
    idea {
       module {
           sourceDirs += file('app')
       }
    }


> We might need to do a project sync to get the changes to `build.gradle` noticed by IDEA.


