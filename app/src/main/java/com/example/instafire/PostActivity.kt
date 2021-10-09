package com.example.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.instafire.model.Post
import com.example.instafire.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_post.*

private const val TAG="PostActivity"
private const val EXTRA_USERNAME="EXTRA_USERNAME"
open class PostActivity : AppCompatActivity() {
    private lateinit var firestoreDb: FirebaseFirestore
    private lateinit var posts: MutableList<Post>
    private lateinit var adapter: PostsAdapter
    private var signedInUser: User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        posts= mutableListOf()
        adapter= PostsAdapter(this,posts)
        rvPosts.adapter=adapter
        rvPosts.layoutManager=LinearLayoutManager(this)


        firestoreDb= FirebaseFirestore.getInstance()
        firestoreDb.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot->
                signedInUser=userSnapshot.toObject(User::class.java)
                Log.i(TAG,"signed in user: $signedInUser")
            }
            .addOnFailureListener{  exception->
                Log.i(TAG,"Failed to Query",exception)
            }




        var postsReference= firestoreDb.collection("posts")
            .limit(20)
            .orderBy("creation_time_ms",Query.Direction.DESCENDING)

        val username=intent.getStringExtra(EXTRA_USERNAME)
        if(username != null)
        {
            postsReference=postsReference.whereEqualTo("user.username",username)
        }


        postsReference.addSnapshotListener { value, error ->
            if(value==null || error!= null)
                Log.e(TAG,"query  not responded")
            if (value != null) {

               val postList=value.toObjects(Post::class.java)

                posts.addAll(postList)

                adapter.notifyDataSetChanged()
                for(post in postList)
                     Log.i(TAG,"Post ${post}")

            }
        }

        fabCreate.setOnClickListener {
            val intent=Intent(this,CreateActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       if(item.itemId==R.id.menu_profile)
       {
           val intent= Intent(this,ProfileActivity::class.java)
           intent.putExtra(EXTRA_USERNAME,signedInUser?.username)
           startActivity(intent)
       }
        return super.onOptionsItemSelected(item)
    }
}

