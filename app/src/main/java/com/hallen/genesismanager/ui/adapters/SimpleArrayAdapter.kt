package com.hallen.genesismanager.ui.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.genesismanager.R
import com.hallen.genesismanager.domain.model.WorkCraft

class SimpleArrayAdapter(
    context: Context,
    private val view: Int,
    private val callback: (WorkCraft) -> Unit,
) : ArrayAdapter<WorkCraft>(context, view, emptyArray()) {
    var list: List<WorkCraft> = emptyList()

    fun update(data: MutableSet<WorkCraft>) {
        val newData = data.toList()
        list = newData.toList()
        notifyDataSetChanged()
    }

    override fun getCount() = list.size

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(parent.context)
        val view: View = inflater.inflate(view, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.list_item_ib)
        val textView = view.findViewById<TextView>(R.id.list_item_tv)
        val colorView = view.findViewById<View>(R.id.list_item_color_view)
        val work = list.getOrNull(position) ?: return view
        colorView.backgroundTintList = ColorStateList.valueOf(Color.parseColor(work.color))
        textView.text = work.name

        imageView.setOnClickListener {
            list.getOrNull(position)?.let { callback(it) }
            notifyDataSetChanged()
        }
        return view
    }
}