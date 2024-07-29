package com.mike.TelegramRummikub.Game;

import com.mike.TelegramRummikub.TelegramRummikubApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class CommonPlayer {
	@Id
	protected String userId;
	@Transient
	private final String tempDir = "/tmp/";
	
	public String getImage() {
		byte[] image = TelegramRummikubApplication.getTelegramBot().getUserImage(userId);
		if (image == null) {
			return "";
		}
		String filename = tempDir + userId + ".jpg";
		try {
			if (new File(filename).createNewFile()) {
				FileOutputStream outputStream = null;
				outputStream = new FileOutputStream(filename);
				outputStream.write(image, 0, image.length);
				outputStream.close();
			}
		} catch (IOException ignored) {
		}
		return filename;
	}
	
	public void deleteImage() {
		File f = new File(tempDir + userId + ".jpg");
		if (f.exists()) f.delete();
	}
}
