package Utils;

import java.io.File;
import java.util.ArrayList;

public class LocalFileSystem {
    static ArrayList<String> fileList= new ArrayList<String>();

    /**
     * returns the entire file list in a given directory and its subdirectories.
     * @param file
     * @return
     */
    public static ArrayList<String> getFilePaths(File file){
        File[] listOfFiles = file.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileList.add(listOfFiles[i].getAbsolutePath());
            } else if (listOfFiles[i].isDirectory())
                getFilePaths(listOfFiles[i]);
        }
        return fileList;
    }
}
