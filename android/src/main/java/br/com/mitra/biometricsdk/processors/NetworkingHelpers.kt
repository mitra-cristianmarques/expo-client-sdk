// Demonstrates calling the FaceTec Managed Testing API and/or FaceTec Server
package br.com.mitra.biometricsdk.processors

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.IOException
import java.io.OutputStream
import java.net.InetAddress
import java.net.Socket
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

object NetworkingHelpers {
    private var _apiClient: OkHttpClient? = null
    private fun createApiClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .callTimeout(180, TimeUnit.SECONDS)
            .connectTimeout(180, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)
            .writeTimeout(180, TimeUnit.SECONDS)
            .build()
    }

    var OK_HTTP_BUILDER_TAG: String = "APIRequest"
    var OK_HTTP_RESPONSE_CANCELED: String = "Canceled"

    @get:Synchronized
    val apiClient: OkHttpClient
        get() {
            if (_apiClient == null) {
                _apiClient = createApiClient()
            }
            return _apiClient!!
        }

    /*
    * Cancels all in flight requests.
    */
    fun cancelPendingRequests() {
        val client: OkHttpClient = apiClient

        // Cancel all queued calls
        for (call in client.dispatcher.queuedCalls()) {
            if (call.request().tag() == OK_HTTP_BUILDER_TAG) call.cancel()
        }
        // Cancel all running calls
        for (call in client.dispatcher.runningCalls()) {
            if (call.request().tag() == OK_HTTP_BUILDER_TAG) call.cancel()
        }
    }
}

/*
 * Implementation of RequestBody that allows upload progress to be retrieved
 */
internal class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val listener: Listener
) : RequestBody() {
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        val progressStream = ProgressStream(sink.outputStream(), contentLength())
        val progressSink = progressStream.sink().buffer()
        requestBody.writeTo(progressSink)
        progressSink.flush()
    }

    protected inner class ProgressStream internal constructor(
        private val stream: OutputStream,
        private val totalBytes: Long
    ) : OutputStream() {
        private var bytesSent: Long = 0

        @Throws(IOException::class)
        override fun write(b: ByteArray, off: Int, len: Int) {
            this.stream.write(b, off, len)
            if (len < b.size) {
                this.bytesSent += len.toLong()
            } else {
                this.bytesSent += b.size.toLong()
            }
            listener.onUploadProgressChanged(this.bytesSent, this.totalBytes)
        }

        @Throws(IOException::class)
        override fun write(b: Int) {
            this.stream.write(b)
            this.bytesSent += 1
            listener.onUploadProgressChanged(this.bytesSent, this.totalBytes)
        }
    }

    internal interface Listener {
        fun onUploadProgressChanged(bytesWritten: Long, totalBytes: Long)
    }
}

// A custom networking class is required in order to support 4.4 and below.
internal class TLSSocketFactory : SSLSocketFactory() {
    private val delegate: SSLSocketFactory

    init {
        val context = SSLContext.getInstance("TLS")
        context.init(null, null, null)
        delegate = context.getSocketFactory()
    }

    override fun getDefaultCipherSuites(): Array<String?>? {
        return delegate.getDefaultCipherSuites()
    }

    override fun getSupportedCipherSuites(): Array<String?>? {
        return delegate.getSupportedCipherSuites()
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket? {
        return enableTLSOnSocket(delegate.createSocket())
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket? {
        return enableTLSOnSocket(delegate.createSocket(s, host, port, autoClose))
    }

    @Throws(IOException::class)
    override fun createSocket(host: String?, port: Int): Socket? {
        return enableTLSOnSocket(delegate.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(
        host: String?,
        port: Int,
        localHost: InetAddress?,
        localPort: Int
    ): Socket? {
        return enableTLSOnSocket(delegate.createSocket(host, port, localHost, localPort))
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress?, port: Int): Socket? {
        return enableTLSOnSocket(delegate.createSocket(host, port))
    }

    @Throws(IOException::class)
    override fun createSocket(
        address: InetAddress?,
        port: Int,
        localAddress: InetAddress?,
        localPort: Int
    ): Socket? {
        return enableTLSOnSocket(delegate.createSocket(address, port, localAddress, localPort))
    }

    private fun enableTLSOnSocket(socket: Socket?): Socket? {
        if (socket != null && (socket is SSLSocket)) {
            socket.setEnabledProtocols(arrayOf<String>("TLSv1.1", "TLSv1.2"))
        }
        return socket
    }
}
