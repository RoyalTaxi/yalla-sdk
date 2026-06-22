package uz.yalla.telemetry.crash

public interface CrashReporter {
    public fun record(throwable: Throwable)

    public fun setUser(userId: String?)

    public object Noop : CrashReporter {
        override fun record(throwable: Throwable) {}

        override fun setUser(userId: String?) {}
    }
}
