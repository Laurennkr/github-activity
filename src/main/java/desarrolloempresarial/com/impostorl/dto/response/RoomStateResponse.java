package desarrolloempresarial.com.impostorl.dto.response;

import desarrolloempresarial.com.impostorl.domain.enums.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoomStateResponse {
    private RoomStatus status;
    private String category;
    private int currentRound;
    private List<PlayerStateResponse> players;
}

