package com.amirhusseinsoori.sockettt.util

sealed class ChatFilter<T,V>(
    var model: T? = null,
    var call:V?=null
) {

    
    class Send<T,V>(model: T,call:V) : ChatFilter<T,V>(model,call)
    class Receive<T,V>(model: T,call:V) : ChatFilter<T,V>(model,call)
}