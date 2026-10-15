package com.example.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.data.models.CliLogType

object CliSyntaxHighlighter {

    // Palette for syntax highlighting
    private val KeywordColor = Color(0xFF67D8EF)      // Cyan
    private val TypeColor = Color(0xFFFFD166)         // Gold / Yellow
    private val StringColor = Color(0xFFA6E22E)       // Bright Green
    private val NumberColor = Color(0xFFAE81FF)       // Violet / Purple
    private val CommentColor = Color(0xFF75715E)      // Muted Olive Gray
    private val AnnotationColor = Color(0xFFFD971F)   // Orange
    private val CommandColor = Color(0xFF00FFCC)      // Bright Neon Mint
    private val ErrorColor = Color(0xFFFF5555)        // Vivid Red
    private val SuccessColor = Color(0xFF50FA7B)      // Light Green
    private val PunctuationColor = Color(0xFFF8F8F2)  // Crisp Off-White

    private val KotlinKeywords = setOf(
        "package", "import", "class", "interface", "object", "fun", "val", "var",
        "return", "if", "else", "when", "for", "while", "do", "try", "catch", "finally",
        "throw", "override", "open", "private", "protected", "public", "internal",
        "sealed", "data", "enum", "suspend", "coroutine", "abstract", "constructor",
        "this", "super", "null", "true", "false", "is", "in", "as", "by", "companion"
    )

    private val ShellKeywords = setOf(
        "git", "gradle", "chmod", "curl", "npm", "node", "python", "antigravity",
        "build", "patch", "monitor", "fix", "upgrade", "status", "logs", "inspect",
        "cd", "ls", "cat", "mkdir", "rm", "echo", "grep", "docker", "gcloud"
    )

    fun highlightTerminalText(
        rawText: String,
        logType: CliLogType = CliLogType.INFO
    ): AnnotatedString {
        if (rawText.isBlank()) return AnnotatedString("")

        return buildAnnotatedString {
            // First check if it's a known error or success message
            if (logType == CliLogType.ERROR || rawText.contains("FATAL") || rawText.contains("Exception")) {
                appendErrorHighlight(rawText)
                return@buildAnnotatedString
            }

            if (logType == CliLogType.COMMAND || rawText.startsWith("$") || rawText.startsWith(">")) {
                appendShellCommandHighlight(rawText)
                return@buildAnnotatedString
            }

            if (rawText.trimStart().startsWith("{") || rawText.trimStart().startsWith("[")) {
                appendJsonHighlight(rawText)
                return@buildAnnotatedString
            }

            if (rawText.contains("class ") || rawText.contains("fun ") || rawText.contains("import ") || rawText.contains("val ")) {
                appendKotlinCodeHighlight(rawText)
                return@buildAnnotatedString
            }

            // General CLI output highlighting: detect flags (--flag, -v), urls, paths, and status badges
            appendGeneralCliHighlight(rawText)
        }
    }

    private fun AnnotatedString.Builder.appendErrorHighlight(text: String) {
        val lines = text.lines()
        lines.forEachIndexed { index, line ->
            if (line.contains("Exception") || line.contains("Error") || line.contains("FATAL")) {
                pushStyle(SpanStyle(color = ErrorColor, fontWeight = FontWeight.Bold))
                append(line)
                pop()
            } else if (line.trim().startsWith("at ") || line.contains(".kt:") || line.contains(".java:")) {
                pushStyle(SpanStyle(color = Color(0xFFFF9494), fontFamily = FontFamily.Monospace))
                append(line)
                pop()
            } else {
                pushStyle(SpanStyle(color = Color(0xFFFFAAAA)))
                append(line)
                pop()
            }
            if (index < lines.size - 1) append("\n")
        }
    }

    private fun AnnotatedString.Builder.appendShellCommandHighlight(text: String) {
        val tokens = text.split(Regex("(?<=\\s)|(?=\\s)"))
        for (token in tokens) {
            val trimmed = token.trim()
            when {
                trimmed == "$" || trimmed == ">" -> {
                    pushStyle(SpanStyle(color = CommandColor, fontWeight = FontWeight.Bold))
                    append(token)
                    pop()
                }
                ShellKeywords.contains(trimmed.lowercase()) -> {
                    pushStyle(SpanStyle(color = KeywordColor, fontWeight = FontWeight.Bold))
                    append(token)
                    pop()
                }
                trimmed.startsWith("--") || trimmed.startsWith("-") -> {
                    pushStyle(SpanStyle(color = AnnotationColor))
                    append(token)
                    pop()
                }
                trimmed.startsWith("\"") && trimmed.endsWith("\"") -> {
                    pushStyle(SpanStyle(color = StringColor))
                    append(token)
                    pop()
                }
                trimmed.startsWith("/") || trimmed.contains(".apk") || trimmed.contains(".kt") -> {
                    pushStyle(SpanStyle(color = TypeColor))
                    append(token)
                    pop()
                }
                else -> {
                    pushStyle(SpanStyle(color = PunctuationColor))
                    append(token)
                    pop()
                }
            }
        }
    }

