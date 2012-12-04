package post

import model.Post

new Post(title: params.title, content: params.content).save()

redirect "/"
