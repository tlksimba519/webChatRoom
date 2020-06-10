package com.systex.chat.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/*
 * 檔案儲存功能
 * 描述 : 將檔案來源透過InputStream讀取後寫入目的資料夾
 */
@Component
public class file {
	
	public void saveFile(String username, MultipartFile file) {
		
		byte[] buffer = new byte[1024];
		
		try {
			
			InputStream input = file.getInputStream();
			FileOutputStream output = new FileOutputStream(new File("src/main/resources/public/file/" + username + "_" + file.getOriginalFilename()));
			int length = -1;
			
            // 從來源檔案讀取資料至緩衝區 
            while((length = input.read(buffer)) != -1) { 
                // 將陣列資料寫入目的檔案 
                output.write(buffer, 0, length);
                
            } 

            // 關閉串流 
            input.close();
            output.close();
            
		} catch (Exception e) {
			
			e.printStackTrace();
			
		}
		
	}
	
}