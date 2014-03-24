import com.google.appengine.api.users.User
import groovyx.gaelyk.datastore.Indexed

@groovyx.gaelyk.datastore.Entity
class Transaction {
    @Indexed String description
    @Indexed Double amount
    @Indexed  Date date
    Date dateAdded = new Date()
    User user
}
