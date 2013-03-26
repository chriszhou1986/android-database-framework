package com.wu.databasedemo.entity;

import java.io.Serializable;

import com.wu.databasedemo.db.helper.ForeignKey;
import com.wu.databasedemo.db.helper.NatrualKey;
import com.wu.databasedemo.db.helper.NotPersistent;
import com.wu.databasedemo.db.helper.Table;

@Table(TableName = "person")
public class Person implements Serializable {

	private static final long serialVersionUID = -7052906467556730227L;

	@NatrualKey
	private String name;

	private String addr;

	private int age;

	@NotPersistent
	private String test;

	@ForeignKey
	private Address address;

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

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Person [name=" + name + ", addr=" + addr + ", age=" + age
				+ ", test=" + test + ", address=" + address + "]";
	}

}
