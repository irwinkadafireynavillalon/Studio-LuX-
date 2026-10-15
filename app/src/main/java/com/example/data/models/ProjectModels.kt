package com.example.data.models

import java.util.UUID

data class GeneratedAppProject(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val packageName: String,
    val description: String,
    val category: String,
    val version: String = "1.0.0",
    val versionCode: Int = 1,
    val status: BuildStatus = BuildStatus.READY,
    val prompt: String,
    val generatedFiles: List<ProjectSourceFile> = emptyList(),
    val apkSizeMb: Double = 18.4,
    val buildDurationSec: Int = 4,
    val compiledApkPath: String = "/build/outputs/apk/release/app-release.apk",
    val isStandAlone: Boolean = true,
    val targetSdk: Int = 36,
    val autoCorrectedCount: Int = 1,
    val autoImprovedCount: Int = 1,
    val hotPatchesAppliedCount: Int = 1,
    val lastEvolutionSummary: String = "Motor Auto-DevOS Hot-Patch y Auto-Corrección activos",
    val timestamp: Long = System.currentTimeMillis()
)

enum class BuildStatus {
    IDLE,
    ANALYZING_PROMPT,
    GENERATING_ARCHITECTURE,
    SYNTHESIZING_CODE,
    CONFIGURING_LOCAL_STORAGE,
    COMPILING_GRADLE,
    PACKAGING_APK,
    READY,
    FAILED
}

data class ProjectSourceFile(
    val fileName: String,
    val path: String,
    val language: String, // kotlin, xml, gradle, json
    val content: String
)

data class CliLogEntry(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val type: CliLogType = CliLogType.INFO,
    val timestamp: String = "",
    val tag: String = "LUX_KERNEL"
)

enum class CliLogType {
    INFO,
    SUCCESS,
    WARNING,
    ERROR,
    AGENT,
    COMMAND,
    SYSTEM
}

data class SelfHealingTask(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val exceptionType: String,
    val affectedFile: String,
    val lineNumber: Int,
    val stackTraceSnippet: String,
    val patchDiffBefore: String,
    val patchDiffAfter: String,
    val status: SelfHealingStatus,
    val stageDescription: String,
    val elapsedMs: Long,
    val timestamp: String,
    val targetScope: String = "LUX Kernel & Apps Creadas"
)

enum class SelfHealingStatus {
    IDLE,
    INCIDENT_DETECTED,
    DIAGNOSING_AST,
    SYNTHESIZING_HOTFIX,
    BYTECODE_VERIFIED,
    RESTORED
}

enum class EvolutionCategory {
    AUTO_CORRECT_HOST,
    AUTO_CORRECT_CHILD_APP,
    AUTO_IMPROVE_HOST,
    AUTO_IMPROVE_CHILD_APP,
    AUTO_UPDATE_VERSION_HOST,
    AUTO_UPDATE_VERSION_CHILD_APP
}

data class EvolutionRecord(
    val id: String = UUID.randomUUID().toString().take(8),
    val category: EvolutionCategory,
    val targetName: String,
    val title: String,
    val details: String,
    val versionBefore: String,
    val versionAfter: String,
    val codeDiffSummary: String = "",
    val timestamp: String = ""
)

data class AppVersionRelease(
    val id: String = UUID.randomUUID().toString().take(8),
    val targetApp: String,
    val versionName: String,
    val versionCode: Int,
    val channel: String = "OMEGA_AUTO_OTA",
    val changelog: List<String>,
    val sha256Checksum: String,
    val timestamp: String
)

/**
 * Ajustes de Interfaz de LUX (Imagen de fondo, bordes y colores Oro y Plata transparentes)
 */
data class LuxInterfaceSettings(
    val backgroundImageKey: String = "lux_emblem", // "lux_emblem" o "lux_icon"
    val backgroundImageAlpha: Float = 0.38f,
    val surfaceGlassAlpha: Float = 0.65f,
    val goldSilverBorderGlow: Float = 0.92f,
    val presetStyle: String = "ORO_Y_PLATA_TRANSPARENTE",
    val showBackgroundVignette: Boolean = true,
    val compactUiMode: Boolean = true,
    val floatingButtonSizeDp: Float = 44f,
    val floatingButtonAlpha: Float = 0.90f
)

/**
 * Modelos del Módulo 'Auto-DevOS':
 * Analiza logs de error de APKs generadas y de la propia app LUX, propone correcciones automáticas
 * y despliega parches en caliente (Hot-Swap DEX Delta) sin necesidad de recompilar completamente.
 */
enum class AutoDevOsPatchStatus {
    PROPOSED,
    INJECTING_HOT_PATCH,
    DEPLOYED_NO_RECOMPILE
}

