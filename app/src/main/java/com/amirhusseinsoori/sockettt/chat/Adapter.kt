package com.amirhusseinsoori.sockettt.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amirhusseinsoori.sockettt.R
import com.amirhusseinsoori.sockettt.model.ParentModels
import com.amirhusseinsoori.sockettt.model.ReceiveMessageModel
import com.amirhusseinsoori.sockettt.model.SendMessageModel
import com.amirhusseinsoori.sockettt.util.Constance
import kotlinx.android.synthetic.main.recive_message_text.view.*
import kotlinx.android.synthetic.main.send_message_text.view.*


class Adapter(private val list: ArrayList<ParentModels>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemViewType(position: Int): Int {
        return list[position].type!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType){
            Constance.MODEL_1 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.send_message_text,parent,false)
                ViewHolderSendMessage(view)
            }
            Constance.MODEL_2 -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.recive_message_text,parent,false)
                ViewHolderReceiveMessage(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.send_message_text,parent,false)
                ViewHolderReceiveMessage(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when(holder){
           is ViewHolderSendMessage -> {
                val model = list[position] as SendMessageModel
               holder.itemView.txt_message_receive.text=model.name

            }
            is ViewHolderReceiveMessage -> {
                val model = list[position] as ReceiveMessageModel
                holder.itemView.txt_message_send.text=model.name
            }
        }
    }

    override fun getItemCount(): Int {
      return list.size
    }


    class ViewHolderSendMessage(item: View): RecyclerView.ViewHolder(item)




    class ViewHolderReceiveMessage(item: View): RecyclerView.ViewHolder(item)
}