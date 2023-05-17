package com.example.application.data.service.mqtt;

import com.example.application.data.entity.SensorValues;
import com.example.application.data.service.SensorValuesService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

import java.io.IOException;
import java.time.LocalDateTime;

@Configuration
public class MqttBeans {


    private SensorValuesService sensorValuesService;

    private ObjectMapper objectMapper;

    public MqttBeans(SensorValuesService sensorValuesService, ObjectMapper objectMapper) {
        this.sensorValuesService = sensorValuesService;
        this.objectMapper = objectMapper;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory(){
        DefaultMqttPahoClientFactory factory= new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{"tcp://192.168.68.102:1883"});
        options.setUserName("sahin");
        String pass = "simsek";
        options.setPassword(pass.toCharArray());
        options.setCleanSession(true);

        factory.setConnectionOptions(options);

        return factory;

    }
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter("serverIn",
                mqttClientFactory(), "#");

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {


            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
                if(topic.equals("sensorValues")) {
                    System.out.println("This is the topic");
                }
                System.out.println(message.getPayload());
                String payload = new String((String) message.getPayload());
                try {
                    SensorValues sensorValues = objectMapper.readValue(payload, SensorValues.class);

                    // Now you can access the sensor values individually
                    LocalDateTime date = java.time.LocalDateTime.now();
                    Integer temperature = sensorValues.getTemperature();
                    Integer humidity = sensorValues.getHumidity();
                    Integer pressure = sensorValues.getPressure();

                    // Save the values to the database using the service
                    sensorValuesService.saveSensorValue(date, temperature, humidity, pressure);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        };
    }


    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        //clientId is generated using a random number
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler("serverOut", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultTopic("#");
        messageHandler.setDefaultRetained(false);
        return messageHandler;
    }

}
