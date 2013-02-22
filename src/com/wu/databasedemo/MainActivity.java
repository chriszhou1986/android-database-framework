package com.wu.databasedemo;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.wu.databasedemo.db.ISQLiteOpenHelper;
import com.wu.databasedemo.db.SQLiteOperator;
import com.wu.databasedemo.entity.Person;

public class MainActivity extends Activity {

	private TextView txt;
	private Button btn1;
	private Button btn2;

	private static int count = 1;
	
	private ISQLiteOpenHelper helper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		helper = SQLiteOperator.getInstance();
		txt = (TextView) findViewById(R.id.txt);
		btn1 = (Button) findViewById(R.id.btn1);
		btn1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Person p = new Person();
				p.setAddr("addr" + count);
				p.setAge(20);
				p.setName("name" + count);
				p.setTest("test" + (count));
				count++;
				helper.save(p);
			}
		});
		btn2 = (Button) findViewById(R.id.btn2);
		btn2.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				List<Person> p = helper.queryAll(Person.class);
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < p.size(); i++) {
					sb.append(p.get(i));
				}
				txt.setText(p.toString());
			}
		});
		Button btn3 = (Button) findViewById(R.id.btn3);
		btn3.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				helper.deleteAll(Person.class);
			}
		});
		Button btn4 = (Button) findViewById(R.id.btn4);
		btn4.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Person p = new Person();
				p.setAddr("addr - update");
				helper.update(p, "name3");
			}
		});
		Button btn5 = (Button) findViewById(R.id.btn5);
		btn5.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Person p = helper.queryById(Person.class, "name2");
				if (p != null) {
					txt.setText(p.toString());
				}
			}
		});
		Button btn6 = (Button) findViewById(R.id.btn6);
		btn6.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				helper.deleteById(Person.class, "name1");
			}
		});
	}

}
