package tellh.com.autogo;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.autogo.annotation.SharePrefs;

import autogo.AutoGo;

public class Test2Activity extends AppCompatActivity {

    @SharePrefs
    String desc;
    @SharePrefs
    Person me;
    private EditText tvDesc;
    private EditText tvMyName;
    private EditText tvMyAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        desc = "Stay hungry, stay foolish.";
        me = new Person();
        me.age = 20;
        me.name = "TellH";
        initView();
        AutoGo.save(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        AutoGo.save(this);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        AutoGo.restore(this);
    }

    private void initView() {
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
