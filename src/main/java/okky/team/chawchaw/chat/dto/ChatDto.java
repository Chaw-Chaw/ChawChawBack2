package okky.team.chawchaw.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ChatDto {

    private Long roomId;
    private Long senderId;
    private String sender;
    private String imageUrl;
    private List<ChatMessageDto> messages;

    public ChatDto(Long roodId, Long senderId, String sender, String imageUrl, List<ChatMessageDto> messages) {
        this.roomId = roodId;
        this.senderId = senderId;
        this.sender = sender;
        this.imageUrl = imageUrl;
        this.messages = messages;
    }
}