package com.example.segundo


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MiAdaptador2 (private val userList : ArrayList<User>, val listener:MyOnClickListener): RecyclerView.Adapter<MiAdaptador2.MiviewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MiAdaptador2.MiviewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        itemView.setOnClickListener {

        }
        return MiviewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MiAdaptador2.MiviewHolder, position: Int) {

        val user : User = userList[position]
        holder.nombre.text = user.nombre
        holder.fecha.text = user.fecha
        holder.correo.text = user.correo
        holder.imagen1.text = user.nombreElectro
        holder.url1.text = user.fotoURL1
        holder.url2.text = user.fotoURL1



    }

    override fun getItemCount(): Int {
        return userList.size
    }
    inner class MiviewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val nombre : TextView = itemView.findViewById(R.id.txtListaNombre)
        val fecha : TextView = itemView.findViewById(R.id.txtListaFecha)
        val correo : TextView = itemView.findViewById(R.id.txtListaCorreo)
        val imagen1 : TextView = itemView.findViewById(R.id.txtListaImagen1)
        val url1: TextView = itemView.findViewById(R.id.txtListaUrl1)
        val url2: TextView = itemView.findViewById(R.id.txtListaUrl2)
        init {
            itemView.setOnClickListener {
                val position = absoluteAdapterPosition
                listener.OnClick(position)
            }
        }

    }
    interface MyOnClickListener{
        fun OnClick(position: Int)

    }
}