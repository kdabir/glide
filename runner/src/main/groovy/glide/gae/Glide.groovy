package glide.gae

import glide.fs.Syncgine

/**
 * configures and starts gae and syncgine
 */
class Glide {

    AppEngine gae
    Syncgine syncgine

    Glide() {
        Syncgine.build {

        }
    }

}
