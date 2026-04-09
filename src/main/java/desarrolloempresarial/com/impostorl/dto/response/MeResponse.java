package desarrolloempresarial.com.impostorl.dto.response;

import desarrolloempresarial.com.impostorl.domain.enums.PlayerRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MeResponse {
    private PlayerRole role;
    private String word;
}

