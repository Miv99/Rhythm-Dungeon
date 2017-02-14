package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtils {
	public static String getExtension(File file) {
		return getExtension(file.getName());
	}
	
	public static String getExtension(String fileName) {
		String extension = "";
		
		int i = fileName.lastIndexOf('.');
		if (i > 0) {
			extension = fileName.substring(i + 1);
		}
			
		return extension;
	}
	
	public static ArrayList<String> getTextFileContent(String fileAbsolutePath) {
		ArrayList<String> content = new ArrayList<String>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader("file.txt"));
			
			String line = br.readLine();
			
			while (line != null) {
				line = br.readLine();
				content.add(line);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return content;
	}
}
