package com.inkubator.radinaldn.smartabsen.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.inkubator.radinaldn.smartabsen.R;
import com.inkubator.radinaldn.smartabsen.config.ServerConfig;
import com.inkubator.radinaldn.smartabsen.utils.SessionManager;
import com.squareup.picasso.Picasso;

import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

import static com.inkubator.radinaldn.smartabsen.fragments.HistoriPresensiFragment.ID_PRESENSI;

public class ProfileSayaActivity extends AppCompatActivity {

    private static final String TAG = ProfileSayaActivity.class.getSimpleName();
    private Dialog myDialog;
    private SessionManager sessionManager;
    private ImageView iv_foto, iv_image;
    public static final String TAG_NIM = "nim";
    public static final String TAG_IMEI = "imei";
    public static final String TAG_NAMA = "nama";
    public static final String TAG_FOTO = "foto";
    public static final String TAG_JK = "jk";
    public static final String ID_TELEGRAM = "idTelegram";
    private String nim, imei, nama, foto, id_telegram, jk, fname;
    private TextView tv_nama, tv_nim, tv_imei, tv_id_telegram, tv_jk;
    public static final String IMAGE_DIRECTORY = "GaleriSmartPresence";
    
    private Button bt_batal, bt_upload;

    private Uri imageCaptureUri;
    private File destFile;
    private File file;
    private Boolean upflag = false;
    private Bitmap bmp;
    private File sourceFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_saya);

        // create SmartPresence folder
        file = new File(Environment.getExternalStorageDirectory()
                + "/" + IMAGE_DIRECTORY);
        if (!file.exists()) {
            file.mkdirs();
        }

        sessionManager = new SessionManager(this);

        nim = sessionManager.getMahasiswaDetail().get(TAG_NIM);
        imei = sessionManager.getMahasiswaDetail().get(TAG_IMEI);
        nama = sessionManager.getMahasiswaDetail().get(TAG_NAMA);
        foto = sessionManager.getMahasiswaDetail().get(TAG_FOTO);
        jk = sessionManager.getMahasiswaDetail().get(TAG_JK);

        id_telegram = sessionManager.getMahasiswaDetail().get(ID_TELEGRAM);

        iv_foto = findViewById(R.id.iv_foto);
        tv_nama = findViewById(R.id.tv_nama);
        tv_nim = findViewById(R.id.tv_nim);
        tv_imei = findViewById(R.id.tv_imei);
        tv_id_telegram = findViewById(R.id.tv_id_telegram);
        tv_jk = findViewById(R.id.tv_jk);

        Picasso.with(getApplicationContext()).load(ServerConfig.IMAGE_PATH+"/mahasiswa/"+foto).into(iv_foto);
        tv_nim.setText(nim);
        tv_nama.setText(nama);
        tv_imei.setText(imei);
        tv_id_telegram.setText(id_telegram);
        tv_jk.setText(jk);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.toolbar_layout);

        collapsingToolbar.setTitle(nama);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final String[] listItems = {"Dari Kamera", "Dari Galeri"};
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileSayaActivity.this);
                //mBuilder.setTitle("Choose an item");
                mBuilder.setItems(listItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "i = "+i, Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();

                        // jika dari kamera
                        if (i==0){
                            openCamera();

                        } else {
                            // jika dari galeri
                            openGaleri();
                        }
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();

            }
        });
    }

    private void openCamera() {

        destFile = new File(file, sessionManager.getMahasiswaDetail().get(TAG_NIM)+".png");
        imageCaptureUri = Uri.fromFile(destFile);

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageCaptureUri);
        startActivityForResult(intentCamera, 101);

    }

    private void openGaleri(){

        Intent intent = new Intent(Intent.ACTION_PICK);

        intent.setType("image/*");

//        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Image From Gallery"), 102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            switch (requestCode){
                case 101 :
                    if (resultCode == Activity.RESULT_OK) {
                        Log.d(TAG + ".PICK_CAMERA_IMAGE", "Selected image uri path :" + imageCaptureUri);

                        /*
                        show dialog
                         */
                        myDialog = new Dialog(ProfileSayaActivity.this);
                        myDialog.setContentView(R.layout.dialog_popup_image);
                        myDialog.setTitle("QR CODE");
                        bt_batal = myDialog.findViewById(R.id.bt_batal);
                        bt_upload = myDialog.findViewById(R.id.bt_upload);
                        iv_image = myDialog.findViewById(R.id.iv_image);
                        Picasso.with(getApplicationContext()).load("file:///storage/emulated/0/"+IMAGE_DIRECTORY+"/"+fname).resize(100, 100).into(iv_image);

                        bt_batal.setEnabled(true);
                        bt_upload.setEnabled(true);

                        bt_batal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.cancel();
                            }
                        });
                        
                        bt_upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                
                            }
                        });

                        myDialog.show();
                        
                        bmp = decodeFile(destFile);

                        if (destFile.exists()) destFile.delete();

                        try {
                            FileOutputStream out = new FileOutputStream(destFile);
                            bmp.compress(Bitmap.CompressFormat.PNG, 10, out);
                            out.flush();
                            out.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        iv_image.setVisibility(View.VISIBLE);
                        iv_image.setImageBitmap(bmp);
                    }
                    break;
                case 102 :
                    if (resultCode == Activity.RESULT_OK){
                        upflag = true;

                        Uri uriPhoto = data.getData();
                        Log.d(TAG+"PICK_GALLERY_IMAGE", "Selected image uri path :"+uriPhoto.toString());

//                        iv_image.setVisibility(View.VISIBLE);
//                        iv_image.setImageURI(uriPhoto);

                        sourceFile = new File(getPathFromGooglePhotosUri(uriPhoto));
                        fname = sessionManager.getMahasiswaDetail().get(TAG_NIM)+".png";

                        destFile = new File(file, fname);

                        Log.d(TAG, "Source File Path :" + sourceFile);

                        try {
                            copyFile(sourceFile, destFile);
                        } catch (IOException e){
                            e.printStackTrace();
                        }

                        //bmp = decodeFile(destFile);

                        /*
                        show dialog
                         */
                        myDialog = new Dialog(ProfileSayaActivity.this);
                        myDialog.setContentView(R.layout.dialog_popup_image);
                        myDialog.setTitle("QR CODE");
                        bt_batal = myDialog.findViewById(R.id.bt_batal);
                        bt_upload = myDialog.findViewById(R.id.bt_upload);
                        iv_image = myDialog.findViewById(R.id.iv_image);
                        Picasso.with(getApplicationContext()).load("file:///storage/emulated/0/"+IMAGE_DIRECTORY+"/"+fname).resize(100, 100).into(iv_image);

                        bt_batal.setEnabled(true);
                        bt_upload.setEnabled(true);

                        bt_batal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDialog.cancel();
                            }
                        });

                        bt_upload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });

                        myDialog.show();

                        iv_image.setVisibility(View.VISIBLE);
                        iv_image.setImageURI(uriPhoto);

                    }
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap decodeFile(File f) {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int IMAGE_MAX_SIZE = 1024;
        int scale = 1;
        if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Width :" + b.getWidth() + " Height :" + b.getHeight());

        fname = sessionManager.getMahasiswaDetail().get(TAG_NIM)+".png";
        destFile = new File(file, fname);

        return b;
    }

    public String getPathFromGooglePhotosUri(Uri uriPhoto) {
        if (uriPhoto == null)
            return null;

        FileInputStream input = null;
        FileOutputStream output = null;
        try {
            ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uriPhoto, "r");
            FileDescriptor fd = pfd.getFileDescriptor();
            input = new FileInputStream(fd);

            String tempFilename = getTempFilename(this);
            output = new FileOutputStream(tempFilename);

            int read;
            byte[] bytes = new byte[4096];
            while ((read = input.read(bytes)) != -1) {
                output.write(bytes, 0, read);
            }
            return tempFilename;
        } catch (IOException ignored) {
            // Nothing we can do
        } finally {
            closeSilently(input);
            closeSilently(output);
        }
        return null;
    }

    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            // Do nothing
        }
    }

    private static String getTempFilename(Context context) throws IOException {
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("image", "tmp", outputDir);
        return outputFile.getAbsolutePath();
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }
}
