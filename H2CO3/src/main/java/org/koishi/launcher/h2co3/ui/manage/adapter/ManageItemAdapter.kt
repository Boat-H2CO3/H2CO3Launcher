package org.koishi.launcher.h2co3.ui.manage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import org.koishi.launcher.h2co3.R
import org.koishi.launcher.h2co3.databinding.ItemManageBinding
import org.koishi.launcher.h2co3.ui.manage.item.ManageItem

class ManageItemAdapter(val context: Context, private val itemList: MutableList<ManageItem>) :
    RecyclerView.Adapter<ManageItemAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onClick(view: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate<ItemManageBinding>(
                LayoutInflater.from(context),
                R.layout.item_manage,
                parent,
                false
            ).root
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        DataBindingUtil.getBinding<ItemManageBinding>(holder.itemView)?.apply {
            val data = itemList[position]
            setItem(data)
            val end = item.compoundDrawablesRelative[2]
            val start = AppCompatResources.getDrawable(
                context,
                data.drawableStart
            )
            start?.setBounds(end.bounds.left, end.bounds.top, end.bounds.right, end.bounds.bottom)
            item.setCompoundDrawablesRelative(
                start,
                null,
                item.compoundDrawablesRelative[2],
                null
            )
        }
    }
}