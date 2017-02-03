# Configuring & customizing a Glide app

Glide deliberately abstracts most of the GAE java web app configurations by setting them to intelligent defaults and
providing a way to override them when necessary. You don't need to edit/create multiple xml files in your app's
directory. The only file that are used are `glide.groovy` (optional), `build.gradle`  (only to apply plugin) and 
`_routes.groovy` (if we want pretty URLs)

## Understanding `glide.groovy`

This file tells glide the application id/version to be deployed on GAE.
It can configure security, cron jobs, sitemesh (layout) configuration to just name a few.

### Setting app name and version <a name="app_name_version"></a>

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

### Configuring layouts (Sitemesh) <a name="layout"></a>

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


### Securing application <a name="securing_app"></a>

Securing urls is dead simple, in the top level `web` block in glide.groovy you can set whether an URL is accessible
 to a logged in user `*` or to the `admin` (developers in general) of the website. See the example below

    web {
            security = [
                '*' : ['/profile/*', '/post/*'],
                'admin': ["/admin/*"]
            ]
    }


### Securing files <a name="securing_files"></a>

By default any file name that starts with an `_` (underscore) is treated as special file and can not be directly
accessed by user request unless it comes through a valid routes mapping. That's why in the layout example the layout
template is name `_layout.html` because we never want user to be able to access this file directly from browser. Same
holds true for `_routes.groovy`.

If you want to go for a stricter route matching such that only URL's matching a route in `_routes` file should be
allowed and user should not able access any file directly then set make this change in `glide.groovy`

    web {
        filters {
            protectedResourcesFilter {
                initParams = [strict: true]
            }
        }
    }

This website uses the same technique for strict url matching so you may refer to it's source to check how it is done.


### Environment specific config

sometimes a glide config is specific to an environment, it can we wrapped in `environments` closure.

    someKey = "prodValue"

    environments {
        prod {
            someKey = "prodValue"
        }
    }



## Understanding `_routes.groovy`

This files is read by gaelyk to configure application routes. This is not glide specific. Check
[Gaelyk's routing](http://gaelyk.appspot.com/tutorial/url-routing) documentation to understand how routes work.

## Understanding `build.gradle`

This is regular [gradle](http://www.gradle.org/) build file and we can add `dependencies` or additional tasks just 
like any other gradle build file.

An example of adding dependency in `glide.gradle`:

    dependencies {
        compile "com.github.rjeschke:txtmark:0.10"
    }

This example adds markdown processing library to glide. And this is actually how this site renders the markdown content.
