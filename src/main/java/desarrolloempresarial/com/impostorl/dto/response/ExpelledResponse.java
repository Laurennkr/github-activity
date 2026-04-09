package desarrolloempresarial.com.impostorl.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor

//Contiene información sobre el jugador expulsado (si hubo uno)
public class ExpelledResponse {
    private UUID id;
    private String nickname;
    private boolean wasImpostor;
}