data class AutoDevOsErrorLogItem(
    val id: String = UUID.randomUUID().toString().take(8),
    val sourceAppId: String,
    val sourceAppName: String,
    val isHostLuxApp: Boolean,
    val severity: String, // "CRITICAL", "ERROR", "WARNING"
    val rawLogLine: String,
    val componentFile: String,
    val lineNumber: Int,
    val rootCauseSummary: String,
    val timestamp: String
)

data class AutoDevOsPatchProposal(
    val id: String = "hpatch_" + UUID.randomUUID().toString().take(6),
    val targetAppId: String,
    val targetAppName: String,
    val isHostLuxApp: Boolean,
    val errorLogLine: String,
    val exceptionSignature: String,
    val rootCauseAnalysis: String,
    val targetFile: String,
    val lineNumber: Int,
    val proposedCodeBefore: String,
    val proposedCodeAfter: String,
    val deliveryMechanism: String = "HOT_SWAP_DEX_DELTA (Sin Recompilar)",
    val estimatedHotPatchMs: Long = 115L,
    val fullRecompileSavedSec: Double = 14.8,
    val status: AutoDevOsPatchStatus = AutoDevOsPatchStatus.PROPOSED,
    val patchVersionTag: String = "+hotfix.1",
    val timestamp: String = ""
)

data class HotSwapPatchDeployment(
    val id: String = "dex_" + UUID.randomUUID().toString().take(6),
    val proposalId: String,
    val targetAppName: String,
    val isHostLuxApp: Boolean,
    val targetFile: String,
    val patchDescription: String,
    val dexDeltaSizeKb: Double = 14.2,
    val injectionLatencyMs: Long = 118L,
    val fullRecompileRequired: Boolean = false,
    val sha256DeltaHash: String = "",
    val timestamp: String = ""
)

data class CodePatch(
    val id: String = UUID.randomUUID().toString().take(8),
    val targetFile: String,
    val description: String,
    val beforeSnippet: String,
    val afterSnippet: String,
    val author: String = "AntigravityCLI",
    val appliedAt: String = "",
    val isVerified: Boolean = true,
    val isReverted: Boolean = false
)

data class KernelHealthStatus(
    val pid: Int = 10842,
    val cpuUsagePercent: Double = 3.2,
    val ramUsedMb: Int = 3450,
    val ramTotalMb: Int = 8192,
    val npuAccelerated: Boolean = true,
    val activeSockets: Int = 2,
    val websocketLatencyMs: Int = 14,
    val crashFreeRate: Double = 100.0,
    val appliedPatchCount: Int = 1,
    val activeDaemonCount: Int = 4
)

data class GradleTaskInfo(
    val taskName: String,
    val durationMs: Long,
    val state: String = "SUCCESS",
    val description: String = ""
)

data class BuildReport(
    val buildId: String = UUID.randomUUID().toString().take(8),
    val appName: String,
    val packageName: String,
    val buildType: String = "Release",
    val status: String = "SUCCESS",
    val durationSeconds: Double = 4.2,
    val apkSizeMb: Double = 18.4,
    val apkPath: String = "/build/outputs/apk/release/app-release.apk",
    val sha256Checksum: String = "7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069",
    val targetSdk: Int = 36,
    val tasksExecuted: List<GradleTaskInfo> = emptyList(),
    val warningsCount: Int = 0,
    val errorsCount: Int = 0,
    val timestamp: String = "",
    val signedWithV2V3: Boolean = true
)

enum class TerminalThemeMode(val displayName: String) {
    OBSIDIAN_MATRIX("Obsidian Matrix (Verde)"),
    CYBER_AMBER("Cyber Amber (Ámbar)"),
    HYPER_MONOLITH("Hyper Monolith (Plata/Oro)"),
    SYNTH_CYAN("Synth Cyan (Cian)")
}

data class AiModelInfo(
    val id: String,
    val name: String,
    val type: AiModelType,
    val sizeDescription: String,
    val quantization: String,
    val isDownloaded: Boolean,
    val downloadProgress: Float = 1.0f,
    val status: String,
    val memoryUsageMb: Int,
    val avgLatencyMs: Int,
    val isDefault: Boolean = false
)

enum class AiModelType {
    LOCAL_OFFLINE,
    LOCAL_CLI,
    CLOUD_RUN
}

data class GemmaModelFile(
    val id: String,
    val name: String,
    val filename: String,
    val description: String,
    val sizeBytes: Long,
    val sizeFormatted: String,
    val expectedSha256: String,
    val quantization: String, // INT4, FP16, Q4_K_M
    val targetEngine: String, // NPU, CPU, GPU
    val isDownloaded: Boolean = false,
    val isIntegrityVerified: Boolean = false,
    val localFilePath: String? = null,
    val verifiedSha256: String? = null,
    val isActiveInCli: Boolean = false,
    val downloadStatus: ModelDownloadStatus = ModelDownloadStatus.IDLE,
    val downloadProgress: Float = 0f,
    val downloadSpeedMbps: Float = 0f,
    val downloadEtaSeconds: Int = 0,
    val integrityCheckMessage: String = ""
)

