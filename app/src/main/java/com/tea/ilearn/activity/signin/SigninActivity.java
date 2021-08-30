package com.tea.ilearn.activity.signin;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.tea.ilearn.databinding.ActivitySigninBinding;

public class SigninActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        // TODO validation error base on database (below are examples)
        /*
        binding.usernameBox.setError("用户名不存在");
        binding.passwordBox.setError("密码错误");
        binding.usernameBox.setError(null);
        binding.passwordBox.setError(null);
         */

        binding.signin.setOnClickListener($ -> {
            // TODO
        });

        binding.tosignup.setOnClickListener($ -> {
            // TODO
        });
    }
}