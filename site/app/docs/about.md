# About Glide

Google App Engine for Java has everything that is required to deploy a scalable web-app. Examples of such services
include Memcache, Task Queue, Email, Xmpp, NoSQL Datastore, Static files server and so on. But Java web-app directory
structure and verbose configuration files make it difficult to jumpstart.

Glide provides a dead simple way to create apps that run on Google App Engine and makes it incredibly easy to develop
apps that harness the power of Google App Engine for Java using expressiveness of [Groovy](http://groovy.codehaus.org)
and sweetness of [Gaelyk](http://gaelyk.appspot.com)'s syntactic sugar.

To cut the long story short, with glide you can create awesome apps on Google App Engine in a snap.

## Glide's Goal

Easier development of *simple* webapps and simple server side scripts on GAE/J


## What's great about glide

- Simple Web App structure
- (Almost) zero configuration. Don't believe it, check out the [samples](https://github.com/kdabir/glide/tree/master/samples) dir.
- Hot reloading - No more build, compile, restart-server phases.
- It's all standard java/groovy project under the hood. No lock-in. You can export your project in standard Java
    Gradle project any time.
- Layout templates works out of box (using Sitemesh)


## Why not standard Java web project for my Google App Engine project?

Nothing against the established stuff, just that it's too complicated for simple web application.

- Nested structure of java web-apps.
- Boilerplate setup/code/config
- Multiple locations where files have to be kept
- Verbose xml files
- Non standard layout (WTP) of Google App Engine Eclipse Plugin
- JDO, Data Neucleus. We don't need them for small apps
- Complex build systems, build files
- Dependencies hell

## When to use glide

 - Small to medium complexity apps
 - Prototyping
 - When time to market is key
 - Simplest possible thing that could work
 - When you expect simple project structure
 - Convention over configuration
 - Include and configure commonly needed stuff
 - Code as less as possible

> Glide is in active development and is not production ready yet. That said, we are using glide to power some real sites
> running on Google App Engine.