enum class ModelDownloadStatus {
    IDLE,
    DOWNLOADING,
    VERIFYING_SHA256,
    VERIFIED_SUCCESS,
    CORRUPTED_ERROR,
    ACTIVE_CLI
}

data class CliTelemetryMetrics(
    val totalLogs: Int,
    val errorCount: Int,
    val warningCount: Int,
    val successCount: Int,
    val infoCount: Int,
    val errorRatePercent: Double,
    val avgCompilationLatencyMs: Long,
    val maxCompilationLatencyMs: Long,
    val gradleTaskLatencies: List<GradleTaskLatencyPoint>,
    val errorTrendPoints: List<ErrorTrendPoint>,
    val throughputLogsPerSec: Double,
    val systemAvailabilityPercent: Double = 99.8
)

data class GradleTaskLatencyPoint(
    val taskName: String,
    val durationMs: Long,
    val isCached: Boolean = false,
    val timestamp: String = ""
)

data class ErrorTrendPoint(
    val timeLabel: String,
    val errorRatePercent: Double,
    val totalInBucket: Int,
    val errorCount: Int
)

data class AgentChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val sender: AgentMessageSender,
    val text: String,
    val timestamp: String = "",
    val actions: List<AgentInteractiveAction> = emptyList(),
    val attachedFiles: List<UniversalTransferFile> = emptyList(),
    val isStreaming: Boolean = false,
    val modelName: String = "gemini-3.8-flash"
)

enum class AgentMessageSender {
    USER,
    ORCHESTRATOR_AGENT,
    SYSTEM
}

data class AgentInteractiveAction(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val type: AgentActionType,
    val payload: String = "",
    val isCompleted: Boolean = false
)

enum class AgentActionType {
    BUILD_APK,
    SHARE_APK,
    INSTALL_APK,
    CREATE_PROJECT,
    SWITCH_TAB,
    RUN_DIAGNOSTIC,
    CHANGE_THEME,
    VIEW_CODE,
    DOWNLOAD_ANTIGRAVITY_CLI,
    SHARE_UNIVERSAL_FILE,
    SYNC_GOOGLE_ACCOUNT
}

data class GoogleAccountSyncState(
    val isSynced: Boolean = true,
    val email: String = "francisco.reyna2077@gmail.com",
    val displayName: String = "Francisco Reyna (Google Cloud Dev)",
    val authProvider: String = "Google Identity · Credential Manager OAuth2",
    val sessionTokenPreview: String = "ya29.a0AXooCgv_GoogleOAuth_ZeroApiKey_Active",
    val bypassManualApiKey: Boolean = true,
    val lastSyncedTimestamp: String = "Sincronizado en vivo",
    val cloudQuotaStatus: String = "Activo (Sesión Google Vinculada · Sin API Key manual)"
)

enum class UniversalFileCategory(val label: String, val defaultMime: String) {
    CLI_BINARY("Binario Antigravity CLI", "application/octet-stream"),
    APK_PACKAGE("Paquete APK Android", "application/vnd.android.package-archive"),
    SOURCE_ARCHIVE_ZIP("Archivo Comprimido ZIP", "application/zip"),
    DOCUMENT_PDF("Documento / PDF / Texto", "application/pdf"),
    IMAGE_MEDIA("Imagen / Diseño UI", "image/*"),
    AUDIO_VIDEO("Audio / Video Multimodal", "video/*"),
    DATABASE_SQLITE("Base de Datos SQLite / Room", "application/x-sqlite3"),
    CODE_SCRIPT("Código Fuente (KT/XML/JSON/PY)", "text/plain"),
    ANY_BINARY("Archivo Universal (*/*)", "*/*")
}

data class UniversalTransferFile(
    val id: String = "file_" + UUID.randomUUID().toString().take(8),
    val fileName: String,
    val mimeType: String = "*/*",
    val extension: String,
    val sizeBytes: Long,
    val sizeFormatted: String,
    val category: UniversalFileCategory = UniversalFileCategory.ANY_BINARY,
    val localFilePath: String = "",
    val sha256Hash: String = "",
    val statusLabel: String = "Listo para enviar / compartir",
    val timestamp: String = ""
)

