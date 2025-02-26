package dev.theturkey.backbones.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import java.io.IOException
import java.io.InputStream
import java.io.UncheckedIOException
import java.net.URI
import java.net.URLDecoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.util.AbstractMap.SimpleImmutableEntry
import java.util.zip.GZIPInputStream

object WebUtil {
    private var CLIENT: HttpClient = HttpClient.newBuilder()
        .connectTimeout(Duration.ofMillis((30 * 1000).toLong()))
        .version(HttpClient.Version.HTTP_2)
        .build()


    fun getDefaultReq(url: String): HttpRequest.Builder {
        return HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Accept-Encoding", "gzip")
            .timeout(Duration.ofMinutes(1))
    }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> getResponseAsJson(request: HttpRequest): T? {
        val stream = getResponse(request)
        try {
            if (stream == null)
                return null
            return Json.decodeFromStream<T>(stream)
        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }

    val json = Json { ignoreUnknownKeys = true }

    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> getResponseAsJson(response: HttpResponse<InputStream?>?): T? {
        val stream = if (response != null) getDecodedInputStream(response) else null
        try {
            if (stream == null)
                return null
            return json.decodeFromStream<T>(stream)
        } catch (e: Exception) {
            println(e.message)
        }
        return null
    }

    fun makeHTTPCall(request: HttpRequest): HttpResponse<InputStream?>? {
        //TODO Log request
        try {
            return CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream())
        } catch (e: Exception) {
            if (e.message == null || (e.message != "Connection reset" && !e.message!!.contains("GOAWAY received")))
                println("Error on request " + request.uri().toString() + "! " + e.message)
        }
        return null
    }

    fun makeHTTPCallGetStatusCode(request: HttpRequest): Int {
        val resp = makeHTTPCall(request)
        return resp?.statusCode() ?: -1
    }

    fun getResponse(request: HttpRequest): InputStream? {
        val response = makeHTTPCall(request)
        return if (response != null) getDecodedInputStream(response) else null
    }

    fun getDecodedInputStream(httpResponse: HttpResponse<InputStream?>): InputStream {
        val encoding = determineContentEncoding(httpResponse)
        try {
            return when (encoding) {
                "" -> httpResponse.body()!!
                "gzip" -> GZIPInputStream(httpResponse.body())
                else -> throw UnsupportedOperationException("Unexpected Content-Encoding: $encoding")
            }
        } catch (ioe: IOException) {
            throw UncheckedIOException(ioe)
        }
    }

    private fun determineContentEncoding(httpResponse: HttpResponse<*>): String {
        return httpResponse.headers().firstValue("Content-Encoding").orElse("")
    }

    fun splitQuery(url: URI): Map<String, List<String>> {
        val query = url.query
        if (query == null || query.isBlank())
            return emptyMap()
        return query.split("&")
            .map { s -> splitQueryParameter(s) }
            .groupBy({ it.key }, { it.value })
    }

    private fun splitQueryParameter(str: String): SimpleImmutableEntry<String, String> {
        val idx = str.indexOf("=")
        val key = if (idx > 0) str.substring(0, idx) else str
        val value = if (idx > 0 && str.length > idx + 1) str.substring(idx + 1) else null
        return SimpleImmutableEntry(
            URLDecoder.decode(key, StandardCharsets.UTF_8),
            if (value == null) null else URLDecoder.decode(value, StandardCharsets.UTF_8)
        )
    }
}