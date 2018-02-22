package Mnist.kafka_mlapp;

import Utils.LocalFileSystem;
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

    /**
     * Publishes the file paths to kafka cluster.
     * @param args
     */
    public static void main(String[] args) {
        //Setting the cluster properties.
        Properties properties= setProperties();

        Producer<String,String> producer= new KafkaProducer<String, String>(properties);

        //Set the path of the files in the given directory.
        File file= new File("/home/murali/Desktop/mnist_png/testing");
        fileList= LocalFileSystem.getFilePaths(file);

        for(int i= 0;i < fileList.size();i++) {
            ProducerRecord<String, String> record = new ProducerRecord<String, String>("mnist_topic", fileList.get(i));
            producer.send(record);
        }
        producer.close();
    }
}
