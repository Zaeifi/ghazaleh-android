package ir.ghazalehriazipsychologist.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class MenuGridAdapter(
    private val items: List<MenuItemData>,
    private val onClick: (MenuItemData) -> Unit
) : RecyclerView.Adapter<MenuGridAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val icon: ImageView = view.findViewById(R.id.itemIcon)
        val label: TextView = view.findViewById(R.id.itemLabel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_grid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.icon.setImageResource(item.iconRes)
        holder.label.text = item.title
        holder.itemView.setOnClickListener { onClick(item) }

        // آیتم اول (رزرو نوبت) به‌صورت کارت شیشه‌ای طلایی برجسته نمایش داده می‌شود
        if (position == 0) {
            holder.itemView.setBackgroundResource(R.drawable.bg_tile_glass_gold)
            holder.label.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.ink))
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_tile_glass)
            holder.label.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.ivory))
        }
    }

    override fun getItemCount() = items.size
}
