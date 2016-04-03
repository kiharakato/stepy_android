package me.stepy.app

class Environment {
    companion object {
        public val IS_PRODUCTION = !BuildConfig.DEBUG
    }
}