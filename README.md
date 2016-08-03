# AutoGo ![AutoGo](https://raw.githubusercontent.com/TellH/AutoGo/master/raw/Go.png)
[![Download](https://api.bintray.com/packages/tellh/maven/AutoGo/images/download.svg)](https://bintray.com/tellh/maven/AutoGo/_latestVersion)<br>
AutoGo is a library that bases on the runtime annotations just like the butterKnife and dagger. In other word, it will generate 
some code automatically according to the annotations you add during compiling to emancipate us from boilerplate code.

##Setup
In project root  build.gradle:
```
    dependencies {
        classpath 'com.neenbedankt.gradle.plugins:android-apt:1.8'
    }
```
In app module build.gradle:
```
    apply plugin: 'com.neenbedankt.android-apt'
    dependencies {
        compile 'com.tellh:autogo-core:1.0.1'
        apt 'com.tellh:autogo-compiler:1.0.1'
    }
```

##Usage
- StartActivity in the awesome way.<br>
Use the @IntentValue to annotate the fields of the target activity.
like this:
```
public class TargetActivity extends AppCompatActivity {
    //the annotated field should not be private
    @IntentValue("myName") String name;
    @IntentValue int age;
    @IntentValue ArrayList<String> friends;
    ......
}
```
Then make project(Ctrl+F9).<br>
Now, in your SrcActivity can start the TargetActivity just like this:
```
        AutoGo.from(SrcActivity.this)
                .gotoTestActivity()
                .age(18)
                .myName("tlh")
                .friends(friends)
                .go();
```
This code will put the data into an Intent and start activity for you.
In the TargetActivity can receive the data through this code:
```
AutoGo.assign(this);
```
Pretty awesome, right?

- Save and restore data with sharedPreference in the awesome way.<br>
Use @SharePrefs to annotate the fields of your class :
```
public class Sample{
    @SharePrefs("customKey")
    String desc;
    @SharePrefs
    Person me;
    ......
}
```
Whenever you want to sava the data, just Call:
```
        AutoGo.save(this);
```
Then whenever your want to restore the data to assign the field in your class, just call:
```
        AutoGo.restore(this);
```
- Save and restore data with Bundle in the awesome way.<br>
Use @Bundle to annotate the fields of your class :
```
    @Bundle
    Long activityLaunchTime;
```
Whenever you want to sava the data, just Call:
```
    protected void onSaveInstanceState(android.os.Bundle outState) {
        AutoGo.save(this, outState);
        ......
    }
```
Then whenever your want to restore the data to assign the field in your class, just call:
```
    protected void onRestoreInstanceState(android.os.Bundle savedInstanceState) {
        AutoGo.restore(this, savedInstanceState);
        ......
    }
```

##Improve
If you have some problem or advice, pleace don't hesitate to raise an issue.<br>
Just have fun and hope this will lessen your code :)

##License

Apache Version 2.0<br>
```
Copyright 2016 Rahul Yadav

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License
