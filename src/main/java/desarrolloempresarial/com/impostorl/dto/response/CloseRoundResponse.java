package desarrolloempresarial.com.impostorl.dto.response;

import desarrolloempresarial.com.impostorl.domain.enums.RoomStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

//devolver la información relacionada con el cierre de una ronda en el jueg
public class CloseRoundResponse {
    private int roundClosed;
    private ExpelledResponse expelled; //Contiene información sobre el jugador expulsado (si hubo uno)
    private RoomStatus status;
    private Integer nextRound;
    private Integer aliveCount;
    private String winner;
    private String secretWord;
    private List<RevealPlayerResponse> reveal;
}

