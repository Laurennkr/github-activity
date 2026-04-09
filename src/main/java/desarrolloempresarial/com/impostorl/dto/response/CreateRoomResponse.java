package desarrolloempresarial.com.impostorl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class CreateRoomResponse {
    private String roomCode;
    private UUID hostPlayerId;
}

