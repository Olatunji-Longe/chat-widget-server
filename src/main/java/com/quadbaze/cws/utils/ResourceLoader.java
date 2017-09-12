package com.quadbaze.cws.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Created by Olatunji on 6/19/2016.
 */

public class ResourceLoader {

    public static final String tempFolder = "/home/temp";
    public static final String PROP_FILE="application.properties";

    public static String readTextContent(String resourceFile) throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFile);
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    public static File load(String resourceFile, boolean isJarResource){

        if(isJarResource){
            // loading resource using getResourceAsStream() method
            String targetFileName = Paths.get(tempFolder, StringUtils.substringAfterLast(resourceFile, "/")).toString();
            File targetFile = new File(targetFileName);
            try {
                InputStream inStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceFile);
                try{
                    FileUtils.copyInputStreamToFile(inStream, targetFile);
                    FileUtils.forceDeleteOnExit(targetFile);
                }finally{
                    inStream.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return targetFile;
        }else{
            return new File(Thread.currentThread().getContextClassLoader().getResource(resourceFile).getPath());
        }
	}

	/*public static String readFile(String path, Charset encoding, boolean useTemporaryFile) {
		try {
            return useTemporaryFile ? FileUtils.readFileToString(load(path), encoding) : readToString(path);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return null;
	}*/

    public static boolean cleanTempDirectory(){
        File tempDir = new File(tempFolder);
        try {
            if(tempDir.exists()){
                FileUtils.cleanDirectory(tempDir);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isRunningFromJar(){
        URL path = ResourceLoader.class.getResource("ResourceLoader.class");
        return path.getProtocol().equals("jar");
    }

}


