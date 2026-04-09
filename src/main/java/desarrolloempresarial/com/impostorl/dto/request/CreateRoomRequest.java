package desarrolloempresarial.com.impostorl.dto.request;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

//Esta clase es para crear una solicitud cuando un jugador quiere crear una sala de juego.
@Getter
@Setter
public class CreateRoomRequest {
    @NotBlank
    private String hostNickname;

    @NotBlank
    private String category;

    @Min(1)
    private Integer impostorCount = 1;
}
//info que el servidor necesita para crear esa sala
