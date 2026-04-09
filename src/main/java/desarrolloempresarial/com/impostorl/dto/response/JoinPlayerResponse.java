package desarrolloempresarial.com.impostorl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class JoinPlayerResponse {
    private UUID playerId;
    private String nickname;
}

