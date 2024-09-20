package edu.up.cs301shogi2024;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button quit = findViewById(R.id.butQuit);
        quit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                confirmExit();
            }
        });
    }
    private void confirmExit() {
        // Show a confirmation dialog before exiting
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Quit App")
                .setMessage("Are you sure you want to quit?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Close the app
                        finishAffinity(); // Close all activities and exit
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}