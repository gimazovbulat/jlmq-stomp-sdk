package itis;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class JlmqProducer {
    private ObjectMapper objectMapper;
    private StompSession webSocketSession;
    private String queueName;

    private JlmqProducer(ProducerBuilder producerBuilder) {
        this.queueName = producerBuilder.queueName;
        objectMapper = producerBuilder.objectMapper;
        webSocketSession = producerBuilder.jlmqConnector.getSession();
    }

    public void send(MessageDto message) throws IOException {
        String requestMes = objectMapper.writeValueAsString(message);
        webSocketSession.send("/messages/" + queueName, requestMes);
    }

    public static ProducerBuilder builder(JlmqConnector jlmqConnector) {
        return new ProducerBuilder(jlmqConnector);
    }

    public static class ProducerBuilder {
        private String queueName;
        private ObjectMapper objectMapper;
        private JlmqConnector jlmqConnector;

        public ProducerBuilder(JlmqConnector jlmqConnector) {
            this.jlmqConnector = jlmqConnector;
            objectMapper = new ObjectMapper();
        }

        @SneakyThrows
        public ProducerBuilder toQueue(String name) {
            queueName = name;
            return this;
        }

        public JlmqProducer create() {
            return new JlmqProducer(this);
        }
    }
}
