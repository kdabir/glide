# Getting Started

At its heart, Glide is just a gradle plugin. It helps to understand some Gradle to customize Glide, however it is NOT required
 to know Gradle to start using glide.   


## Prerequisites

1. JDK 1.7 +

2. Gradle 2.13+ 


## Creating first app

1. Create a directory for our project, lets call it a `hello-glide`
      
        mkdir hello-glide && cd hello-glide
      
2. Create a file called `build.gradle` in this directory
      
        touch build.gradle


3. Apply the glide plugin. Just copy paste the following snippet in build.gradle       

        plugins {
          id "com.appspot.glide-gae" version "0.9.3"
        }

4. Create a directory `app`, this is where our controllers (groovelets) and other web resources will go
    
    
        mkdir app && cd app


5. Create a file `hello.groovy` in `app` with following content
 
        println "hello, world!"

  
6. Run app, from `hello-glide` (the project root) 

        gradle appRun
        

7. Open http://localhost:8080/index.groovy in browser (and try changing the groovy file).        


  
