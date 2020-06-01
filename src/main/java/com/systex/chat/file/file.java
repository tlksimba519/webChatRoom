package com.systex.chat.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class file {
	
	public static void save(String username,MultipartFile file) {
		byte[] buffer = new byte[1024];
		try {
			InputStream input = file.getInputStream();
			FileOutputStream output = new FileOutputStream(new File("src/main/resources/public/file/"+username+"_"+file.getOriginalFilename()));
			int length = -1;
            // 從來源檔案讀取資料至緩衝區 
            while((length = input.read(buffer)) != -1) { 
                // 將陣列資料寫入目的檔案 
                output.write(buffer, 0, length);
            } 

            // 關閉串流 
            input.close();
            output.close();
            TimeUnit.SECONDS.sleep(3);
		} catch (Exception e ) {
			e.printStackTrace();
		}
	}
	
}