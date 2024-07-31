package com.mike.TelegramRummikub.Game;

import com.mike.TelegramRummikub.TelegramRummikubApplication;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public abstract class CommonPlayer {
	@Id
	protected String userId;
	protected String image;
	@Transient
	private final String tempDir = "/tmp/";
	
	public String getImage() {
		if (this.image != null) return this.image;
		byte[] image = TelegramRummikubApplication.getTelegramBot().getUserImage(userId);
		if (image == null) {
			Random r = new Random();
			int random = r.nextInt(1, 9);
			this.image = "/common_%d.png".formatted(random);
			return this.image;
		}
		this.image = tempDir + userId + ".jpg";
		try {
			if (new File(this.image).createNewFile()) {
				FileOutputStream outputStream = new FileOutputStream(this.image);
				outputStream.write(image, 0, image.length);
				outputStream.close();
			}
		} catch (IOException ignored) {
		}
		return this.image;
	}
	
	public void deleteImage() {
		File f = new File(tempDir + userId + ".jpg");
		if (f.exists()) f.delete();
	}
}
