package com.axinfu.modellib.thrift.app;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

import java.io.Serializable;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class Appendix extends RealmObject implements Serializable{
	@PrimaryKey
	public String id ;  // optional
	public String url;

}