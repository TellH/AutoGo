package tellh.com.autogo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;

import autogo.AutoGo;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickGo(View view) {
        ArrayList<String> friends=new ArrayList<>();
        friends.add("Curry");
        friends.add("Thompson");
        friends.add("Green");
        friends.add("Durante");

        AutoGo.from(MainActivity.this)
                .gotoTestActivity()
                .scores(new double[]{97.3,98.2,99})
//                .age(18)
                .myName("tlh")
                .friends(friends)
                .go();
    }

    public void onClickSharedPrefsShow(View view) {
        startActivity(new Intent(this,Test2Activity.class));
    }
}
