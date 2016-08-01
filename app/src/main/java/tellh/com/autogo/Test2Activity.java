package tellh.com.autogo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.autogo.annotation.IntentValue;

public class Test2Activity extends AppCompatActivity {

    @IntentValue
    String desc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
    }
}
