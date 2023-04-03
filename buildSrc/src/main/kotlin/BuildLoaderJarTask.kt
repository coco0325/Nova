import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import org.bukkit.configuration.file.YamlConfiguration
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultExternalModuleDependency
import org.gradle.api.internal.artifacts.repositories.DefaultMavenArtifactRepository
import org.gradle.api.internal.artifacts.repositories.DefaultMavenLocalArtifactRepository
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.component.external.model.DefaultModuleComponentIdentifier
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.named
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.appendText
import kotlin.io.path.copyTo
import kotlin.io.path.createDirectories
import kotlin.io.path.inputStream
import kotlin.io.path.name
import kotlin.io.path.writeText

private const val MAVEN_CENTRAL = "https://repo1.maven.org/maven2/"

abstract class BuildLoaderJarTask : DefaultTask() {
    
    private val mojangMapped = project.hasProperty("mojang-mapped") || System.getProperty("mojang-mapped") != null
    
    @get:Input
    lateinit var nova: Project
    
    @get:Input
    lateinit var novaAPI: Project
    
    @get:Input
    lateinit var novaLoader: Project
    
    @OptIn(ExperimentalPathApi::class)
    @TaskAction
    fun run() {
        val novaFile = getOutputFile(nova)
        val novaApiFile = getOutputFile(novaAPI)
        val novaLoaderFile = getOutputFile(novaLoader)
        
        // copy loader jar to out jar
        val bundlerFile = File(project.buildDir, "Nova-${project.version}.jar").toPath()
        bundlerFile.parent.createDirectories()
        novaLoaderFile.copyTo(bundlerFile, true)
        
        // include api classes
        // TODO: replace with the following code once this runs on kotlin 1.8.20
        // apiZipRoot.forEachDirectoryEntry { it.copyToRecursively(bundlerZipRoot.resolve(it.name), followLinks = false) }
        //<editor-fold desc="include api classes", defaultstate="collapsed">
        val bundlerZip = ZipFile(bundlerFile.toFile())
        val apiZip = ZipFile(novaApiFile.toFile())
        apiZip.fileHeaders.forEach {
            if (it.isDirectory)
                return@forEach
            
            bundlerZip.addStream(apiZip.getInputStream(it), ZipParameters().apply { fileNameInZip = it.fileName })
        }
        bundlerZip.close()
        apiZip.close()
        //</editor-fold>
        
        val bundlerZipFs = FileSystems.newFileSystem(bundlerFile)
        val apiZipFs = FileSystems.newFileSystem(novaApiFile)
        val bundlerZipRoot = bundlerZipFs.rootDirectories.first()
        val apiZipRoot = apiZipFs.rootDirectories.first()
        
        // include nova.jar
        novaFile.copyTo(bundlerZipRoot.resolve("nova.jar"))
        // generate libraries.yml
        bundlerZipRoot.resolve("libraries.yml").writeText(generateNovaLoaderLibrariesYaml().saveToString())
        // add spigot loader libraries to plugin.yml
        bundlerZipRoot.resolve("plugin.yml").appendText("\n" + generateSpigotLoaderLibrariesYaml().saveToString())
        
        // close zip files
        bundlerZipFs.close()
        apiZipFs.close()
        
        // copy to custom output directory
        val customOutDir = (project.findProperty("outDir") as? String)?.let(::File)
            ?: System.getProperty("outDir")?.let(::File)
            ?: return
        customOutDir.mkdirs()
        val copyTo = File(customOutDir, bundlerFile.name)
        bundlerFile.inputStream().use { ins -> copyTo.outputStream().use { out -> ins.copyTo(out) } }
    }
    
    private fun generateNovaLoaderLibrariesYaml(): YamlConfiguration {
        val librariesYml = YamlConfiguration()
        
        librariesYml["repositories"] = project.repositories.asSequence()
            .filterIsInstance<DefaultMavenArtifactRepository>()
            .filter { it !is DefaultMavenLocalArtifactRepository }
            .mapTo(HashSet()) { it.url.toString() }
            .apply { this -= MAVEN_CENTRAL }
            .toList() // Required for proper serialization
        println()
        
        setLibraries(librariesYml, "novaLoader", true)
        excludeConfiguration(librariesYml, "spigotRuntime")
        
        return librariesYml
    }
    
    private fun generateSpigotLoaderLibrariesYaml(): YamlConfiguration {
        val cfg = YamlConfiguration()
        setLibraries(cfg, "spigotLoader", false)
        return cfg
    }
    
    @Suppress("SENSELESS_COMPARISON") // it isn't
    private fun setLibraries(cfg: YamlConfiguration, configuration: String, writeExclusions: Boolean) {
        cfg["libraries"] = project.configurations.getByName(configuration)
            .incoming.dependencies.asSequence()
            .filterIsInstance<DefaultExternalModuleDependency>()
            .mapTo(ArrayList()) { dep ->
                val coords = getArtifactCoords(dep)
                val excludeRules = dep.excludeRules
                if (excludeRules.isNotEmpty()) {
                    val exCfg = YamlConfiguration()
                    exCfg["library"] = coords
                    exCfg["exclusions"] = excludeRules.map {
                        require(it.group != null && it.module != null) { "Exclusion rules need to specify group and module" }
                        "${it.group}:${it.module}::jar"
                    }
                    
                    return@mapTo exCfg
                }
                
                return@mapTo coords
            }
        
        if (writeExclusions) {
            val exclusions = cfg.getStringList("exclusions")
            exclusions += project.configurations.getByName(configuration).excludeRules.map { "${it.group}:${it.module}" }
            cfg["exclusions"] = exclusions
        }
    }
    
    private fun excludeConfiguration(cfg: YamlConfiguration, configuration: String) {
        val exclusions = cfg.getStringList("exclusions")
        
        exclusions += project.configurations.getByName(configuration)
            .incoming.artifacts.artifacts.asSequence()
            .map { it.variant.owner }
            .filterIsInstance<DefaultModuleComponentIdentifier>()
            .map { "${it.group}:${it.module}" }
        
        cfg["exclusions"] = exclusions
    }
    
    private fun getArtifactCoords(dependency: DefaultExternalModuleDependency): String {
        val artifact = dependency.artifacts.firstOrNull()?.takeUnless { it.classifier == "remapped-mojang" && !mojangMapped }
        return if (artifact != null)
            "${dependency.group}:${dependency.name}:${artifact.extension}:${artifact.classifier}:${dependency.version}"
        else "${dependency.group}:${dependency.name}:${dependency.version}"
    }
    
    private fun getOutputFile(project: Project): Path {
        return getOutputFile(project.tasks.named<Jar>("jar").get())
    }
    
    private fun getOutputFile(jar: Jar): Path {
        val dir = jar.destinationDirectory.get().asFile
        var name = listOf(
            jar.archiveBaseName.orNull ?: "",
            jar.archiveAppendix.orNull ?: "",
            jar.archiveVersion.orNull ?: "",
            jar.archiveClassifier.orNull ?: ""
        ).filterNot(String::isBlank).joinToString("-")
        jar.archiveExtension.orNull?.let { name += ".$it" }
        
        return File(dir, name).toPath()
    }
    
}