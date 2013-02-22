package com.wu.databasedemo.entity;

import com.wu.databasedemo.db.helper.Column;
import com.wu.databasedemo.db.helper.NatrualKey;
import com.wu.databasedemo.db.helper.NotPersistent;
import com.wu.databasedemo.db.helper.Table;
import com.wu.databasedemo.db.helper.Column.ColumnClass;

@Table(TableName = "person")
public class Person {

	@NatrualKey
	private String name;

	private String addr;

	@Column(classType = ColumnClass.INTEGER)
	private int age;

	@NotPersistent
	private String test;

	public Person() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", addr=" + addr + ", age=" + age
				+ ", test=" + test + "]";
	}

}
