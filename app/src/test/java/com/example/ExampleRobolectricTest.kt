package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.models.BuildStatus
import com.example.data.models.SelfHealingStatus
import com.example.data.repository.LuxRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Studio LuX", appName)
    }

    @Test
    fun `verify lux repository projects and cli commands`() = runBlocking {
        val repository = LuxRepository()
        val projects = repository.projects.value
        assertTrue(projects.isNotEmpty())
        assertEquals("OmniInventory Pro", projects.first().name)

        // Test status command
        val statusResult = repository.executeCliCommand("status")
        assertTrue(statusResult.contains("LUX META-DEVOS STATUS REPORT"))

        // Test antigravity command
        val antigravityResult = repository.executeCliCommand("antigravity --run \"diagnostico rapido\"")
        assertTrue(antigravityResult.contains("Antigravity CLI"))

        // Test fix command
        val fixResult = repository.executeCliCommand("fix --last-error")
        assertTrue(fixResult.contains("Hotfix aplicado"))

        // Test db stats and share commands
        val dbResult = repository.executeCliCommand("db --stats")
        assertTrue(dbResult.contains("ROOM DATABASE STATS"))

        val shareResult = repository.executeCliCommand("share --apk")
        assertTrue(shareResult.contains("Invocación de compartición"))

        // Test new app generation
        val newApp = repository.createNewAppFromPrompt(
            prompt = "App de control de gastos personales con persistencia local Room",
            appName = "GastoSmart",
            category = "Fintech"
        )
        assertNotNull(newApp)
        assertEquals("GastoSmart", newApp.name)
        assertEquals(BuildStatus.READY, newApp.status)
        assertTrue(newApp.generatedFiles.any { it.fileName == "MainActivity.kt" })
    }

    @Test
    fun `verify self-healing simulation and telemetry`() = runBlocking {
        val repository = LuxRepository()
        val initialTask = repository.activeSelfHealingTask.value
        assertNotNull(initialTask)
        assertEquals("SyncManager.kt", initialTask?.affectedFile)

        // Trigger simulation
        val task = repository.triggerSelfHealingSimulation()
        assertNotNull(task)
        assertEquals(SelfHealingStatus.RESTORED, task.status)
        assertTrue(task.patchDiffAfter.contains("return@onMessage"))
        assertTrue(repository.healingHistory.value.isNotEmpty())
    }

    @Test
    fun `verify activity terminal stateflow log injection`() = runBlocking {
        val repository = LuxRepository()
        val initialCount = repository.cliLogs.value.size

        // Inject new real-time log into StateFlow
        repository.addCliLog("Test log injected for ActivityTerminal", com.example.data.models.CliLogType.AGENT, "TEST_TAG")

        val updatedLogs = repository.cliLogs.value
        assertEquals(initialCount + 1, updatedLogs.size)
        assertEquals("Test log injected for ActivityTerminal", updatedLogs.last().text)
    }

    @Test
    fun `verify antigravity cli bridge and patch management`() = runBlocking {
        val logs = mutableListOf<String>()
        val cli = com.example.data.engine.AntigravityCLI { text, _, _ -> logs.add(text) }

        // Test monitor status
        val monitorRes = cli.execute("monitor status")
        assertTrue(monitorRes.contains("REPORTE DE TELEMETRÍA DEL SISTEMA OPERATIVO"))
        assertEquals(100.0, cli.kernelHealth.value.crashFreeRate, 0.01)

        // Test patch list
        val patchListRes = cli.execute("patch list")
        assertTrue(patchListRes.contains("REGISTRO DE PARCHES"))

        // Test patch apply
        val patchApplyRes = cli.execute("patch apply UserRepository.kt Añadir caché en memoria")
        assertTrue(patchApplyRes.contains("aplicado con éxito"))

        // Test antigravity run
        val runRes = cli.execute("antigravity run \"test inference\"")
        assertTrue(runRes.contains("Inferencia completada"))

        // Test auto-correction engine cycle directly
        val autoCorrectTask = cli.autoCorrectionEngine.executeAutoCorrectionCycle()
        assertEquals(SelfHealingStatus.RESTORED, autoCorrectTask.status)
        assertTrue(cli.autoCorrectionEngine.registeredPatches.value.isNotEmpty())
    }

    @Test
    fun `verify recovery mode trigger upon error detection in log stream`() = runBlocking {
        val repository = LuxRepository()

        // Inject simulated fatal error into log stream
        repository.addCliLog(
            "FATAL ERROR: IllegalStateException in EventBus dispatch",
            com.example.data.models.CliLogType.ERROR,
            "CRASH_WATCHER"
        )

        // Verify error is present in stream
        val hasErrors = repository.cliLogs.value.any { it.type == com.example.data.models.CliLogType.ERROR }
        assertTrue(hasErrors)

        // Trigger recovery mode
        val restoredTask = repository.triggerSelfHealingSimulation()
        assertNotNull(restoredTask)
        assertEquals(SelfHealingStatus.RESTORED, restoredTask.status)
    }

    @Test
    fun verifyApkBuilderServiceGradleCompilationAndConsoleStatusReports() {
        runBlocking {
            val logs = mutableListOf<String>()
            val builderService = com.example.data.engine.ApkBuilderService { text, _, _ -> logs.add(text) }
            val testProject = com.example.data.models.GeneratedAppProject(
                name = "TestApp",
                packageName = "com.lux.apps.test",
                description = "Test description",
                category = "Utility",
                version = "1.0.0",
                status = BuildStatus.READY,
                prompt = "Test prompt",
                generatedFiles = emptyList()
            )

            val report = builderService.executeBuild(testProject, "Release")
            assertNotNull(report)
            assertEquals("TestApp", report.appName)
            assertEquals("SUCCESS", report.status)
            assertTrue(report.tasksExecuted.isNotEmpty())
            assertTrue(report.sha256Checksum.isNotEmpty())

            val consoleReport = builderService.generateStatusReport(report)
            assertTrue(consoleReport.contains("REPORTE DE ESTADO DE COMPILACIÓN GRADLE"))
            assertTrue(consoleReport.contains("TestApp"))
            assertTrue(consoleReport.contains("com.lux.apps.test"))
        }
    }

    @Test
    fun verifyTtsServiceWithGeminiFlashTts() {
        runBlocking {
            val repository = LuxRepository()
            val ttsResult = repository.executeCliCommand("tts \"Prueba de locución de log\"")
            assertTrue(ttsResult.contains("gemini-3.8-flash-tts"))
            assertTrue(repository.cliLogs.value.any { it.tag == "AUDIO_SPARK" || it.text.contains("gemini-3.8-flash-tts") })
        }
    }

    @Test
    fun verifyFileDownloaderSha256IntegrityCalculationAndVerification() {
        runBlocking {
            val testDir = java.io.File(System.getProperty("java.io.tmpdir"), "test_gemma_models")
            if (!testDir.exists()) testDir.mkdirs()
            val downloader = com.example.data.engine.FileDownloader(testDir)

            val testFile = java.io.File(testDir, "test_weights.bin")
            testFile.writeText("Gemma-4-OnDevice-Quantized-Model-Weights-Test-Payload")

            val calculatedSha256 = downloader.calculateFileSha256(testFile)
            assertNotNull(calculatedSha256)
            assertEquals(64, calculatedSha256.length)

            // Integrity verification
            val isMatch = downloader.verifyFileIntegrity(testFile, calculatedSha256)
            assertTrue(isMatch)

            // Tampered checksum should fail
            val isTampered = downloader.verifyFileIntegrity(testFile, "0000000000000000000000000000000000000000000000000000000000000000")
            assertFalse(isTampered)
        }
    }

    @Test
    fun verifyGemmaModelManagerCatalogAndCliLoadSafety() {
        runBlocking {
            val repository = LuxRepository()
            val modelManager = repository.gemmaModelManager

            val models = modelManager.models.value
            assertTrue(models.isNotEmpty())
            assertTrue(models.any { it.id == "gemma-micro-q4" })

            // gemma-micro-q4 is pre-verified and active
            val activeModel = modelManager.activeModel.value
            assertNotNull(activeModel)
            assertEquals("gemma-micro-q4", activeModel?.id)
            assertTrue(activeModel?.isIntegrityVerified == true)

            // Loading an unverified, not-downloaded model should be rejected for safety
            val loadResult = modelManager.loadModelIntoCli("gemma-2b-it-int4")
            assertFalse(loadResult)
        }
    }

    @Test
    fun verifyAntigravityCliModelsAndTelemetryCommands() {
        runBlocking {
            val repository = LuxRepository()

            // Test models command
            val modelsOutput = repository.executeCliCommand("antigravity models")
            assertTrue(modelsOutput.contains("CATÁLOGO LOCAL DE PESOS GEMMA"))
            assertTrue(modelsOutput.contains("gemma-micro-q4"))

            // Test verify command
            val verifyOutput = repository.executeCliCommand("antigravity verify gemma-micro-q4")
            assertTrue(verifyOutput.contains("SHA-256") || verifyOutput.contains("Integridad"))

            // Test telemetry command
            val telemetryOutput = repository.executeCliCommand("antigravity telemetry")
            assertTrue(telemetryOutput.contains("DASHBOARD DE TELEMETRÍA EN TIEMPO REAL"))
            assertTrue(telemetryOutput.contains("Tasa de Errores"))
            assertTrue(telemetryOutput.contains("Latencia Compilación"))
        }
    }

    @Test
    fun verifyRechartsTelemetryMetricsCalculation() {
        runBlocking {
            val repository = LuxRepository()
            // Inject sample logs
            repository.addCliLog("Test info log", com.example.data.models.CliLogType.INFO)
            repository.addCliLog("Test error log", com.example.data.models.CliLogType.ERROR)
            repository.addCliLog("Test success log", com.example.data.models.CliLogType.SUCCESS)

            val metrics = repository.getTelemetryMetrics()
            assertNotNull(metrics)
            assertTrue(metrics.totalLogs >= 3)
            assertTrue(metrics.errorCount >= 1)
            assertTrue(metrics.errorRatePercent > 0.0)
            assertTrue(metrics.gradleTaskLatencies.isNotEmpty())
            assertTrue(metrics.errorTrendPoints.isNotEmpty())
        }
    }

    @Test
    fun verifyAutonomousSelfCorrectionSelfImprovementAndVersionUpdate() {
        runBlocking {
            val repository = LuxRepository()
            val initialLuxVer = repository.luxVersionName.value
            val initialProjectVer = repository.activeProject.value.version

            // 1. Test Auto-Correction of LUX and Generated Apps
            repository.autoCorrectLuxAndGeneratedApps()
            val correctedProject = repository.activeProject.value
            assertTrue(correctedProject.autoCorrectedCount > 1)
            assertTrue(
                correctedProject.generatedFiles.any {
                    it.fileName == "DataRepository.kt" && it.content.contains("AutoHealingGuard")
                }
            )

            // 2. Test Auto-Improvement of LUX and Generated Apps
            repository.autoImproveLuxAndGeneratedApps()
            val improvedProject = repository.activeProject.value
            assertTrue(improvedProject.autoImprovedCount > 1)
            assertTrue(
                improvedProject.generatedFiles.any {
                    it.fileName == "AutoEvolutionEngine.kt"
                }
            )

            // 3. Test Auto-Version Update of LUX and Generated Apps
            val newLuxVer = repository.autoUpdateAppAndProjectsVersion("MINOR")
            assertTrue(initialLuxVer != newLuxVer)
            assertTrue(initialProjectVer != repository.activeProject.value.version)
            assertTrue(
                repository.activeProject.value.generatedFiles.any {
                    it.fileName == "build.gradle.kts" && it.content.contains("versionName = \"${repository.activeProject.value.version}\"")
                }
            )
        }
    }

    @Test
    fun verifyAutoDevOsHotSwapPatchWithoutRecompileAndInterfaceSettings() {
        runBlocking {
            val repository = LuxRepository()

            // Verify Gold & Silver Interface Settings update
            val updatedUi = com.example.data.models.LuxInterfaceSettings(
                backgroundImageKey = "lux_icon",
                backgroundImageAlpha = 0.45f,
                surfaceGlassAlpha = 0.58f
            )
            repository.updateInterfaceSettings(updatedUi)
            assertEquals("lux_icon", repository.interfaceSettings.value.backgroundImageKey)

            // Verify Auto-DevOS log analysis for both LUX and generated APKs
            val proposals = repository.analyzeLogsWithAutoDevOs()
            assertTrue(proposals.isNotEmpty())
            assertTrue(proposals.any { it.isHostLuxApp })
            assertTrue(proposals.any { !it.isHostLuxApp })

            // Verify zero-recompile Hot-Swap DEX Delta deployment
            val deployments = repository.deployAllAutoDevOsHotPatches()
            assertTrue(deployments.isNotEmpty())
            assertTrue(deployments.all { !it.fullRecompileRequired })
            assertTrue(
                repository.activeProject.value.generatedFiles.any {
                    it.fileName == "AutoDevOsHotSwapRuntime.kt" && it.content.contains("ZERO_RECOMPILE_ENABLED = true")
                }
            )
        }
    }
}
