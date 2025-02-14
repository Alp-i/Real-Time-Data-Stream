package com.sau;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static Properties properties = new Properties();
    private static final Random random = new Random();
    private static final int[] USER_IDS = {
            7369, 7499, 7521, 7566, 7654, 7698, 7788, 7839, 7844, 7876, 7934
    };

    public static void main(String[] args) {
        System.out.println("Started");

        String bootstrapServers = "127.0.0.1:9092";

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Timer timer = new Timer();
        timer.schedule(new ExpenseGenerator(), 0, 1000);

        System.out.println("Producer is running...");
    }

    static class ExpenseGenerator extends TimerTask {
        private final KafkaProducer<String, String> producer = new KafkaProducer<>(properties);
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        public void run() {
            int userId = USER_IDS[random.nextInt(USER_IDS.length)];
            String description = generateRandomDescription();
            String type = generateRandomType();
            int count = random.nextInt(5) + 1;
            double payment = Math.round(random.nextDouble() * 500 * 100.0) / 100.0;

            String expenseRecord = String.format(
                    "%d, %s, %s, %s, %d, %.2f",
                    userId,
                    LocalDateTime.now().format(formatter),
                    description,
                    type,
                    count,
                    payment
            );

            String topic = "user_" + userId;
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, expenseRecord);
            producer.send(producerRecord);
        }
    }

    private static String generateRandomDescription() {
        String[] descriptions = {"Macaroni", "Jacket", "Car", "Shoes", "Laptop"};
        return descriptions[random.nextInt(descriptions.length)];
    }

    private static String generateRandomType() {
        String[] types = {"food", "clothing", "vehicle", "electronics"};
        return types[random.nextInt(types.length)];
    }
}