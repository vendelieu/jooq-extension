package eu.vendeli.jooq.generator

import org.jooq.codegen.GeneratorStrategy
import org.jooq.codegen.KotlinGenerator
import org.jooq.impl.DAOImpl
import org.jooq.meta.TableDefinition
import org.jooq.tools.JooqLogger
import java.io.File
import java.io.IOException

class ExtendedKotlinJooqGenerator : KotlinGenerator() {
    private val logger: JooqLogger = JooqLogger.getLogger(ExtendedKotlinJooqGenerator::class.java)

    override fun generateDao(table: TableDefinition) {
        super.generateDao(table)

        File("$targetDirectory/DAOExtendedImpl.kt").takeIf {
            !it.exists()
        }?.also { targetFile ->
            targetFile.createNewFile()
            javaClass.classLoader.getResource("DAOExtendedImpl.kt")!!.openStream().use {
                targetFile.writeBytes(it.readBytes())
            }
        }

        val file = getFile(table, GeneratorStrategy.Mode.DAO)
        if (file.exists()) {
            try {
                var fileContent = file.readBytes().toString(Charsets.UTF_8)

                fileContent =
                    fileContent.replace(
                        "import org.jooq.impl.DAOImpl\n",
                        "import org.springframework.beans.factory.annotation.Autowired\n",
                    )
                fileContent =
                    fileContent.replace(
                        " : " + DAOImpl::class.java.getSimpleName(),
                        " : eu.vendeli.jooq.impl.DAOExtendedImpl",
                    )

                if (generateSpringAnnotations()) {
                    val dslContextSetting =
                        ") {\n\t@Autowired\n\tfun setCfg(configuration: Configuration) {\n\t\t" +
                            "super.setConfiguration(configuration)\n\t}"
                    fileContent = fileContent.replace(") {", dslContextSetting)
                }

                file.writeBytes(fileContent.toByteArray())
            } catch (e: IOException) {
                logger.error("generateDao error: {}", file.absolutePath, e)
            }
        }
    }
}
