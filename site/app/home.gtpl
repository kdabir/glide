<!doctype html>
<html>

<head>
    <title>Home</title>
</head>

<body>


<div id="header-l">
  <div style="text-align: center; height: 350px"><img src="/img/glide_trans.png"></div>
</div>

<div class="container">
  <section id="about" class="row">
    <div class="col-sm-12 col-lg-12">

      <h2>What is Glide?</h2>

      <p class="about-glide">
        Glide makes it incredibly easy to develop apps that harness the power of
        Google App Engine for Java using expressiveness of <a href="http://groovy.codehaus.org">Groovy</a> and
        sweetness of <a href="http://gaelyk.appspot.com">Gaelyk</a>'s syntactic sugar.
      </p>

      <p class="about-glide">
        At it's core, Glide is a Gradle plugin that reduces all the boilerplate required with typical Java web applications
      </p>

    </div>
  </section>

  <section id="steps" class="row">
    <div class="col-sm-4 col-lg-4">
      <h3>Create</h3>

      <p class="app-desc">Create a minimal app with glide</p>

      <input type="text" class="cmd-text" value="glide -a myapp create" onClick="select(this);"/>

    </div>

    <div class="col-sm-4 col-lg-4">
      <h3>Run</h3>

      <p class="app-desc">Run this app</p>
      <input type="text" class="cmd-text" value="glide -a myapp run" onClick="select(this);"/>

    </div>

    <div class="col-sm-4 col-lg-4">
      <h3>Deploy</h3>

      <p class="app-desc">Deploy it on Google App engine</p>

      <input type="text" class="cmd-text" value="glide -a myapp deploy" onClick="select(this);"/>
    </div>
  </section>

  <section id="prev" class="row">
    <div class="col-sm-12 col-lg-12">

      <h3>Looking for previous version</h3>

      <p class="about-glide">
        Glide has substantially changed with latest releases, the docs for older release can be found at
         <a href="http://previous.glide-gae.appspot.com">Previous Docs</a>.
      </p>


    </div>
  </section>

</div>

</body>

</html>
