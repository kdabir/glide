
log.info new File("docs/${params.docname}.md").absolutePath
log.info new File("docs/${params.docname}.md").exists().toString()

out.println  com.github.rjeschke.txtmark.Processor.process(new File("docs/${params.docname}.md").text)


