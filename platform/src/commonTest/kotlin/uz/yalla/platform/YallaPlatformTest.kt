package uz.yalla.platform

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

private class FakePlatformConfig : PlatformConfig
private class OtherPlatformConfig : PlatformConfig

class YallaPlatformTest {

    private fun resetPlatform() {
        YallaPlatform.reset()
    }

    @Test
    fun shouldNotBeInstalledByDefault() {
        resetPlatform()
        assertFalse(YallaPlatform.isInstalled)
    }

    @Test
    fun shouldBeInstalledAfterInstall() {
        resetPlatform()
        YallaPlatform.install(FakePlatformConfig())
        assertTrue(YallaPlatform.isInstalled)
    }

    @Test
    fun shouldClearConfigOnReset() {
        resetPlatform()
        YallaPlatform.install(FakePlatformConfig())
        assertTrue(YallaPlatform.isInstalled)

        YallaPlatform.reset()
        assertFalse(YallaPlatform.isInstalled)
    }

    @Test
    fun shouldThrowOnRequireConfigWhenNotInstalled() {
        resetPlatform()
        val exception = assertFailsWith<IllegalStateException> {
            YallaPlatform.requireConfig<FakePlatformConfig>()
        }
        assertTrue(exception.message!!.contains("YallaPlatform not installed"))
    }

    @Test
    fun shouldReturnConfigOnRequireConfigWhenInstalled() {
        resetPlatform()
        val config = FakePlatformConfig()
        YallaPlatform.install(config)
        val retrieved = YallaPlatform.requireConfig<FakePlatformConfig>()
        assertEquals(config, retrieved)
    }

    @Test
    fun shouldThrowOnRequireConfigWithWrongType() {
        resetPlatform()
        YallaPlatform.install(FakePlatformConfig())
        val exception = assertFailsWith<IllegalStateException> {
            YallaPlatform.requireConfig<OtherPlatformConfig>()
        }
        assertTrue(exception.message!!.contains("YallaPlatform not installed"))
    }

    @Test
    fun shouldAllowReinstallWithNewConfig() {
        resetPlatform()
        val first = FakePlatformConfig()
        val second = FakePlatformConfig()

        YallaPlatform.install(first)
        assertEquals(first, YallaPlatform.requireConfig<FakePlatformConfig>())

        YallaPlatform.install(second)
        assertEquals(second, YallaPlatform.requireConfig<FakePlatformConfig>())
    }
}
