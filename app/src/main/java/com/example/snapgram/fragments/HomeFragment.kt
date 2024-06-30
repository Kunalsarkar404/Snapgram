
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.snapgram.Models.Post
import com.example.snapgram.Models.User
import com.example.snapgram.Utils.FOLLOW
import com.example.snapgram.Utils.POST
import com.example.snapgram.adapters.FollowRvAdapter
import com.example.snapgram.adapters.PostAdapter
import com.example.snapgram.databinding.FragmentHomeBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private var postList = ArrayList<Post>()
    private lateinit var adapter: PostAdapter
    private var followList = ArrayList<User>()
    private lateinit var followRvAdapter: FollowRvAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        adapter = PostAdapter(requireContext(), postList)
        binding.postRv.layoutManager = LinearLayoutManager(requireContext())
        followRvAdapter = FollowRvAdapter(requireContext(), followList)
        binding.followRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.followRv.adapter = followRvAdapter
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.materialToolbar2)


        // Fetch followed users and posts data
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid + FOLLOW).get()
            .addOnSuccessListener {
                val tempList = ArrayList<User>()
                followList.clear()
                for (document in it.documents) {
                    val user = document.toObject<User>()
                    user?.let { u ->
                        tempList.add(u)
                    }
                }
                followList.addAll(tempList)
                followRvAdapter.notifyDataSetChanged()
            }

        Firebase.firestore.collection(POST).get().addOnSuccessListener {
            val tempList = ArrayList<Post>()
            postList.clear()
            for (document in it.documents) {
                val post = document.toObject<Post>()
                post?.let { p ->
                    tempList.add(p)
                }
            }
            postList.addAll(tempList)
            adapter.notifyDataSetChanged()
        }
        return binding.root
    }
}
