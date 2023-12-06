import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.carecircle.R
import com.example.carecircle.databinding.DocItemBinding

class SpecificCategoryAdapter : RecyclerView.Adapter<SpecificCategoryAdapter.ViewHolder>() {
    private var userList: List<Map<String, Any>> = ArrayList()

    class ViewHolder(val binding: DocItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DocItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            val user: Map<String, Any> = userList[position]
            val context = holder.itemView.context

            Glide.with(context)
                .load(user["profileImage"])
                .placeholder(R.drawable.profile_pic)
                .into(holder.binding.docImg)

            binding.docName.text = user["userName"].toString()
            binding.speciality.text = user["category"].toString()
            binding.ratingBar1.rating = user["rating"].toString().toFloatOrNull()!!
            // Add other user details using user map
        }
    }

    fun setData(users: List<Map<String, Any>>) {
        userList = users
        notifyDataSetChanged()
    }
}
