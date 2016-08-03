package tellh.com.autogo;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.autogo.annotation.Bundle;
import com.autogo.annotation.SharePrefs;

import autogo.AutoGo;

public class Test2Activity extends AppCompatActivity {

    @Bundle
    Long activityLaunchTime;
    @SharePrefs
    String desc;
    @SharePrefs
    Person me;
    private EditText tvDesc;
    private EditText tvMyName;
    private EditText tvMyAge;
    private TextView tvTime;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        Log.d("TAG", "onCreate() called with: " + "savedInstanceState = [" + savedInstanceState + "]");
        desc = "Stay hungry, stay foolish.";
        me = new Person();
        me.age = 20;
        me.name = "TellH";
        initView();
        activityLaunchTime = System.currentTimeMillis();
        tvTime.setText(String.valueOf(activityLaunchTime));
    }

    @Override
    protected void onPause() {
        super.onPause();
        AutoGo.save(this);
    }

    @Override
    protected void onSaveInstanceState(android.os.Bundle outState) {
        super.onSaveInstanceState(outState);
        //when you rotate the screen, the field while be saved in bundle before activity restart.
        AutoGo.save(this, outState);
    }

    @Override
    protected void onRestoreInstanceState(android.os.Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        AutoGo.restore(this, savedInstanceState);
        tvTime.setText(String.valueOf(activityLaunchTime));
    }

    private void initView() {
        tvTime = (TextView) findViewById(R.id.tv_launchTime);
        tvDesc = (EditText) findViewById(R.id.tv_desc);
        tvMyName = (EditText) findViewById(R.id.tv_myName);
        tvMyAge = (EditText) findViewById(R.id.tv_myAge);
        tvDesc.setText(desc);
        tvMyName.setText(me.name);
        tvMyAge.setText(String.valueOf(me.age));
    }

    private void submit() {
        desc = tvDesc.getText().toString().trim();
        me.name = tvMyName.getText().toString().trim();
        me.age = Integer.parseInt(tvMyAge.getText().toString().trim());
    }

    public void onClickSave(View view) {
        submit();
        AutoGo.save(this);
    }

    public void onClickRestore(View view) {
        AutoGo.restore(this);
        tvDesc.setText(desc);
        tvMyName.setText(me.name);
        tvMyAge.setText(String.valueOf(me.age));
    }
}
