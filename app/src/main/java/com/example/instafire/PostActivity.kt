package com.example.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.firestore.FirebaseFirestore
private const val TAG="PostActivity"
class PostActivity : AppCompatActivity() {
    private lateinit var firestoreDb: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        firestoreDb= FirebaseFirestore.getInstance()
        val postsReference= firestoreDb.collection("posts")
        postsReference.addSnapshotListener { value, error ->
            if(value==null || error!= null)
                Log.i(TAG,"query  not respoded")
            if (value != null) {
                for(document in value.documents)
                     Log.i(TAG,"Document ${document.id} : ${document.data}")

            }
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
           startActivity(intent)
       }
        return super.onOptionsItemSelected(item)
    }
}

