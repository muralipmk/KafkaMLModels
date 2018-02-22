package Mnist.HadoopkafkaMLApp;

import Utils.LocalFileSystem;
import Utils.MyHadoop;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class kafkaHadoopProducer {
    static List<String> fileList= new ArrayList<String>();

    public static Properties setProperties(){
        Properties properties= new Properties();
        //Setting bootstrap server
        properties.setProperty("bootstrap.servers","Ipaddress");
        //setting the key serializers
        properties.setProperty("key.serializer", StringSerializer.class.getName());
        //setting the value serializer
        properties.setProperty("value.serializer",StringSerializer.class.getName());
        //setting ack property
        properties.setProperty("acks","1");
        //setting number retries
        properties.setProperty("retries","3");
        properties.setProperty("linger.ms","1");
        return properties;
    }


    public static void main(String[] args) throws IOException {
        //Setting the cluster properties.
        Properties properties= setProperties();

        Producer<String,String> producer= new KafkaProducer<String, String>(properties);

        //Set the path of the files in the given directory.
        URI uri = URI.create ("hdfs://Ipaddress/murali/mnist_png");
        Configuration conf= new Configuration();
        FileSystem file = FileSystem.get (uri, conf);

        fileList= MyHadoop.getAllFilePath(new Path(uri),file);

        for(int i= 0;i < fileList.size();i++) {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>("mnist_topic", fileList.get(i));
            producer.send(record);
        }
        producer.close();
    }
}
