package modellib.thrift.app;

import java.io.Serializable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * AUTO-GENERATE FILE, DO NOT MODIFY
 */
public class Appendix extends RealmObject implements Serializable{
	@PrimaryKey
	public String id ;  // optional
	public String url;

}