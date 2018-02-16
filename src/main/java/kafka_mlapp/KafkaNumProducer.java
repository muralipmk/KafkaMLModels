package kafka_mlapp;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

public class KafkaNumProducer {

    static ArrayList<String> fileList= new ArrayList<String>();

    public static Properties setProperties(){
        Properties properties= new Properties();
        //Setting bootstrap server
        properties.setProperty("bootstrap.servers","10.0.3.58:9092");
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

    public static ArrayList<String> setFilePaths(File f){
        File[] listOfFiles = f.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                fileList.add(listOfFiles[i].getAbsolutePath());
            } else if (listOfFiles[i].isDirectory())
                setFilePaths(listOfFiles[i]);
        }
        return fileList;
    }

    public static void main(String[] args) {
        //Setting the cluster properties.
        Properties properties= setProperties();

        Producer<String,String> producer= new KafkaProducer<String, String>(properties);

        //Set the path of the files in the given directory.
        File file= new File("/home/murali/Desktop/mnist_png/testing");
        setFilePaths(file);

        for(int i= 0;i < fileList.size();i++) {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>("mnist_topic", fileList.get(i));
            producer.send(record);
        }
        producer.close();
    }
}
