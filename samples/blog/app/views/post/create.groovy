package views.post

import model.Post

new Post(title: params.title, content: params.content, user: users.currentUser).save()

redirect "/"
