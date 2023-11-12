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
        if (file.exists()) try {
            var fileContent = String(file.readBytes())

            val oldExtends = " extends " + DAOImpl::class.java.getSimpleName()
            val newExtends = " extends eu.vendeli.jooq.impl.DAOExtendedImpl"
            fileContent = fileContent.replace(
                "import org.jooq.impl.DAOImpl;\n",
                "import eu.vendeli.jooq.impl.DAOExtendedImpl;\n" +
                        "import org.jooq.DSLContext;\n",
            )
            fileContent = fileContent.replace(oldExtends, newExtends)

            if (generateSpringAnnotations()) {
                val dslContextSetting =
                    "> {\n\t@Autowired\n\tvoid dslCtxSetter(DSLContext ctx) {\n\t\tsuper.setDslContext(ctx);\n\t}"
                fileContent = fileContent.replace("> {", dslContextSetting)
            }

            file.writeBytes(fileContent.toByteArray())
        } catch (e: IOException) {
            logger.error("generateDao error: {}", file.absolutePath, e)
        }
        copyDaoExtdImpl()
    }

    private fun copyDaoExtdImpl() {
        val targetFile = File("$targetDirectory/${targetPackage.replace('.', '/')}/DAOExtendedImpl.kt")
        targetFile.createNewFile()
        classLoader.getResource("DAOExtendedImpl.kt")?.openStream()?.copyTo(targetFile.outputStream())
    }
}