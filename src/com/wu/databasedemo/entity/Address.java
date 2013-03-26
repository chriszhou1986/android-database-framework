package com.wu.databasedemo.entity;

import java.io.Serializable;

import com.wu.databasedemo.db.helper.NatrualKey;
import com.wu.databasedemo.db.helper.Table;

@Table(TableName="address")
public class Address implements Serializable {

	private static final long serialVersionUID = 7444210356893026321L;

	@NatrualKey
	private String provinceName;

	private String provinceCode;

	public Address() {
		super();
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getProvinceCode() {
		return provinceCode;
	}

	public void setProvinceCode(String provinceCode) {
		this.provinceCode = provinceCode;
	}

	@Override
	public String toString() {
		return "Address [provinceName=" + provinceName + ", provinceCode="
				+ provinceCode + "]";
	}

}
