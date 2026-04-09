package desarrolloempresarial.com.impostorl.dto.request;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
//Esta clase es para un jugador que quiere unirse a una sala existente
@Getter
@Setter
public class JoinPlayerRequest {
    @NotBlank
    private String nickname;
}

