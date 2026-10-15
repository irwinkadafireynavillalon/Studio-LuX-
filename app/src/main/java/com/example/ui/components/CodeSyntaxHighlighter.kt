package com.example.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Utilidad de resaltado de sintaxis (Syntax Highlighting) para el visor y editor de archivos de DevOS.
 * Soporta Kotlin, XML, Gradle (Kotlin DSL) y JSON con paleta de alto contraste.
 * Incluye un VisualTransformation para aplicar resaltado en tiempo real dentro de campos TextField.
 */
object CodeSyntaxHighlighter {

    // Paleta de colores para código
    val ColorKeyword = Color(0xFF67D8EF)      // Cyan / Azul eléctrico para palabras clave
    val ColorType = Color(0xFFFFD166)         // Amarillo / Oro para Clases, Tipos e Interfaces
    val ColorString = Color(0xFFA6E22E)       // Verde brillante para cadenas de texto
    val ColorNumber = Color(0xFFAE81FF)       // Violeta / Púrpura para números y valores literales
    val ColorComment = Color(0xFF75715E)      // Gris oliva atenuado para comentarios
    val ColorAnnotation = Color(0xFFFF9E3B)   // Naranja cálido para anotaciones @ y etiquetas
    val ColorAttribute = Color(0xFF82AAFF)    // Azul suave para atributos XML y propiedades
    val ColorPunctuation = Color(0xFFF8F8F2)  // Blanco nítido para puntuación y operadores
    val ColorFunction = Color(0xFF50FA7B)     // Verde menta para nombres de funciones

    private val KotlinKeywords = setOf(
        "package", "import", "class", "interface", "object", "fun", "val", "var",
        "return", "if", "else", "when", "for", "while", "do", "try", "catch", "finally",
        "throw", "override", "open", "private", "protected", "public", "internal",
        "sealed", "data", "enum", "suspend", "coroutine", "abstract", "constructor",
        "this", "super", "null", "true", "false", "is", "in", "as", "by", "companion",
        "lateinit", "inline", "crossinline", "noinline", "reified", "tailrec", "operator"
    )

    private val GradleKeywords = setOf(
        "plugins", "alias", "dependencies", "implementation", "testImplementation",
        "androidTestImplementation", "ksp", "android", "namespace", "compileSdk",
        "defaultConfig", "applicationId", "minSdk", "targetSdk", "versionCode",
        "versionName", "buildTypes", "release", "debug", "signingConfigs",
        "compileOptions", "buildFeatures", "compose", "sourceCompatibility", "targetCompatibility"
    )

    /**
     * Resalta el código según el lenguaje y devuelve un AnnotatedString con estilos tipográficos.
     */
    fun highlightCode(text: String, language: String): AnnotatedString {
        if (text.isEmpty()) return AnnotatedString("")

        val lang = language.lowercase().trim()
        return when {
            lang == "xml" -> highlightXml(text)
            lang == "json" -> highlightJson(text)
            lang == "gradle" || lang.endsWith(".kts") || lang == "groovy" -> highlightGradle(text)
            else -> highlightKotlin(text)
        }
    }

