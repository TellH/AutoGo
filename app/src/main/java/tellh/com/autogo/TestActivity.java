package tellh.com.autogo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.autogo.annotation.IntentValue;

import java.util.ArrayList;

import autogo.AutoGo;


public class TestActivity extends AppCompatActivity {

    //the annotated field should not be private
    @IntentValue("myName")
    String name;
    @IntentValue int age;
    @IntentValue
    ArrayList<String> friends;
    @IntentValue
    double[] scores;
    private TextView tvAge;
    private TextView tvName;
    private TextView tvFriends;
    private TextView tvScores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        AutoGo.assign(this);
        tvAge.setText("Age:" + age);
        tvName.setText("Name:" + name);
        StringBuilder friendsStrBuilder = new StringBuilder();
        for (String friend : friends) {
            friendsStrBuilder.append(friend).append(";");
        }
        tvFriends.setText("Friends:" + friendsStrBuilder.toString());
        StringBuilder scoresStrBuilder = new StringBuilder();
        for (double score : scores) {
            scoresStrBuilder.append(score).append(";");
        }
        tvScores.setText("Scores:" + scoresStrBuilder.toString());

    }

    private void initView() {
        tvAge = (TextView) findViewById(R.id.tv_age);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvFriends = (TextView) findViewById(R.id.tv_friends);
        tvScores = (TextView) findViewById(R.id.tv_scores);
    }
}
