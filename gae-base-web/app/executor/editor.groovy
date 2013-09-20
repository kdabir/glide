html.html {
    body {
        h1 "Glide Live Console"
        div(class: "editor") {
            form(method: "POST", action: "/executor") {
                textarea(name: "script", rows: 20, cols: 80, params.script)
                br()
                input(type: "submit")
            }
            i((request.executionResult) ? request.executionResult : '')
            i((request.outputText) ? request.outputText : '')
            i((request.stacktraceText) ? request.executionResult : '')
        }
    }
}

