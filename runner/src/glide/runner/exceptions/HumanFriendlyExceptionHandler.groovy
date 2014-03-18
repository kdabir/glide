package glide.runner.exceptions

/**
 * Translates Glide Exceptions into User Readable Messages
 */
class HumanFriendlyExceptionHandler {

    static def wrap(Closure aClosureThatMayThrowAnException) {

        try {
            aClosureThatMayThrowAnException.call()
        } catch (InvalidGlideAppException e) {
            System.err.println e.message
        }catch (Exception e) {
            System.err.println "Duh! Something just went wrong"
            e.printStackTrace()
        } finally {
            // do something useful here
        }

    }

}
