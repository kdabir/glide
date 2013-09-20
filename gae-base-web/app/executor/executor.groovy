package executor
// shameless ripoff from groovy console :)

import org.codehaus.groovy.control.MultipleCompilationErrorsException


def scriptText = params.script ?: "'The received script was null.'"

def encoding = 'UTF-8'
def stream = new ByteArrayOutputStream()

def stacktrace = new StringWriter()
def errWriter = new PrintWriter(stacktrace)


def result = ""
try {
    result = new GroovyShell(binding).evaluate(scriptText)
} catch (MultipleCompilationErrorsException e) {
    stacktrace.append(e.message - 'startup failed, Script1.groovy: ')
} catch (Throwable t) {
    sanitizeStacktrace(t)
    def cause = t
    while (cause = cause?.cause) { sanitizeStacktrace(cause)  }
    t.printStackTrace(errWriter)
}

request.executionResult = escape(result)
request.outputText = escape(stream.toString(encoding))
request.stacktraceText= escape(stacktrace)

forward "/executor/editor.groovy"

def escape(object) {
    object ? object.toString().replaceAll(/\n/, /\\\n/).replaceAll(/\t/, /\\\t/).replaceAll(/"/, /\\"/) : ""
}

def sanitizeStacktrace(t) {
    def filtered = [
            'com.google.', 'org.mortbay.', 'java.', 'javax.', 'sun.', 'groovy.', 'org.codehaus.groovy.', 'groovyx.gaelyk.', 'executor'
    ]
    def trace = t.stackTrace
    def newTrace = []
    trace.each { stackTraceElement ->
        if (filtered.every { !stackTraceElement.className.startsWith(it) }) {
            newTrace << stackTraceElement
        }
    }
    def clean = newTrace.toArray(newTrace as StackTraceElement[])
    t.stackTrace = clean
}