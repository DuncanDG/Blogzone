package com.dunteh.blogzone;

        import androidx.annotation.NonNull;
        import androidx.annotation.Nullable;
        import androidx.appcompat.app.AppCompatActivity;
        import androidx.core.app.ActivityCompat;
        import androidx.core.content.ContextCompat;
        import android.app.ActionBar;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Paint;
        import android.graphics.fonts.Font;
        import android.net.Uri;
        import android.os.Bundle;
        import android.text.TextUtils;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.Gallery;
        import android.widget.ImageButton;
        import android.widget.Toast;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.database.ChildEventListener;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;
        import com.squareup.picasso.Picasso;

public class PostActivity extends AppCompatActivity {
    //specifies where to store the posted image -- point to the uploaded file
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;  //to store the title and the description
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private static final int GALLERY_REQUEST_CODE = 2;
    private static final int STORAGE_PERMISSION_CODE = 0 ;
    private ImageButton img;
    private EditText posttitle;
    private EditText pdescription;
    private Button postbtn;
    private Object Uri;
    Post post;


    //In this activity we will allow the user to select an image from the device
    // gallery type a title and description for the post and then send the post

    //============
    /*We get the values from the edit text values entered by the user and store them into
      string variables then do a check to ensure that thee are no empty fields

      > A firebase reference represents a particular location in your Database and can be used for reading writing data
        to the database === they can be seen as pointers to a file in the cloud
      > Afterwards we add StorageReference firebase instance -- it represents a reference to a google cloud storage object
        that developers can upload and download object get/set object metadata and delete objects
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_acivity);

        database = FirebaseDatabase.getInstance();
        databaseRef = database.getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        img = findViewById(R.id.imageButton);
        posttitle = findViewById(R.id.posttitle);
        pdescription = findViewById(R.id.postdescription);
        postbtn = findViewById(R.id.btn_post);
        this.post = new Post();
        // ---------   posting to firebase --------
        postbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePost();
            }
        });

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });
    }

    private void uploadImage() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        //setType sets an Explicit MIME data non-text attachments audio,video,images,applications
        galleryIntent.setType("image/*");
        galleryIntent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        //start the intent
        startActivityForResult(galleryIntent.createChooser(galleryIntent,"insert picture") ,GALLERY_REQUEST_CODE);
    }

    private void savePost() {
        //Toast.makeText(PostActivity.this, posttitle.getText().toString() + pdescription.getText().toString(), Toast.LENGTH_LONG).show();
        post.setTitle(posttitle.getText().toString());
        post.setDescription(pdescription.getText().toString());
        if (post.getId() == null){
            databaseRef.child("posts").push().setValue(post);
            Toast.makeText(PostActivity.this, "Post saved successfully", Toast.LENGTH_LONG).show();
        } else {
            databaseRef.child("posts").child(post.getId()).setValue(post);
            Toast.makeText(PostActivity.this, "Post saved", Toast.LENGTH_LONG).show();
        }
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK){
            Uri uri = data.getData();
            final StorageReference filepath = storageRef.child("post_images").child(uri.getLastPathSegment());
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<android.net.Uri>() {
                        @Override
                        public void onSuccess(android.net.Uri uri) {
                            Uri url = uri;
                            post.setImageUrl(url.toString());
                            post.setImageName(filepath.getPath());
                            showImage(url.toString());
                        }
                    });

                    Toast.makeText(PostActivity.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                    final DatabaseReference newPost = databaseRef.push();

                    //adding post contents to database reference
                }
            });
        }
    }

    private void showImage(String url) {
        if (url.isEmpty() == false && url != null){
            Picasso.get()
                .load(url)
                .into(img);
        }
    }


//get image from xml to java
// mCurrentUser = mAuth.getCurrentUser();

    // Initialize Firebase Auth
    // for authentication

    //mAuth = FirebaseAuth.getInstance();

    //initialize storageReference storage
    //specifies where to store the posted image used upload image to firebase
    //database = FirebaseDatabase.getInstance();

    //storage = FirebaseStorage.getInstance().getReference();

    //initialize databaseReference
    //to store the title and the description entered





//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
//    }
//
//    private void updateUI(FirebaseUser currentUser) {
//    }

    //to check and request permission
    // Function to check and request permission
//    public void checkPermission(String permission, int requestCode)
//        {
//            // Checking if permission is not granted
//            if (ContextCompat.checkSelfPermission(
//                    PostActivity.this,
//                    permission)
//                    == PackageManager.PERMISSION_DENIED) {
//                ActivityCompat
//                        .requestPermissions(
//                                PostActivity.this,
//                                new String[] { permission },
//                                requestCode);
//            }
//            else {
//                Toast
//                        .makeText(PostActivity.this,
//                                "Permission already granted",
//                                Toast.LENGTH_SHORT)
//                        .show();
//            }
//
//    }

    // This function is called when user accept or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when user is prompt for permission.

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//     if (requestCode == STORAGE_PERMISSION_CODE) {
//            if (grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(PostActivity.this,
//                        "Storage Permission Granted",
//                        Toast.LENGTH_SHORT)
//                        .show();
//            }
//            else {
//                Toast.makeText(PostActivity.this,
//                        "Storage Permission Denied",
//                        Toast.LENGTH_SHORT)
//                        .show();
//            }
//        }
//    }

}
