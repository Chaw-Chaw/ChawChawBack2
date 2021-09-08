package okky.team.chawchaw.config;

import lombok.RequiredArgsConstructor;
import okky.team.chawchaw.config.jwt.JwtTokenProvider;
import okky.team.chawchaw.user.Role;
import okky.team.chawchaw.user.UserEntity;
import okky.team.chawchaw.user.UserRepository;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT == command) {
            String path = accessor.getFirstNativeHeader("ws-path");
            String authorization = accessor.getFirstNativeHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Bearer")) {
                return null;
            } else {
                String token = authorization.replace("Bearer ", "");
                String email = jwtTokenProvider.getClaimByTokenAndKey(token, "email").asString();
                UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("유저를 찾지 못함"));
                if (!user.getRole().equals(Role.USER)) {
                    return null;
                }
            }

        } else if (StompCommand.SEND == command) {
        } else if (StompCommand.DISCONNECT == command) {
        }

        return message;
    }
}
