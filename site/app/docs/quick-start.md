# Getting Started

So you have [installed](/docs/installing) glide and are all set for your first glide in the cloud.

1. Glide can create an empty project for you, just fire:

        $ glide --app hello-glide create

   Creates a directory for your project `hello-glide`

2. `cd` into this directory

        $ cd hello-glide

3. Fasten your seat-belts and fire up `glide` command

        $ glide

5. Open up browser and go to [http://localhost:8080](http://localhost:8080)

    ** Congrats! You are running glide locally on Google App Engine local dev server. **

    >Yes! no verbose config, nothing else. Your web content is being served.

## Making changes

A Typically glide app looks like following:

    glide-project
    |-- app
    |   |-- _routes.groovy
    |   `-- ....
    |-- glide.groovy
    |-- glide.gradle


You can create groovlets/gtpl file in `app` dir as well as keep regular html / css and js stuff there.


## Deploying on the Google App Engine

1. To deploy this app on Google App Engine, first [register](http://appengine.google.com) your app and get an app id.

    Let's say you registered app with id `your-glide-app-id`

2. Update the file called `glide.groovy` in the `hello-glide` directory with content:

        app {
            name = "your-glide-app-id"
            version ="1"
        }


3.  From your project dir (hello-glide) fire up

    `glide deploy`

     > If this is first time you are deploying app on Google App Engine, you will be redirected to oauth2 token page.
     > Copy and paste that token on command line.

4. Open up browser and go to  `http://your-glide-app-id.appspot.com`

    > don't forget the change the 'your-glide-app-id' with actual app id.

    ** congrats, your app is live on the Google App Engine **


## Exporting App to a standard Java web app:

You can export a glide app to a standard Java EE web app along with gradle build file.

     glide -o ../myapp-export export

> Make sure you specify the output path thats not inside your glide project
