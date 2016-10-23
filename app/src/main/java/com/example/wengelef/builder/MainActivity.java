package com.example.wengelef.builder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = new UserBuilder()
                .age(12)
                .mail("wengelef@gmail.com")
                .username("wengelef")
                .build();

        TextView textView = (TextView) findViewById(R.id.textview);
        textView.setText(String.format("Username: %s \nEmail: %s \nAge: %d", user.username, user.mail, user.age));
    }
}
