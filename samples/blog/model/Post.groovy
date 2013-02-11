package model
import com.google.appengine.api.users.User

@groovyx.gaelyk.datastore.Entity
class Post {
    String title
    String content
    Date date = new Date()
    User user
}
