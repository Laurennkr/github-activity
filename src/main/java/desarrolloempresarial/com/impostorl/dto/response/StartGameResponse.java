package desarrolloempresarial.com.impostorl.dto.response;

import desarrolloempresarial.com.impostorl.domain.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StartGameResponse {
    private RoomStatus status;
    private int currentRound;
}

