package xyz.wingio.plugins.sessions

import android.view.*
import androidx.recyclerview.widget.RecyclerView

class Adapter(var data: MutableList<Session>) :
    RecyclerView.Adapter<Adapter.ViewHolder?>() {

    var onLogoutClick: (sessionId: String) -> Unit = {}

    class ViewHolder(private val adapter: Adapter, val item: SessionCard) : RecyclerView.ViewHolder(item)

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(this, SessionCard(parent.context!!))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val session = data[position]
        holder.item.logOutBtn.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                onLogoutClick(session.idHash)
            }
        }

        holder.item.title = "${session.clientInfo.os} · ${session.clientInfo.platform}"
        holder.item.location = session.clientInfo.location

        holder.item.isMobile = session.clientInfo.os.startsWith("Android") || session.clientInfo.os.startsWith("iOS")
    }

    fun updateData(newData: List<Session>?) {
        newData?.let {
            data.clear()
            addData(newData)
        }
    }

    fun addData(data: List<Session>?) {
        this.data.addAll(data!!)
        notifyDataSetChanged()
    }
}