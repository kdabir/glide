import groovyx.gaelyk.GaelykBindings


@GaelykBindings
class HelloService {
    def greet (name) {
        def message = new Message(content:"hello $name!",date: new Date())
        message.save()
        message.content
    }

    def greetCount () {
    	Message.count()	
    }

}