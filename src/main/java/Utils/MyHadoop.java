package Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides the functionality of  reading and writing files  to hdfs.
 */

public class MyHadoop {

    /**
     * @param filePath
     * @param fs
     * @return list of absolute file path present in given path
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static List<String> getAllFilePath(Path filePath, FileSystem fs) throws FileNotFoundException, IOException {
        List<String> fileList = new ArrayList<String>();
        FileStatus[] fileStatus = fs.listStatus(filePath);
        for (FileStatus fileStat : fileStatus) {
            if (fileStat.isDirectory()) {
                fileList.addAll(getAllFilePath(fileStat.getPath(), fs));
            } else {
                fileList.add(fileStat.getPath().toString());
            }
        }
        return fileList;
    }

   public static InputStream getInputStream(String url_path) throws IOException{
       //URI uri = URI.create ("hdfs://10.0.3.67:9000" + "/murali/mnist_png/testing/0/9993.png");
       URI uri = URI.create (url_path);
       Configuration conf= new Configuration();
       //System.out.println(conf.get("fs.default.name"));
       FileSystem file = FileSystem.get (uri, conf);
       return file.open(new Path(uri));
   }

}
