<!DOCTYPE html>

<% def errorCode = request.errorCode ?: "Oops" %>
<% def errorMessage = request.errorMessage ?: "Something just went wrong" %>

<html lang="en">

<head>
  <meta charset="utf-8">
  <meta http-equiv="content-type" content="text/html; charset=UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <meta name="description" content="error">
  <meta name="author" content="glide">
  <meta name="keywords" lang="en" content="glide, groovy, gaelyk, google app engine, java, gae, sitemesh">

  <link rel='stylesheet' href="//netdna.bootstrapcdn.com/bootswatch/2.3.0/spacelab/bootstrap.min.css"/>
  <link rel="stylesheet" href="//netdna.bootstrapcdn.com/font-awesome/3.0.2/css/font-awesome.css"/>

  <title>${errorCode}</title>

  <style>
    body {
      padding-top: 20px;
      padding-bottom: 40px;
    }

    .container-narrow {
      margin: 0 auto;
      max-width: 700px;
    }

    .container-narrow > hr {
      margin: 30px 0;
    }

    .message {
      margin: 60px 0;
      text-align: center;
    }

    .message h1 {
      font-size: 72px;
      line-height: 1;
      font-weight: bold;
      margin-bottom: 75px;
    }

    .message .btn {
      font-size: 21px;
      padding: 14px 24px;
    }
  </style>

</head>

<body>
<div class="container-narrow">
  <div class="masthead">
    <h3 class="muted">
      ${app.id}
    </h3>
  </div>
  <hr>
  <div class="message">
    <h1>${errorCode}!</h1>

    <p class="lead">${errorMessage}</p>

    <p class="muted">You can try checking home page</p>
    <a class="btn btn-large btn-success" href="/">Home</a>
  </div>
</div>
</body>

</html>
