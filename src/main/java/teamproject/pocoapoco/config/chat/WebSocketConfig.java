package teamproject.pocoapoco.config.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocketMessageBroker
@Configuration
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat")
                //sockJs 클라이언트가 websocket handshake로 커넥션할 경로
                .setAllowedOrigins("http://*:*")
                //가능한 경로 설정 ( 전체 오픈 : 기호에따라 수정 )
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/pub");
        // /pub 으로 시작하는 stomp 메세지의 경로는 @controller @MessageMaping 메서드로 라우팅
        config.enableSimpleBroker("/sub");
        // /sub 로 시작하는 stomp 메세지는 브로커로 라우팅함
    }


}

