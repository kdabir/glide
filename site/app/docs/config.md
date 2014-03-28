# Configuring & customizing a Glide app

Glide deliberately abstracts most of the GAE java web app configurations by setting them to intelligent defaults and
providing a way to override them when necessary. You don't need to edit/create multiple xml files in your app's
directory. The only file that are used are `glide.groovy`, `glide.gradle` (optional, if at all you want to
customize the build) and `_routes.groovy` (required by Gaelyk)

## Understanding `glide.groovy`

This file tells glide the application id/version to be deployed on GAE.
It can configure security, cron jobs, sitemesh (layout) configuration to just name a few.

### Setting app name and version

`glide.groovy`

    app {
        name = "testapp"
        version = "1"
    }


This will set the app name and version for deployment on Google App Engine. You must create the unique app id by
registering your app at [http://appengine.google.com](http://appengine.google.com)

A valid app id is required to deploy your app on Google App Engine. You can continue to develop locally without
registering an app.

This app closure takes many more Google App Engine's `appengine-web.xml` related settings like `public-root`,
`resource-files` etc. See [sample](/samples) apps.

### Configuring layouts (Sitemesh)

Layout section allows you to customize the url patterns to decorator mapping for sitemesh. It starts with top level
`layout` element in the `glide.groovy` file.

    layout {
        mappings = [
                "/*": "/_layout.gtpl"
        ]
        excludes = ["/install", "/install.html"]
    }


This is the example from the layout of documentation's `glide.groovy`. We want all url's to be decorated by
`_layout.gtpl` but the `/install` & `/install.html` urls (the installer shell script) to be excluded from decoration.


## Understanding `_routes.groovy`

This files is read by gaelyk to configure application routes. This is not glide specific. Check
[Gaelyk's routing](http://gaelyk.appspot.com/tutorial/url-routing) documentation to understand how routes work.

## Understanding `glide.gradle`

This file acts as extension to standard glide build. This is regular [gradle](http://www.gradle.org/) build file and you
can add `dependencies` or additional tasks just like any other gradle build file.