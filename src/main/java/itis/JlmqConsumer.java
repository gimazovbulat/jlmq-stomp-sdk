package itis;

import lombok.Getter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;

@Getter
public class JlmqConsumer {

    private JlmqConsumer(ConsumerBuilder consumerBuilder) {
    }

    public static ConsumerBuilder builder(JlmqConnector jlmqConnector) {
        return new ConsumerBuilder(jlmqConnector);
    }

    public static class ConsumerBuilder {
        private StompSession session;
        private ConsumerWebSocketHandler webSocketHandler;

        public ConsumerBuilder(JlmqConnector jlmqConnector) {
            this.session = jlmqConnector.getSession();
            this.webSocketHandler = jlmqConnector.getWebSocketHandler();
        }

        public ConsumerBuilder subscribe(String queueName) {
            System.out.println("subbing to " + queueName + ". Full path: \"/topic/\"" + queueName + ". Handler: " + webSocketHandler);
            session.subscribe("/topic/" + queueName, (StompFrameHandler) webSocketHandler);
            return this;
        }

        public ConsumerBuilder onReceive(ConsumerCallback cc) {
            webSocketHandler.setCallback(cc);
            return this;
        }

        public JlmqConsumer create() {
            return new JlmqConsumer(this);
        }
    }
}
