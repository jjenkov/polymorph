package com.plmph.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;

public class FileUtil {

    public static byte[] readFile(String path) {
        assertFileExists(path, "File path does not exist: ");
        try( FileInputStream fileInputStream = new FileInputStream(path) ){
            return fileInputStream.readAllBytes();
        } catch (IOException e) {
            throw new PolymorphException("Error reading file: " + path, e);
        }
    }

    public static boolean fileExists(String path){
        return new File(path).exists();
    }

    public static File assertFileExists(String path, String errorMessage){
        if(!fileExists(path)){
            throw new PolymorphException(errorMessage + path);
        }
        return new File(path);
    }

    public static void assertFileNotExists(String path, String errorMessage){
        if(fileExists(path)){
            throw new PolymorphException(errorMessage + path);
        }
    }

    public static File assertIsDirectory(String path) {
        File dir = assertFileExists(path, "Directory path does not exist: ");
        if(!new File(path).isDirectory()){
            throw new PolymorphException("Path is not a directory: " + path);
        }
        return dir;
    }

    public static void forEachFileInDir(String dir, Consumer<File> consumer) {
        assertIsDirectory(dir);
        File[] files = new File(dir).listFiles();
        if(files == null){
            return;
        }
        for(File file : files){
            consumer.accept(file);
        }
    }

    public static boolean makeDirs(String path){
        return new File(path).mkdirs();
    }

    public static void writeFile(String path, byte[] content){
        try(FileOutputStream fileOutputStream = new FileOutputStream(path)){
            fileOutputStream.write(content);
        } catch (IOException e){
            throw new PolymorphException("Failed to write file: " + path, e);
        }
    }

    public static void writeFile(String path, byte[] content, int offset, int length){
        try(FileOutputStream fileOutputStream = new FileOutputStream(path)){
            fileOutputStream.write(content, offset, length);
        } catch (IOException e){
            throw new PolymorphException("Failed to write file: " + path, e);
        }
    }

    public static boolean deleteFile(String path){
        return new File(path).delete();
    }

    public static String concatPaths(String ... paths){
        StringBuilder sb = new StringBuilder();
        for(String path : paths){
            sb.append(path);
            sb.append(File.separator);
        }
        return sb.toString();
    }
}