data class AntigravityCliPackageState(
    val version: String = "2.6.0-arm64-npu",
    val packageFileName: String = "antigravity-cli-v2.6-universal.bin",
    val sizeBytes: Long = 152_043_520L, // 145 MB
    val sizeFormatted: String = "145.0 MB",
    val isInstalledAndActive: Boolean = true,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 1.0f,
    val downloadSpeedMbps: Float = 0f,
    val localBinaryPath: String = "/data/local/tmp/antigravity_cli/antigravity-cli-v2.6-universal.bin",
    val sha256Checksum: String = "a7f9c4e128b50d6391e4f820c75a1b39e8d204f617a3c9e5b0d4f8a216c3e971",
    val statusMessage: String = "Antigravity CLI v2.6.0 instalado, verificado y conectado a la aplicación.",
    val activeCapabilities: List<String> = listOf(
        "Puente IA & OS en tiempo real",
        "Compilación y empaquetado de APKs",
        "Auto-DevOS Hot-Swap DEX sin recompilar",
        "Transferencia universal de archivos (*/*)"
    )
)

enum class AiAgentType(
    val id: String,
    val displayName: String,
    val subtitle: String,
    val hostPort: String,
    val requiresManualKey: Boolean
) {
    GEMINI_CLOUD(
        id = "gemini_cloud",
        displayName = "IA Gemini",
        subtitle = "Cloud Oficial (API Key / Google Sync)",
        hostPort = "generativelanguage.googleapis.com:443",
        requiresManualKey = true
    ),
    GEMMA4_VERTEX(
        id = "gemma4_vertex",
        displayName = "IA Gemma Vertex",
        subtitle = "Vertex AI Gateway (90.11.29.1:1402)",
        hostPort = "90.11.29.1:1402",
        requiresManualKey = false
    ),
    GEMMA3_270M_LOCAL(
        id = "gemma3_270m_local",
        displayName = "Gemma3 270m",
        subtitle = "Localhost Termux (https://90.11.29.1:4250)",
        hostPort = "https://90.11.29.1:4250",
        requiresManualKey = false
    ),
    GEMINI3_LIGHT_TERMUX(
        id = "gemini3_light_termux",
        displayName = "Gemma3 270m",
        subtitle = "Localhost Termux (https://90.11.29.1:4250)",
        hostPort = "https://90.11.29.1:4250",
        requiresManualKey = false
    ),
    ANTIGRAVITY_CLI_HYBRID(
        id = "antigravity_cli_hybrid",
        displayName = "Antigravity CLI Híbrido",
        subtitle = "Orquestador Agente · Memoria Cloud/Drive",
        hostPort = "localhost:npu-bridge",
        requiresManualKey = false
    )
}

enum class LuxScreenDestination(
    val route: String,
    val title: String,
    val shortName: String,
    val subtitle: String,
    val tag: String
) {
    STUDIO_IA("studio_ia", "LuX Studio IA", "Studio IA", "Google AI Studio + Android Studio Suite", "nav_studio_ia"),
    TERMUX_LUX("termux_lux", "Termux LuX", "Termux", "Terminal Termux API, Teclado Amplio & Antigravity CLI", "nav_termux_lux"),
    PERMISSIONS_SETTINGS("permissions_settings", "Permisos y Ajustes", "Permisos", "Batería, Paletas, Temas y Vinculaciones", "nav_permissions_settings"),
    SECURITY_ACCOUNTS("security_accounts", "Seguridad y Cuentas", "Seguridad", "Cifrado E2EE, Contraseñas y Cuentas", "nav_security_accounts"),
    TESTING_AUTOCORRECTION("testing_autocorrection", "Prueba y Auto-Corrección", "Prueba", "Auto-Corrección Offline, Hot-Patches y Diagnóstico", "nav_testing_autocorrection"),
    SANDBOX_MEMORY("sandbox_memory", "Memoria Caja de Arena", "Sandbox", "Optimizador RAM, Bóveda Multimedia y Lector */*", "nav_sandbox_memory"),
    WORKSPACE("workspace", "Workspace", "Workspace", "Editor de Proyectos, Código y Compilación", "nav_workspace"),
    LUX_SETTINGS("lux_settings", "Ajustes LuX", "Ajustes LuX", "Configuración de Sys-LUX, Temas y API Key", "nav_lux_settings"),
    SYSTEM_APPS("system_apps", "LuX App Sistema", "App Sistema", "Gestión y Desinstalación de Apps vía Termux", "nav_system_apps"),
    BUILDER("builder", "Compilador APK", "Compilador", "Generador y Firmador APK Nativo", "nav_builder")
}

data class SystemAppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String = "1.0",
    val isSystemApp: Boolean = false,
    val isEnabled: Boolean = true,
    val grantedPermissionsCount: Int = 4,
    val securityRiskLevel: String = "Bajo",
    val sizeMb: Double = 24.5,
    val canDisable: Boolean = true
)

data class SandboxMediaItem(
    val id: String = UUID.randomUUID().toString().take(8),
    val title: String,
    val type: String, // "IMAGE", "VIDEO", "APK", "CODE", "DATABASE"
    val sizeFormatted: String,
    val uriOrPath: String,
    val timestamp: String,
    val createdBy: String = "IA LuX Studio"
)


