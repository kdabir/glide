<!doctype html>
<html>

<head>
  <title>Docs</title>
</head>

<body>

 <div class="container">
   <div class="row">
     <div class="col-sm-2 col-lg-2">
       <ul class="nav nav-pills nav-stacked">
         <% ['installing', 'getting-started','quick-start','config', 'how-to', 'about'].each { docname ->
         def caption = docname.replaceAll(/[^a-zA-Z0-9]/, ' ').trim()
         def title = caption[0].toUpperCase() + caption[1..-1].toLowerCase()
         %>
         <li class="${docname==params.docname?'active':'inactive'}">
           <a href="${docname}">${title}</a>
         </li>
         <% } %>
       </ul>
     </div>
     <div class="docs-content col-sm-10 col-lg-10">
       <sitemesh:write property="body"/>
     </div>
   </div>
 </div>

</body>

</html>
