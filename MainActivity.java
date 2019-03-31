package basicandroid.com.firebasenew;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView name;
    private Button save,list;

    private static final int CAMERA_REQUEST = 1000;
    private static final int GALLEY_REQUEST = 1001;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    String img_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.profile_image);
        name = (TextView)findViewById(R.id.name);
        save = (Button)findViewById(R.id.savebutton);
        list = (Button)findViewById(R.id.listbutton);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] options = {"Camera","Gallery","Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("PhotoImage");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(options[i].equals("Camera")){
                            cameraNew();
                        }else if(options[i].equals("Gallery")){
                            galleryNew();
                        }else if(options[i].equals("Cancel")){
                            dialogInterface.dismiss();
                        }

                    }
                });
                builder.show();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });
        
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showList();
            }
        });




    }

    private void showList() {
        Intent intent = new Intent(MainActivity.this,ImageActivity.class);
        startActivity(intent);

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void saveData() {

        if(img_url != null){

            Uri uri = Uri.parse(img_url);

            StorageReference fileStoreage = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(uri));

            fileStoreage.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String name_str = name.getText().toString().trim();

                    String image_str = taskSnapshot.getDownloadUrl().toString();

                    Member member = new Member(name_str,image_str);
                    String id_str = databaseReference.push().getKey();
                    databaseReference.child(id_str).setValue(member);
                    Toast.makeText(MainActivity.this, "Saved in Firebase", Toast.LENGTH_SHORT).show();
                    imageView.setImageResource(R.mipmap.ic_launcher);
                    name.setText("");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Not-Saved in Firebase", Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(this, "No file Selected...", Toast.LENGTH_SHORT).show();
        }



    }

    private void galleryNew() {
        Intent camerIintent = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(camerIintent,GALLEY_REQUEST);
    }

    private void cameraNew() {
        Intent camerIintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camerIintent,CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode == RESULT_OK){
          //  Bundle bundle = data.getExtras();
          //  Bitmap bitmap = (Bitmap)bundle.get("data");
            // img_url = BitMapToString(bitmap).toString();
          //  imageView.setImageBitmap(bitmap);
          //  Glide.with(MainActivity.this).load(img_url).into(imageView);

            Uri uri = data.getData();
            img_url = uri.toString();
          //  imageView.setImageURI(uri);
            Picasso.with(MainActivity.this).load(uri).into(imageView);
        }

        if(requestCode == GALLEY_REQUEST){
            Uri uri = data.getData();
             img_url = uri.toString();
            Picasso.with(MainActivity.this).load(uri).into(imageView);
           // imageView.setImageURI(uri);
            //Glide.with(MainActivity.this).load(img_url).into(imageView);
        }

    }

    private byte[] imageViewToByteArray(ImageView imageView){
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public byte[] BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
       // String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return b;
    }

}
