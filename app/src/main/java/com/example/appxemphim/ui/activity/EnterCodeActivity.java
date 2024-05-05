package com.example.appxemphim.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.appxemphim.R;
import com.example.appxemphim.data.remote.ServiceApiBuilder;
import com.example.appxemphim.data.remote.UserService;
import com.example.appxemphim.model.ForgotDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnterCodeActivity extends AppCompatActivity {

    private EditText editTextcheckCode;
    private Button confirmButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_code);

        editTextcheckCode = findViewById(R.id.codeEditText);
        confirmButton = findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getIntent().getStringExtra("email");
                String code = editTextcheckCode.getText().toString().trim();
                if (!code.isEmpty()) {
                    checkCodeRequest(Integer.parseInt(code), email);

                } else {
                    Toast.makeText(EnterCodeActivity.this, "Vui lòng nhập mã của bạn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    public void goBack(View view) {
        finish();
    }
    private void checkCodeRequest(int code, String email) {
        UserService userService = ServiceApiBuilder.buildUserApiService(UserService.class);
        ForgotDTO checkCodeDTO = new ForgotDTO(code, email);
        Call<Void> call = userService.forgotPasswordCheckCode(checkCodeDTO);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Intent intent = new Intent(EnterCodeActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    intent.putExtra("code", code);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(EnterCodeActivity.this, "Không kiểm tra được mã", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(EnterCodeActivity.this, "Không kiểm tra được mã", Toast.LENGTH_SHORT).show();
            }
        });
    }

}