package com.example.appxemphim.ui.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.UserService;
import com.example.appxemphim.model.ChangeInfoUserDTO;
import com.example.appxemphim.model.User;
import com.example.appxemphim.ui.adapter.GlideLoadImgListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeAvatarActivity extends AppCompatActivity {

    ActivityResultLauncher<CropImageContractOptions> cropImageLauncher;
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> captureImageLauncher;
    private StorageReference storageRef;
    ImageView imageViewAvatar;
    Button selectImageButton;
    Button captureImageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_avatar);

        //Init
        storageRef = FirebaseStorage.getInstance().getReference().child("Image");
        imageViewAvatar = findViewById(R.id.imageViewAvatar);
        Glide.with(this)
                .load(getSharedPreferences("UserInfo", MODE_PRIVATE).getString("avatar", ""))
                .apply(new RequestOptions().placeholder(R.drawable.placeholder_img_load))
                .into(imageViewAvatar);

        //Button back click
        ImageView btnBack = findViewById(R.id.back_button);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        //Button pick image click
        selectImageButton = findViewById(R.id.btn_choose_image);
        selectImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        });

        //Button capture click
        captureImageButton = findViewById(R.id.btn_capture_image);
        captureImageButton.setOnClickListener(v -> {
            // Create an intent to open camera
            if (ContextCompat.checkSelfPermission(ChangeAvatarActivity.this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Yêu cầu quyền truy cập vào máy ảnh
                ActivityCompat.requestPermissions(ChangeAvatarActivity.this,
                        new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
            } else {
                // Quyền đã được cấp, mở trình camera
                openCamera();
            }
        });

        //Pick image result
        pickImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    openCropLauncher(selectedImageUri);
                }
            }
        });

        //Crop image result
        cropImageLauncher = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));
                imageViewAvatar.setImageBitmap(cropped);
                SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
                int userId = prefs.getInt("userId", -1);
                uploadToFirebase(cropped, "avatar-of-user-"+userId);
            }
        });

        //Capture image result
        captureImageLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    Uri uri = bitmapToUri(ChangeAvatarActivity.this, imageBitmap);
                    openCropLauncher(uri);
                }
            }
        });
    }



    private void openCamera() {
        // Tạo Intent để mở trình camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureImageLauncher.launch(intent);
    }

    private void openCropLauncher(Uri selectedImageUri){
        // Khởi chạy ActivityResultLauncher để cắt ảnh
        CropImageOptions cropImageOptions = new CropImageOptions();
        cropImageOptions.imageSourceIncludeGallery = false;
        cropImageOptions.imageSourceIncludeCamera = true;
        cropImageOptions.fixAspectRatio = true;
        cropImageOptions.cropShape = CropImageView.CropShape.OVAL;
        cropImageOptions.guidelines = CropImageView.Guidelines.ON;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(
                selectedImageUri, cropImageOptions);
        cropImageLauncher.launch(cropImageContractOptions);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, mở trình camera
                openCamera();
            } else {
                // Quyền không được cấp, hiển thị thông báo hoặc xử lý tương ứng
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Uri bitmapToUri(Context context, Bitmap bitmap) {
        // Tạo thư mục lưu trữ ảnh
        File picturesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!picturesFolder.exists()) {
            picturesFolder.mkdirs();
        }

        // Tạo thư mục con với tên ứng dụng của bạn
        File appFolder = new File(picturesFolder, getString(R.string.app_name));
        if (!appFolder.exists()) {
            appFolder.mkdirs();
        }

        // Tạo file mới với tên
        String fileName = "review_movie_image_" + System.currentTimeMillis() + ".png";
        File file = new File(appFolder, fileName);

        // Lưu bitmap vào file
        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            Log.e("FILE_ERROR", "", e);
        }

        // Trả về URI của file đã lưu
        return Uri.fromFile(file);
    }

    private void uploadToFirebase(Bitmap bitmap, String fileName){
        storageRef = storageRef.child(fileName);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();
        showProgressBarImageAvatar();

        UploadTask uploadTask = storageRef.putBytes(imageData);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                hideProgressBarImageAvatar();
                throw Objects.requireNonNull(task.getException());
            }
            // Continue with the task to get the download URL
            return storageRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri downloadUri = task.getResult();
                changeAvatarInUserApi(downloadUri.toString());
            } else {
                hideProgressBarImageAvatar();
                Log.e("GET_URL_ERROR", "GET URL ERROR");
                Toast.makeText(getApplicationContext(), "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void changeAvatarInUserApi(String avatarUrl){
        SharedPreferences prefs = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String email = prefs.getString("email", "");
        String token = prefs.getString("token", "");
        ChangeInfoUserDTO changeInfoUserDTO =  new ChangeInfoUserDTO();
        changeInfoUserDTO.setAvatar(avatarUrl);
        changeInfoUserDTO.setEmail(email);

        UserService userService = ServiceApiBuilder.buildUserApiService(UserService.class);
        Call<User> call =  userService.changeName(changeInfoUserDTO, "Bearer "+ token);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                hideProgressBarImageAvatar();
                if (response.isSuccessful() && response.body() != null) {
                    prefs.edit().putString("avatar", response.body().getAvatar()).apply();
                    // Hiển thị thông báo thành công
                    Toast.makeText(getApplicationContext(), "Đã thay đổi ảnh đại diện thành công", Toast.LENGTH_SHORT).show();
                    // Đóng activity và trở về màn hình trước đó

                }else {
                    Toast.makeText(getApplicationContext(), "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, Throwable t) {
                Log.e("ERROR_API", "", t);
                hideProgressBarImageAvatar();
                Toast.makeText(getApplicationContext(), "Có lỗi xảy ra, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showProgressBarImageAvatar(){
        ProgressBar progressBar = findViewById(R.id.progressBarImageAvatar);
        progressBar.setVisibility(View.VISIBLE);
        captureImageButton.setEnabled(false);
        selectImageButton.setEnabled(false);
    }
    private void hideProgressBarImageAvatar(){
        ProgressBar progressBar = findViewById(R.id.progressBarImageAvatar);
        progressBar.setVisibility(View.INVISIBLE);
        captureImageButton.setEnabled(true);
        selectImageButton.setEnabled(true);
    }
}
