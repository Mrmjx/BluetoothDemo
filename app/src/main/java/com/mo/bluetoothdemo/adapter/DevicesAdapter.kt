package com.mo.bluetoothdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.mo.bluetoothdemo.R
import com.mo.bluetoothdemo.bean.DevicesBean
import java.util.zip.Inflater

/**
 * Created by mjx on 2017/12/19.
 */
class DevicesAdapter(mContext:Context,list:List<DevicesBean>): BaseAdapter() {
    var list:List<DevicesBean> = list
    var mContext:Context = mContext

    fun setData(list:List<DevicesBean>){
        this.list=list
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var holder:DeviceHolder
        var view:View
        if (convertView==null){
            var inflater:LayoutInflater = LayoutInflater.from(mContext)
            view = inflater.inflate(R.layout.device_list_item,null)
            holder = DeviceHolder(view.findViewById(R.id.device_name),view.findViewById(R.id.device_address))
            view.tag =holder
        }else{
            view = convertView
            holder = view.tag as DeviceHolder
        }
        holder.deviceName?.text = list.get(position).device.name
        holder.deviceAddress?.text = list.get(position).device.address

        return view
    }

    override fun getItem(position: Int): Any{
        return list.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

    data class DeviceHolder(
            var deviceName:TextView ?= null,
            var deviceAddress:TextView ?= null
    )

}