<!doctype html>
<html>

<head>
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <title>Glide - <sitemesh:write property='title'/></title>

  <meta name="generator" content="glide">
  <meta name="powered-by" content="glide">

  <meta name="keywords" lang="en" content="glide, groovy, gaelyk, google app engine, java, gae, sitemesh">
  <meta name="description" content="Create Awesome Apps on Google App Engine in a Snap" />

  <!-- Twitter Card -->
  <meta name="twitter:card" content="summary">
  <meta name="twitter:site" content="@kdabir">
  <meta name="twitter:creator" content="@kdabir">
  <meta name="twitter:title" content="Glide - Create Awesome Apps on Google App Engine">
  <meta name="twitter:description" content="Glide makes it incredibly easy to develop apps that harness the power of Google App Engine for Java using expressiveness of Groovy and sweetness of Gaelyk's syntactic sugar.">
  <meta name="twitter:image" content="https://glide-gae.appspot.com/img/glide_25x.png">

  <!-- Open Graph -->
  <meta property="og:title" content="Glide" />
  <meta property="og:type" content="website" />
  <meta property="og:url" content="http://glide-gae.appspot.com" />
  <meta property="og:image" content="https://glide-gae.appspot.com/img/glide_25x.png" />
  <meta property="og:description" content="Create Awesome Apps on Google App Engine in a Snap" />

  <link rel="stylesheet" href="//netdna.bootstrapcdn.com/bootswatch/3.1.1/flatly/bootstrap.min.css">
  <link rel="stylesheet" href="//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.min.css">

  <!-- included end to override bootstrap values-->
  <link rel="stylesheet" href="/css/app.css">

  <% def footerHeight = "75px" %>
  <style type="text/css">
    /* Sticky footer styles */
    html, body {height: 100%;}
    #wrap {  min-height: 100%; height: auto !important; height: 100%; margin: 0 auto -${footerHeight}; }
    #push, #footer {height: ${footerHeight}; }
    @media (max-width: 767px) {
      #footer { margin-left: -20px; margin-right: -20px; padding-left: 20px; padding-right: 20px; }
    }/* Sticky footer styles end*/
    #footer {
      margin-top: 50px;
    }
    #wrap > .container {
      padding-top: ${footerHeight};
    }
  </style>

 <sitemesh:write property="head"/>
</head>

<body>
  <div id="wrap">
    <!-- nav -->
    <div class="navbar navbar-default navbar-fixed-top" role="navigation">
      <div itemscope itemtype="http://schema.org/SoftwareApplication" class="container">
        <div class="navbar-header">
          <a class="navbar-brand" href="/"><span itemprop="name">Glide</span></a>
          <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
        </div>

        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <% [
                "home": [href: "/", label: "Home", itemprop: "url"],
                "docs": [href: "/docs", label: "Docs"],
                "samples": [href: "/samples", label: "Samples"],
                ].each { key, value -> %>
            <li class="${key==params.page?'active':'inactive'}"><a itemprop="${value.itemprop}" href="${value.href}">${value.label}</a></li>
            <%}%>

          </ul>
          <ul class="nav navbar-nav navbar-right">
            <li><a href="https://groups.google.com/forum/#!forum/glide-groovy">Support</a></li>
            <li><a href="http://github.com/kdabir/glide/issues">Issues</a></li>
            <li><a href="http://github.com/kdabir/glide">Source</a></li>
          </ul>
        </div>
      </div>
    </div>


    <sitemesh:write property="body"/>

    <div id="push"></div>

  </div>

  <div id="footer">
    <div class="container">
      <div class="row">
        <p class="span12 small mute text-center">
          <iframe src="http://ghbtns.com/github-btn.html?user=kdabir&repo=glide&type=watch&count=true" allowtransparency="true" frameborder="0" scrolling="0" width="110" height="20"></iframe>

          <iframe src="http://ghbtns.com/github-btn.html?user=kdabir&repo=glide&type=fork&count=true" allowtransparency="true" frameborder="0" scrolling="0" width="95" height="20"></iframe>

        </p>
      </div>

      <div class="row">
        <p class="span12 small mute text-center">
          This website is powered by <a href="/">glide</a>
        </p>
      </div>

      <div class="row">
        <p id="logos" class="span12 mute text-center">
          <a id="gae-logo" href="http://appengine.google.com"><img class="grayscale" src="/img/gae-42x32.png"></a>
          <a id="gaelyk-logo" href="http://gaelyk.appspot.com"><img class="grayscale" src="/img/gaelyk-32.png"></a>
          <a id="groovy-logo" href="http://groovy.codehaus.org"><img class="grayscale" src="/img/groovy-64x32.png"></a>
          <a id="gradle-logo" href="http://gradle.org"><img class="grayscale" src="/img/gradle-32.png"></a>
          <a id="git-logo" href="http://git-scm.com"><img class="grayscale" src="/img/git-32.png"></a>
          <a id="gh-mark-logo" href="http://github.com"><img class="grayscale" src="/img/github-mark-32.png"></a>
        </p>
      </div>
    </div>
  </div>

  <script src="/js/app.js"></script>
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
  <script src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
  <script>
    (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
        m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
    })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

    ga('create', 'UA-5123395-10', 'glide-gae.appspot.com');
    ga('send', 'pageview');
  </script>

</body>

</html>