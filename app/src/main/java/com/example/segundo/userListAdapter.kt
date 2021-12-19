package com.example.segundo

import Paciente
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val userList : ArrayList<User>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyAdapter.MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.datoslista,parent, false)
        return MyViewHolder(itemView)

    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder, position: Int) {
        val user : User = userList[position]
        holder.txtNombre.text = user.nombre
        holder.txtFecha.text = user.fecha
        holder.txtEmail.text = user.correo
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    public class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        val txtFecha : TextView = itemView.findViewById(R.id.txtFecha)
        val txtEmail : TextView = itemView.findViewById(R.id.txtemail)

    }

}

class userListAdapter(private var activity: Activity, private var items: ArrayList<Paciente>):BaseAdapter() {

    private class ViewHolder(row: View?){
        var txtNombre: TextView? = null
        var txtFecha: TextView? = null
        var txtEmail: TextView? = null
        init {
            this.txtNombre = row?.findViewById<TextView>(R.id.txtNombre)
            this.txtFecha = row?.findViewById<TextView>(R.id.txtFecha)
            this.txtEmail = row?.findViewById<TextView>(R.id.txtemail)
        }
    }


    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view: View?
        val viewHolder: ViewHolder
        if(p1 == null){
            val inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.datoslista, null)
            viewHolder = ViewHolder(view)
            view.tag = viewHolder
        }else{
            view = p1
            viewHolder = view.tag as ViewHolder
        }
        var datos = items[p0]
        viewHolder.txtNombre?.text = datos.nombre
        viewHolder.txtFecha?.text = datos.fecha
        viewHolder.txtEmail?.text = datos.correo
        return view as View
    }
}