import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hanto.Hook.databinding.ItemTagTagBinding
import com.hanto.Hook.model.Hook
class TagAdapter(val context: Context, val dataSet: List<Hook>) :
    RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    // 모든 Hook 객체에서 중복되지 않는 고유한 태그들을 저장할 리스트
    private val uniqueTags: List<String> by lazy {
        // flatMap을 사용하여 각 Hook 객체의 태그를 평평하게 만든 후 중복을 제거하여 리스트로 변환
        dataSet.flatMap { it.tag.orEmpty() }.distinct()
    }


    inner class TagViewHolder(val binding: ItemTagTagBinding) : RecyclerView.ViewHolder(binding.root) {
        val textView = binding.tvTagNameXl
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val binding = ItemTagTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.textView.text = uniqueTags[position]
    }

    override fun getItemCount(): Int {
        return uniqueTags.size
    }
}