    private fun AnnotatedString.Builder.appendKotlinCodeHighlight(text: String) {
        // Regex word tokenization preserving whitespace and punctuation
        val regex = Regex("(\"(\\\\.|[^\"])*\"|//.*|/\\*.*?\\*/|@[a-zA-Z0-9_]+|[a-zA-Z0-9_]+|\\s+|[^a-zA-Z0-9_\\s])")
        val matches = regex.findAll(text)

        for (match in matches) {
            val token = match.value
            when {
                token.startsWith("//") || token.startsWith("/*") -> {
                    pushStyle(SpanStyle(color = CommentColor))
                    append(token)
                    pop()
                }
                token.startsWith("\"") -> {
                    pushStyle(SpanStyle(color = StringColor))
                    append(token)
                    pop()
                }
                token.startsWith("@") -> {
                    pushStyle(SpanStyle(color = AnnotationColor, fontWeight = FontWeight.SemiBold))
                    append(token)
                    pop()
                }
                KotlinKeywords.contains(token) -> {
                    pushStyle(SpanStyle(color = KeywordColor, fontWeight = FontWeight.Bold))
                    append(token)
                    pop()
                }
                token.firstOrNull()?.isUpperCase() == true && token.all { it.isLetterOrDigit() || it == '_' } -> {
                    pushStyle(SpanStyle(color = TypeColor, fontWeight = FontWeight.Medium))
                    append(token)
                    pop()
                }
                token.toIntOrNull() != null || token.toDoubleOrNull() != null -> {
                    pushStyle(SpanStyle(color = NumberColor))
                    append(token)
                    pop()
                }
                else -> {
                    pushStyle(SpanStyle(color = PunctuationColor))
                    append(token)
                    pop()
                }
            }
        }
    }

    private fun AnnotatedString.Builder.appendJsonHighlight(text: String) {
        val regex = Regex("(\"(\\\\.|[^\"])*\"\\s*:|\"(\\\\.|[^\"])*\"|[0-9.]+|true|false|null|[{}\\[\\],:]|\\s+)")
        val matches = regex.findAll(text)

        for (match in matches) {
            val token = match.value
            when {
                token.trimEnd().endsWith(":") -> {
                    // JSON key
                    pushStyle(SpanStyle(color = KeywordColor, fontWeight = FontWeight.Medium))
                    append(token)
                    pop()
                }
                token.startsWith("\"") -> {
                    // JSON string value
                    pushStyle(SpanStyle(color = StringColor))
                    append(token)
                    pop()
                }
                token.toIntOrNull() != null || token.toDoubleOrNull() != null -> {
                    pushStyle(SpanStyle(color = NumberColor))
                    append(token)
                    pop()
                }
                token == "true" || token == "false" -> {
                    pushStyle(SpanStyle(color = AnnotationColor, fontWeight = FontWeight.Bold))
                    append(token)
                    pop()
                }
                token == "null" -> {
                    pushStyle(SpanStyle(color = CommentColor, fontWeight = FontWeight.Bold))
                    append(token)
                    pop()
                }
                else -> {
                    pushStyle(SpanStyle(color = PunctuationColor))
                    append(token)
                    pop()
                }
            }
        }
    }

    private fun AnnotatedString.Builder.appendGeneralCliHighlight(text: String) {
        val tokens = text.split(Regex("(?<=\\s)|(?=\\s)"))
        for (token in tokens) {
            val trimmed = token.trim()
            when {
                trimmed.startsWith("[") && trimmed.endsWith("]") -> {
                    // Badge [OK], [ALERTA], [FALLO], [AGENTE]
                    val color = when {
                        trimmed.contains("OK") || trimmed.contains("SUCCESS") -> SuccessColor
                        trimmed.contains("FALLO") || trimmed.contains("ERROR") -> ErrorColor
                        trimmed.contains("ALERTA") || trimmed.contains("WARN") -> AnnotationColor
                        trimmed.contains("AGENTE") || trimmed.contains("GEMMA") -> Color(0xFFD946EF)
                        else -> KeywordColor
                    }
                    pushStyle(SpanStyle(color = color, fontWeight = FontWeight.Bold))
                    append(token)
                    pop()
                }
                trimmed.startsWith("--") || (trimmed.startsWith("-") && trimmed.length > 1) -> {
                    pushStyle(SpanStyle(color = AnnotationColor))
                    append(token)
                    pop()
                }
                trimmed.startsWith("\"") && trimmed.endsWith("\"") -> {
                    pushStyle(SpanStyle(color = StringColor))
                    append(token)
                    pop()
                }
                trimmed.contains("http://") || trimmed.contains("https://") -> {
                    pushStyle(SpanStyle(color = KeywordColor))
                    append(token)
                    pop()
                }
                trimmed.contains(".apk") || trimmed.contains(".bin") || trimmed.contains(".json") || trimmed.contains(".kt") -> {
                    pushStyle(SpanStyle(color = TypeColor))
                    append(token)
                    pop()
                }
                trimmed.toIntOrNull() != null || trimmed.endsWith("ms") || trimmed.endsWith("MB") || trimmed.endsWith("GB") -> {
                    pushStyle(SpanStyle(color = NumberColor))
                    append(token)
                    pop()
                }
                else -> {
                    pushStyle(SpanStyle(color = PunctuationColor))
                    append(token)
                    pop()
                }
            }
        }
    }
}
