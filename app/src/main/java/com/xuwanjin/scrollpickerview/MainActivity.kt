package com.xuwanjin.scrollpickerview

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }
    private lateinit var dataList:ArrayList<String?>

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var scrollPickerRecyclerView: ScrollPickerRecyclerView = findViewById(R.id.scrollPickerRecyclerView)
        var linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        scrollPickerRecyclerView.layoutManager = linearLayoutManager
        dataList = ArrayList<String?>()
        dataList.add("常温")
        dataList.add("31℃")
        dataList.add("33℃")
        dataList.add("35℃")
        dataList.add("37℃")
        dataList.add("39℃")
        val scrollPickerAdapter = ScrollPickerAdapter.Builder(this)
                .setAdapterDataList(dataList)
                .setDefaultSelectItemIndex(2)
                .setVisibleItemCount(5)
                .setLineColor(applicationContext.getColor(R.color.colorPrimaryDark))
                .setOnSelectedItemClickListener(object :
                    ScrollPickerAdapter.OnSelectedItemClickListener {
                    override fun onSelectedItemClick(view: View) {
                        val textView: TextView =  view as TextView
                        Log.d(TAG, "onSelectedItemClick: textView.text = ${textView.text}")
                        Toast.makeText(applicationContext, textView.text, Toast.LENGTH_SHORT).show()
                    }
                })
                .build()
        scrollPickerRecyclerView.adapter = scrollPickerAdapter

    }

}