    /**
     * Resaltado de sintaxis para Kotlin
     */
    fun highlightKotlin(text: String): AnnotatedString {
        return buildAnnotatedString {
            // Regex que captura comentarios, cadenas, anotaciones, números y palabras
            val regex = Regex("(\"(\\\\.|[^\"])*\"|//.*|/\\*.*?\\*/|@[a-zA-Z0-9_]+|[a-zA-Z0-9_]+|\\s+|[^a-zA-Z0-9_\\s])")
            val matches = regex.findAll(text)

            var isAfterFun = false
            for (match in matches) {
                val token = match.value
                when {
                    token.startsWith("//") || token.startsWith("/*") -> {
                        pushStyle(SpanStyle(color = ColorComment, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                        append(token)
                        pop()
                        isAfterFun = false
                    }
                    token.startsWith("\"") -> {
                        pushStyle(SpanStyle(color = ColorString))
                        append(token)
                        pop()
                        isAfterFun = false
                    }
                    token.startsWith("@") -> {
                        pushStyle(SpanStyle(color = ColorAnnotation, fontWeight = FontWeight.SemiBold))
                        append(token)
                        pop()
                        isAfterFun = false
                    }
                    KotlinKeywords.contains(token) -> {
                        pushStyle(SpanStyle(color = ColorKeyword, fontWeight = FontWeight.Bold))
                        append(token)
                        pop()
                        isAfterFun = (token == "fun")
                    }
                    isAfterFun && token.all { it.isLetterOrDigit() || it == '_' } -> {
                        pushStyle(SpanStyle(color = ColorFunction, fontWeight = FontWeight.Bold))
                        append(token)
                        pop()
                        isAfterFun = false
                    }
                    token.firstOrNull()?.isUpperCase() == true && token.all { it.isLetterOrDigit() || it == '_' } -> {
                        pushStyle(SpanStyle(color = ColorType, fontWeight = FontWeight.Medium))
                        append(token)
                        pop()
                        isAfterFun = false
                    }
                    token.toIntOrNull() != null || token.toDoubleOrNull() != null || token.endsWith("L") || token.endsWith("f") -> {
                        pushStyle(SpanStyle(color = ColorNumber))
                        append(token)
                        pop()
                        isAfterFun = false
                    }
                    else -> {
                        pushStyle(SpanStyle(color = ColorPunctuation))
                        append(token)
                        pop()
                        if (token.isNotBlank()) isAfterFun = false
                    }
                }
            }
        }
    }

    /**
     * Resaltado de sintaxis para XML
     */
    fun highlightXml(text: String): AnnotatedString {
        return buildAnnotatedString {
            // Regex que captura comentarios XML, etiquetas, cadenas y atributos
            val regex = Regex("(<!--.*?-->|\"(\\\\.|[^\"])*\"|<[/]?[a-zA-Z0-9_:.-]+|[/]?>|[a-zA-Z0-9_:.-]+=|\\s+|[^a-zA-Z0-9_\\s])")
            val matches = regex.findAll(text)

            for (match in matches) {
                val token = match.value
                when {
                    token.startsWith("<!--") -> {
                        pushStyle(SpanStyle(color = ColorComment, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                        append(token)
                        pop()
                    }
                    token.startsWith("<") -> {
                        pushStyle(SpanStyle(color = ColorKeyword, fontWeight = FontWeight.Bold))
                        append(token)
                        pop()
                    }
                    token == ">" || token == "/>" || token == "</" -> {
                        pushStyle(SpanStyle(color = ColorKeyword, fontWeight = FontWeight.Bold))
                        append(token)
                        pop()
                    }
                    token.endsWith("=") -> {
                        pushStyle(SpanStyle(color = ColorAttribute, fontWeight = FontWeight.Medium))
                        append(token)
                        pop()
                    }
                    token.startsWith("\"") -> {
                        pushStyle(SpanStyle(color = ColorString))
                        append(token)
                        pop()
                    }
                    else -> {
                        pushStyle(SpanStyle(color = ColorPunctuation))
                        append(token)
                        pop()
                    }
                }
            }
        }
    }

    /**
     * Resaltado de sintaxis para Gradle (Kotlin DSL)
     */
    fun highlightGradle(text: String): AnnotatedString {
        return buildAnnotatedString {
            val regex = Regex("(\"(\\\\.|[^\"])*\"|//.*|/\\*.*?\\*/|[a-zA-Z0-9_]+|\\s+|[^a-zA-Z0-9_\\s])")
            val matches = regex.findAll(text)

            for (match in matches) {
                val token = match.value
                when {
                    token.startsWith("//") || token.startsWith("/*") -> {
                        pushStyle(SpanStyle(color = ColorComment, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
                        append(token)
                        pop()
                    }
                    token.startsWith("\"") -> {
                        pushStyle(SpanStyle(color = ColorString))
                        append(token)
                        pop()
                    }
                    GradleKeywords.contains(token) || KotlinKeywords.contains(token) -> {
                        pushStyle(SpanStyle(color = ColorKeyword, fontWeight = FontWeight.Bold))
                        append(token)
                        pop()
                    }
                    token.toIntOrNull() != null || token.toDoubleOrNull() != null -> {
                        pushStyle(SpanStyle(color = ColorNumber))
                        append(token)
                        pop()
                    }
                    token.firstOrNull()?.isUpperCase() == true && token.all { it.isLetterOrDigit() || it == '_' } -> {
                        pushStyle(SpanStyle(color = ColorType, fontWeight = FontWeight.Medium))
                        append(token)
                        pop()
                    }
                    else -> {
                        pushStyle(SpanStyle(color = ColorPunctuation))
                        append(token)
                        pop()
                    }
                }
            }
        }
    }

    /**
     * Resaltado de sintaxis para JSON
     */
    fun highlightJson(text: String): AnnotatedString {
        return buildAnnotatedString {
            val regex = Regex("(\"(\\\\.|[^\"])*\"\\s*:|\"(\\\\.|[^\"])*\"|[0-9.]+|true|false|null|[{}\\[\\],:]|\\s+)")
            val matches = regex.findAll(text)

            for (match in matches) {
                val token = match.value
                when {
                    token.trimEnd().endsWith(":") -> {
                        pushStyle(SpanStyle(color = ColorKeyword, fontWeight = FontWeight.Medium))
                        append(token)
                        pop()
                    }
                    token.startsWith("\"") -> {
                        pushStyle(SpanStyle(color = ColorString))
                        append(token)
                        pop()
                    }
                    token.toIntOrNull() != null || token.toDoubleOrNull() != null -> {
                        pushStyle(SpanStyle(color = ColorNumber))
                        append(token)
                        pop()
                    }
                    token == "true" || token == "false" -> {
                        pushStyle(SpanStyle(color = ColorAnnotation, fontWeight = FontWeight.Bold))
                        append(token)
                        pop()
                    }
                    token == "null" -> {
                        pushStyle(SpanStyle(color = ColorComment, fontWeight = FontWeight.Bold))
                        append(token)
                        pop()
                    }
                    else -> {
                        pushStyle(SpanStyle(color = ColorPunctuation))
                        append(token)
                        pop()
                    }
                }
            }
        }
    }

    /**
     * VisualTransformation para resaltar el código en tiempo real dentro de un TextField de Compose.
     */
    class CodeVisualTransformation(val language: String) : VisualTransformation {
        override fun filter(text: AnnotatedString): TransformedText {
            val highlighted = highlightCode(text.text, language)
            return TransformedText(highlighted, OffsetMapping.Identity)
        }
    }
}
