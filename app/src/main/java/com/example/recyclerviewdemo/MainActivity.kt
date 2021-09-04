package com.example.recyclerviewdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val recyclerView = RecyclerView(this)
        setContentView(recyclerView)
        val customizeLayoutManager =
            LoopLayoutManager()
        recyclerView.layoutManager = customizeLayoutManager
        val list = mutableListOf("https://scpic.chinaz.net/files/pic/pic9/202108/apic34896.jpg",
            "https://pic.netbian.com/uploads/allimg/210719/225953-1626706793ff85.jpg",
            "https://pic.netbian.com/uploads/allimg/210618/232704-162403002455f5.jpg",)

        val adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): RecyclerView.ViewHolder {
                val inflate =
                    LayoutInflater.from(this@MainActivity).inflate(R.layout.activity_main, null)
                return object : RecyclerView.ViewHolder(inflate) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                val imageView = holder.itemView.findViewById(R.id.image) as ImageView
                Glide.with(this@MainActivity).load(list[position]).into(imageView)
            }

            override fun getItemCount(): Int {
                return list.size
            }
        }

        recyclerView.adapter = adapter
        LinearSnapHelper().attachToRecyclerView(recyclerView)
    }
}