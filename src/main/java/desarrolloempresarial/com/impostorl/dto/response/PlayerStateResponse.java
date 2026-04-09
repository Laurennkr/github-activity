package desarrolloempresarial.com.impostorl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class PlayerStateResponse {
    private UUID id;
    private String nickname;
    private boolean alive;
}

