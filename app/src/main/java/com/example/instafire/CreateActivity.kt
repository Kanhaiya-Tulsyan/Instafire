package com.example.instafire

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.instafire.model.Post
import com.example.instafire.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_create.*
import java.net.URI

private const val TAG="CreateActivity"
private const val PICK_PHOTO_CODE=1234
private var photoUri: Uri?=null
private var signedInUser: User?=null
private lateinit var firestoreDb: FirebaseFirestore
private lateinit var storageReference: StorageReference
class CreateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storageReference=FirebaseStorage.getInstance().reference
        setContentView(R.layout.activity_create)
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

        btnPickImage.setOnClickListener {
            Log.i(TAG,"open up image picker on device")
            val imagePickerIntent=Intent(Intent.ACTION_GET_CONTENT)
            imagePickerIntent.type="image/*"
            if(imagePickerIntent.resolveActivity(packageManager) != null)
            {
             startActivityForResult(imagePickerIntent,PICK_PHOTO_CODE)

            }

        }
        btnSubmit.setOnClickListener {
            handleSubmitbutton()
        }


    }

    private fun handleSubmitbutton() {
      if(photoUri== null){
          Toast.makeText(this,"no pic selected",Toast.LENGTH_SHORT).show()
          return }
       if(etDescription.text.isBlank()){
           Toast.makeText(this,"no description given",Toast.LENGTH_SHORT).show()
           return}
        if(signedInUser==null)
        {
            Toast.makeText(this,"no signed in user",Toast.LENGTH_SHORT).show()
            return
        }
        btnSubmit.isEnabled=false
        val photouploaduri= photoUri as Uri
     val photoReference=storageReference.child("images/${System.currentTimeMillis()}-photo.jpeg")
        photoReference.putFile(photouploaduri)
            .continueWithTask { photoUploadTask ->
                photoReference.downloadUrl

            }.continueWithTask { downloadUrlTask ->
                val post=Post(etDescription.text.toString(),downloadUrlTask.result.toString(),System.currentTimeMillis(),
                    signedInUser)
                firestoreDb.collection("posts").add(post)
            }.addOnCompleteListener { postCreationTask ->
                btnSubmit.isEnabled=true
                if(!postCreationTask.isSuccessful)
                {
                    Toast.makeText(this,"Failed to save post",Toast.LENGTH_SHORT).show()
            }
             etDescription.text.clear()
                imageView.setImageResource(0)
                Toast.makeText(this,"success",Toast.LENGTH_SHORT).show()
                val profileIntent=Intent(this,ProfileActivity::class.java)
                profileIntent.putExtra(EXTRA_USERNAME, signedInUser?.username)
                startActivity(profileIntent)
           finish()
            }





    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== PICK_PHOTO_CODE)
        {
            if(resultCode==Activity.RESULT_OK)
            {
                photoUri= data?.data
                Log.i(TAG,"photoUri : $photoUri")
                imageView.setImageURI(photoUri)
            }else
                Toast.makeText(this,"image picker action canceled",Toast.LENGTH_SHORT).show()
        }


    }
}
