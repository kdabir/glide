package model
import com.google.appengine.api.users.User
import groovyx.gaelyk.datastore.Indexed

@groovyx.gaelyk.datastore.Entity
class Post {
    String title
    String content
    @Indexed Date date = new Date()
    User user
}
