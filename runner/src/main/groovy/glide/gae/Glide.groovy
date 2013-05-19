package glide.gae

import glide.fs.Syncgine

/**
 *
 */
class Glide {

    AppEngine gae
    Syncgine syncgine

    Glide() {
        Syncgine.build {

        }
    }

}
