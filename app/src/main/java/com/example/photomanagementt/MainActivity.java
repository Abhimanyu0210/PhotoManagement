package com.example.photomanagementt;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    private ArrayList<String> imagePaths;
    private RecyclerView imagesRV;
    private RecyclerViewAdapter imageRVAdapter;
    private Object View;
    private Object view;

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        imagePaths = new ArrayList<>();
        imagesRV = findViewById(R.id.idRVImages);

        requestPermissions();
        prepareRecyclerView();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                Activity2();
            }
        });
    }
    public void Activity2(){
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        if (checkPermission()) {
            Toast.makeText(this, "Permissions granted..", Toast.LENGTH_SHORT).show();
            getImagePath();
        } else {
            requestPermission();
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    private void prepareRecyclerView() {

        imageRVAdapter = new RecyclerViewAdapter(MainActivity.this, imagePaths);

        GridLayoutManager manager = new GridLayoutManager(MainActivity.this, 4);

        imagesRV.setLayoutManager(manager);
        imagesRV.setAdapter(imageRVAdapter);
    }

    private void getImagePath() {
        boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

        if (isSDPresent) {

            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;
            Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, orderBy);

            int count = cursor.getCount();

            for (int i = 0; i < count; i++) {

                cursor.moveToPosition(i);

                int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

                imagePaths.add(cursor.getString(dataColumnIndex));
            }
            imageRVAdapter.notifyDataSetChanged();
            cursor.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted) {
                        Toast.makeText(this, "Permissions Granted..", Toast.LENGTH_SHORT).show();
                        getImagePath();
                    } else {
                        Toast.makeText(this, "Permissions denied, Permissions are required to use the app..", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }

    }

}