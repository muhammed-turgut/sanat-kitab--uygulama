package com.random.artbook;

import android.Manifest;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;
import com.random.artbook.databinding.ActivityArtBinding;
import com.random.artbook.databinding.ActivityMainBinding;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class artActivity extends AppCompatActivity {
    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;// galeriye gitmek için
    ActivityResultLauncher<String>permissionlancher; // bunu izin almak için
    Bitmap selectImage;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityArtBinding.inflate(getLayoutInflater());
        View view =binding.getRoot();
        setContentView(view);
        database= this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);
        registerLuncher();
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");

        if(info.equals("new")){
            //new
            binding.Artnametext.setText("");
            binding.Artistnametext.setText("");
            binding.yeartext.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.imageView.setImageResource(R.drawable.imageselect);
        }else {
            int artId=intent.getIntExtra("artId",1);
            binding.button.setVisibility(View.INVISIBLE);

            try {
                Cursor cursor=database.rawQuery("SELECT *FROM arts WHERE id=?",new String[] {String.valueOf(artId)});
                int artNameIx=cursor.getColumnIndex("artname");
                int painterNmaeIx=cursor.getColumnIndex("paintername");
                int yearIx=cursor.getColumnIndex("year");
                int imageIx=cursor.getColumnIndex("image");

                while (cursor.moveToNext()){
                    binding.Artnametext.setText(cursor.getColumnIndex(cursor.getString(artNameIx)));
                    binding.Artistnametext.setText(cursor.getColumnIndex(cursor.getString(painterNmaeIx)));
                    binding.yeartext.setText(cursor.getColumnIndex(cursor.getString(yearIx)));

                    byte[] byts=cursor.getBlob(imageIx);
                    Bitmap bitmap= BitmapFactory.decodeByteArray(byts,0,byts.length);
                    binding.imageView.setImageBitmap(bitmap);
                }
                cursor.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
    public  void  save(View view){
          String name=binding.Artnametext.getText().toString();
          String artistname=binding.Artistnametext.getText().toString();
          String year=binding.yeartext.getText().toString();

         Bitmap smalImage=makeSmallerImage(selectImage,300);

         ByteArrayOutputStream outputStrema=new ByteArrayOutputStream();
         smalImage.compress(Bitmap.CompressFormat.PNG,50,outputStrema);
         byte[] byteArray =outputStrema.toByteArray();

         try {
                database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY, artname VARCHAR, paintername VARCHAR, year VARCHAR, image BLOB)");
               String sqlString="INSERT INTO arts(artname, paintername,year,image) VALUES(?, ?, ?, ?)";

             SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
             sqLiteStatement.bindString(1,name);
             sqLiteStatement.bindString(2,artistname);
             sqLiteStatement.bindString(3,year);
             sqLiteStatement.bindBlob(4,byteArray);
             sqLiteStatement.execute();

         }catch (Exception e){
             e.printStackTrace();
         }
          //Burda biriken activitiyşeri kapatıp MainActivity geçiyoruz.
          Intent intent = new Intent(artActivity.this,MainActivity.class);
          intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          startActivity(intent);


    }

    public Bitmap makeSmallerImage(Bitmap image,int maximumSize){

        int widht=image.getWidth();
        int height=image.getHeight();
        float bitmapRatio=(float)widht/(float)height;
        if(bitmapRatio>0){
            widht=maximumSize;
            height=(int)(widht/bitmapRatio);
        }else{
                height=maximumSize;
                widht=(int)(height*bitmapRatio);
        }

        return  image.createScaledBitmap(image,widht,height,true);
    }
    public  void selectimage(View view){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            //Burda izin verilmişmi verilmemişmi onu kontrol ediyoruz
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                //senkbar mesajı
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Permesin needed for galeriy",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionlancher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                        }
                    }).show();
                }
                else{
                    permissionlancher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                }
            }
            else{
                //Burası Galeriye Gitiğimiz yer
                Intent intentToGallery= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }
        else {
            //Burda izin verilmişmi verilmemişmi onu kontrol ediyoruz
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                //senkbar mesajı
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Permesin needed for galeriy",Snackbar.LENGTH_INDEFINITE).setAction("Give permission", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            permissionlancher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).show();
                }
                else{
                    permissionlancher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
            else{
                //Burası Galeriye Gitiğimiz yer
                Intent intentToGallery= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);
            }
        }


    }

    private void registerLuncher(){

        activityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(result.getResultCode()== RESULT_OK){
                    Intent intentFromResult=result.getData();
                    if(intentFromResult !=null){
                       Uri imageData= intentFromResult.getData();
                       //binding.imageView.setImageURI(imageData); Aldığımız Bu veri bu halde veri tabanına kaydedemeyiz.

                        try{
                            if(Build.VERSION.SDK_INT >=28){
                                ImageDecoder.Source source= ImageDecoder.createSource(artActivity.this.getContentResolver(),imageData);
                                selectImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectImage);
                            }
                            else {
                                selectImage= MediaStore.Images.Media.getBitmap(artActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectImage);
                            }


                        }catch (Exception e){
                          e.printStackTrace();
                        }
                    }
                }
            }
        });
        permissionlancher=registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if(result){
                   Intent intentToGallery= new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   activityResultLauncher.launch(intentToGallery);
                }else{
                    Toast.makeText(artActivity.this,"Permission needed",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}