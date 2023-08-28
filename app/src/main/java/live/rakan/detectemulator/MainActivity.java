package live.rakan.detectemulator;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import live.rakan.detectemulator.security.EmulatorDetector;


public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("detectemulator");
    }

    TextView mainText;
    Button mainButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainText = findViewById(R.id.MainTextView);
        mainButton = (Button) findViewById(R.id.Button1);

       mainButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Log.d("checkEmulator",EmulatorDetector.isEmulator()+" ");
           }
       });
    }


    public native void test_syscalls();



}