package desarrolloempresarial.com.impostorl.dto.response;

import desarrolloempresarial.com.impostorl.domain.enums.PlayerRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RevealPlayerResponse {
    private UUID playerId;
    private String nickname;
    private PlayerRole role;
}

