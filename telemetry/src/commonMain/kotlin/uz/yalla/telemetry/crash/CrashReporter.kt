package uz.yalla.telemetry.crash

interface CrashReporter {
    fun record(throwable: Throwable)
    fun setUser(userId: String?)

    object Noop : CrashReporter {
        override fun record(throwable: Throwable) {}
        override fun setUser(userId: String?) {}
    }
}
