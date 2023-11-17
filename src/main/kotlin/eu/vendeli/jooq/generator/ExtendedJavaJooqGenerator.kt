package eu.vendeli.jooq.generator

import org.jooq.codegen.GeneratorStrategy
import org.jooq.codegen.JavaGenerator
import org.jooq.impl.DAOImpl
import org.jooq.meta.TableDefinition
import org.jooq.tools.JooqLogger
import java.io.File
import java.io.IOException

class ExtendedJavaJooqGenerator : JavaGenerator() {
    private val logger: JooqLogger = JooqLogger.getLogger(ExtendedJavaJooqGenerator::class.java)
    private val classLoader: ClassLoader = javaClass.classLoader

    override fun generateDao(table: TableDefinition) {
        super.generateDao(table)

        val file = getFile(table, GeneratorStrategy.Mode.DAO)
        if (file.exists()) {
            try {
                var fileContent = String(file.readBytes())

                fileContent = fileContent.replace(
                    "import org.jooq.impl.DAOImpl;\n",
                    "import eu.vendeli.jooq.impl.DAOExtendedImpl;\nimport org.jooq.DSLContext;\n",
                )
                fileContent = fileContent.replace(
                    " extends " + DAOImpl::class.java.getSimpleName(),
                    " extends eu.vendeli.jooq.impl.DAOExtendedImpl",
                )

                if (generateSpringAnnotations()) {
                    val dslContextSetting = "> {\n\t@Autowired\n\tvoid setCfg(Configuration configuration) {\n\t\t" +
                        "super.setConfiguration(configuration);\n\t}"
                    fileContent = fileContent.replace("> {", dslContextSetting)
                }

                file.writeBytes(fileContent.toByteArray())
            } catch (e: IOException) {
                logger.error("generateDao error: {}", file.absolutePath, e)
            }

            val daoExtdImplFile = File("${file.parentFile.parentFile.parentFile.path}/DAOExtendedImpl.kt")
            daoExtdImplFile.createNewFile()
            classLoader.getResource("DAOExtendedImpl.kt")?.openStream()?.copyTo(daoExtdImplFile.outputStream())
        }
    }
}
