package com.example.myapplicationetc;

import android.annotation.SuppressLint;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.utils.MathUtils;

public class CalcActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calc);

        EditText num1 = findViewById(R.id.inputField1);
        EditText num2 = findViewById(R.id.inputField2);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        TextView result = findViewById(R.id.resultText);

        buttonAdd.setOnClickListener(v -> {
            String num1str = num1.getText().toString();
            String num2str = num2.getText().toString();

            if (num1str.isEmpty() || num2str.isEmpty()) {
                result.setText("Введите оба числа!");
                return;
            }

            int n1 = Integer.parseInt(num1str);
            int n2 = Integer.parseInt(num2str);

            int sum = MathUtils.add(n1, n2);

            result.setText("Результат: " + sum);
        });
    }
}