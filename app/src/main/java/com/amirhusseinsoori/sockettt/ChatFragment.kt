package com.amirhusseinsoori.sockettt

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhusseinsoori.sockettt.chat.Adapter
import com.amirhusseinsoori.sockettt.model.ParentModels
import com.amirhusseinsoori.sockettt.model.ReceiveMessageModel
import com.amirhusseinsoori.sockettt.model.SendMessageModel
import com.amirhusseinsoori.sockettt.util.ChatFilter
import com.amirhusseinsoori.sockettt.util.Constance
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import kotlinx.android.synthetic.main.fragment_chat.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Response
import java.net.URISyntaxException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class ChatFragment : Fragment(R.layout.fragment_chat) {
    lateinit var socket: Socket
    private var list = ArrayList<ParentModels>()
    var adapter: Adapter? = null

    init {
        try {
            socket = IO.socket(Constance.URL, accessHttps(Constance.TOKEN))
        } catch (ex: URISyntaxException) {
            ex.printStackTrace()
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        socket.connect()
        socket.on("chat") {
            Handler(Looper.getMainLooper()).post {
                val jsonObject: JSONObject = it[0] as JSONObject
                run {
                    try {
                        Log.e("receiveMessage", jsonObject.toString())
                        chatFilter(jsonObject.getString("message"), 0)
                    } catch (e: JSONException) {
                        Log.e("TAG", "receiveMessage : " + e.message)
                    }

                }

            }


        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerview()
        image_send.setOnClickListener {
            if (edit_send_msg.text.toString().isNotEmpty()) {
                sendMessage()
            }

        }
        socket.on(Socket.EVENT_CONNECT, emitterDebug("connected"));
        socket.on(Socket.EVENT_DISCONNECT, emitterDebug("Disconnect"));
        socket.on(Socket.EVENT_CONNECT_ERROR, emitterDebug("Error connecting"));
        socket.on(Socket.EVENT_CONNECT_TIMEOUT, emitterDebug("TimeOut"));
    }

    private fun sendMessage() {
        val sendMessage: String = edit_send_msg.text.toString().trim()
        edit_send_msg.setText("")
        chatFilter(sendMessage, 1)
        val messageObject = JSONObject()
        try {
            messageObject.put("tran_id", 16)
            messageObject.put("message", sendMessage)
            socket.emit("chat", messageObject)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    private fun chatFilter(msg: String, type: Int): ChatFilter<Boolean,Unit> = when (type) {
            0 -> ChatFilter.Receive(list.add(SendMessageModel(msg)) ,updateChat())
            1 -> ChatFilter.Send(list.add(ReceiveMessageModel(msg)),updateChat())
            else -> ChatFilter.Receive(list.add(SendMessageModel(msg)) ,updateChat( ))
    }

    private fun updateChat() {
        adapter = Adapter(list)
        adapter!!.notifyItemInserted(adapter!!.itemCount - 1)
        recyclerview.scrollToPosition(adapter!!.itemCount - 1)
    }


    private fun setUpRecyclerview() {
        adapter = Adapter(list)
        recyclerview.layoutManager = LinearLayoutManager(activity)
        recyclerview.adapter = adapter
    }


    private fun <T> emitterDebug(data: T?): Emitter.Listener {
        return Emitter.Listener {
            Log.e("TAG", "${data}...")
            Handler(Looper.getMainLooper()).post {

                Toast.makeText(requireActivity(), "$data", Toast.LENGTH_SHORT).show()
            }

        }

    }


    private fun accessHttps(token: String): IO.Options {
        return IO.Options().also { opts ->
            opts.query = "token=$token"
            opts.sslContext = SSLContext.getInstance("SSL").also { ssl ->
                // trustAllCerts  value part2
                ssl.init(null, arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }
                }), null)
            }
            opts.reconnection = true
            opts.secure = true
            opts.timeout = 1500

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        socket.off()
        socket.disconnect()
    }

}