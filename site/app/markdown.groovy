import com.github.rjeschke.txtmark.Processor as Markdown

log.info new File("docs/${params.docname}.md").absolutePath
log.info new File("docs/${params.docname}.md").exists().toString()

out.println  Markdown.process(new File("docs/${params.docname}.md").text)


