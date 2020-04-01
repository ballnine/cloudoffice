package com.chinatelecom.ctdfs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import com.chinatelecom.udp.core.lang.json.JSONObject;

public class JSONUtil {
	public static JSONObject readJSONFile(String fileName) {
		String tempFilePath = System.getProperty("user.dir") + File.separator + "resource" + File.separator + fileName;
		JSONObject data=new JSONObject();
		InputStream input;
		try{
            input = new FileInputStream(tempFilePath);
            StringBuilder sb = new StringBuilder();
            byte[] buffer = new byte[1024];
            int length = 0;
            length = input.read(buffer);

            while(length != -1){
                sb.append(new String(buffer, 0 , length));
                length = input.read(buffer);
            }
          
             data=new JSONObject(sb.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
		return data;
	}
}
