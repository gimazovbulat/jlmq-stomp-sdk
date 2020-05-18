package itis;

import lombok.Getter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Getter
public class JlmqConnector {
    private ConsumerWebSocketHandler webSocketHandler;
    private StompSession session;

    private final static String URL_PATTERN = "ws://localhost:%d/queue";

    private JlmqConnector(Connector connector) {
        this.webSocketHandler = connector.webSocketHandler;
        this.session = connector.session;
    }

    public static Connector connector(ConsumerWebSocketHandler webSocketHandler) {
        return new Connector(webSocketHandler);
    }

    public JlmqConsumer.ConsumerBuilder consumer() {
        return JlmqConsumer.builder(this);
    }

    public JlmqProducer.ProducerBuilder producer() {
        return JlmqProducer.builder(this);
    }

    public static class Connector {
        private String formattedUrl;
        private StompSession session;
        private ConsumerWebSocketHandler webSocketHandler;
        private int port;

        public Connector(ConsumerWebSocketHandler webSocketHandler) {
            this.webSocketHandler = webSocketHandler;
        }

        public Connector port(int port) {
            this.port = port;
            formattedUrl = String.format(URL_PATTERN, port);
            return this;
        }

        public JlmqConnector connect() throws ExecutionException, InterruptedException {
            List<Transport> transports = new ArrayList<>(1);
            transports.add(new WebSocketTransport( new StandardWebSocketClient()));

            WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));
            stompClient.setMessageConverter(new StringMessageConverter());
            session = stompClient.connect(formattedUrl, (StompSessionHandler) webSocketHandler).get();

            return new JlmqConnector(this);
        }
    }
}
