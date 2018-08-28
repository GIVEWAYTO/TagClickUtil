package com.ken.tagclicktextutil;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String contentString ="@王安石 古人之观于天地、^#65&&山川#^、草木、虫鱼、鸟兽，往往有得，以其求思之深而无不在也。" +
            "夫夷以近，则游者众；险以远，则至者少。而世之奇伟、瑰怪，^$1&&非常之观$^，常在于险远，而人之所罕至焉，故非有志者不能至也。有志矣，不随以止也，然力不足者，^#2&&亦不能至也。有志与力，而又不随以怠，至于幽暗昏惑而无物以相之，亦不能至也。#^" +
            "然力足以至焉，于人为可讥，而在己为有悔；尽吾志也而不能至者，可以无悔矣，其孰能讥之乎？^!74&&此余之所得也。!^ --- ^@5232&&王安石@^" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView content = findViewById(R.id.content);

        content.setText(new TagTextUtil().getTagContent(contentString, this, content, new TagTextUtil.ClickListener() {
            @Override
            public void click(TagTextUtil.Section section) {
                Toast.makeText(MainActivity.this, section.getName() + "   index ==  " +section.getIndex() , Toast.LENGTH_SHORT).show();
            }
        }));


    }
}
