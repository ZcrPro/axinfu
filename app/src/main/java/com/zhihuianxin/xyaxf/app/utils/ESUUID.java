package com.zhihuianxin.xyaxf.app.utils;

import android.os.Environment;

import com.zhihuianxin.axutil.Util;
import com.zhihuianxin.xyaxf.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * Created by John on 2015/3/24.
 */
public class ESUUID  {
	public static final String TAG = "ESUUID";
	private String uuid;

	public String getName() {
		return "uuid";
	}

	public void load() {
		// try to load uuid from sp
		App.mAxLoginSp.setUUID(uuid);

		// try to load uuid from uuid file
		if(!Util.isEnabled(uuid)){
			try {
				uuid = Util.readFile(getUuidFile().getAbsolutePath(), "utf-8");
				App.mAxLoginSp.setUUID(uuid);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// generate new uuid
		if(!Util.isEnabled(uuid)){
			resetUUID();
			App.mAxLoginSp.setUUID(uuid);
		}
	}

	private File getUuidFile(){
		String uuidPath = String.format("xiaozhang/%s.dat", URLEncoder.encode(getName()));
		File uuidFile = new File(Environment.getExternalStorageDirectory(), uuidPath);

		return uuidFile;
	}

	private void resetUUID(){
		this.uuid = UUID.randomUUID().toString();

		try{
			File uuidFile = getUuidFile();

			if(uuidFile.exists()){
				uuidFile.delete();
			}

			uuidFile.createNewFile();

			FileOutputStream fos = new FileOutputStream(uuidFile);
			fos.write(Util.getUtf8Bytes(uuid));

			fos.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public String getUUID(){
		if(!Util.isEnabled(uuid)){
			resetUUID();
			App.mAxLoginSp.setUUID(uuid);
		}

		return uuid.replace("-", " ");
	}
}
