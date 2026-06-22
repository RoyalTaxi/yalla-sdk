package uz.yalla.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Test

class PrimitivesPurityKonsistTest {
    @Test
    fun primitivesDoNotImportCoreDomain() {
        Konsist
            .scopeFromProject()
            .files
            .withPackage("uz.yalla.components.primitives..")
            .assertFalse { file ->
                file.hasImport { import ->
                    FORBIDDEN_DOMAIN_PREFIXES.any { prefix -> import.name.startsWith(prefix) }
                }
            }
    }

    private companion object {
        val FORBIDDEN_DOMAIN_PREFIXES =
            listOf(
                "uz.yalla.core.payment",
                "uz.yalla.core.identity",
                "uz.yalla.core.order",
                "uz.yalla.core.profile",
                "uz.yalla.core.location"
            )
    }
}
