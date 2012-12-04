package model

@groovyx.gaelyk.datastore.Entity
class Post {
    String title
    String content
    Date date = new Date()
}
