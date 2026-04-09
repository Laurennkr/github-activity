package desarrolloempresarial.com.impostorl.dto.request;

import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;
//Esta clase es para un jugador que quiere votar a otro jugador
@Getter
@Setter
public class VoteRequest {
    @NotNull
    private UUID votedId;
}

