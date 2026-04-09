package desarrolloempresarial.com.impostorl.controller;

import desarrolloempresarial.com.impostorl.dto.request.CreateRoomRequest;
import desarrolloempresarial.com.impostorl.dto.request.JoinPlayerRequest;
import desarrolloempresarial.com.impostorl.dto.request.VoteRequest;
import desarrolloempresarial.com.impostorl.dto.response.CloseRoundResponse;
import desarrolloempresarial.com.impostorl.dto.response.CreateRoomResponse;
import desarrolloempresarial.com.impostorl.dto.response.JoinPlayerResponse;
import desarrolloempresarial.com.impostorl.dto.response.MeResponse;
import desarrolloempresarial.com.impostorl.dto.response.RoomStateResponse;
import desarrolloempresarial.com.impostorl.dto.response.StartGameResponse;
import desarrolloempresarial.com.impostorl.dto.response.VoteResponse;
import desarrolloempresarial.com.impostorl.services.RoomService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
//Indica que esta clase es un controlador que maneja peticiones HTTP
// y responde en formato JSON (como una API REST).
@RequestMapping("/api/rooms")
//todas las rutas empiezan con /api/rooms
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public CreateRoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request); //crea sala
    }

    @PostMapping("/{code}/players")
    public JoinPlayerResponse joinPlayer(@PathVariable String code, @Valid @RequestBody JoinPlayerRequest request) {
        return roomService.joinPlayer(code, request); //unirse a la sala
    }

    @GetMapping("/{code}")
    public RoomStateResponse getRoom(@PathVariable String code) {
        return roomService.getRoomState(code);
    }
    //Obtener el estado de la sala

    @PostMapping("/{code}/start")
    public StartGameResponse startGame(@PathVariable String code, @RequestParam UUID hostPlayerId) {
        return roomService.startGame(code, hostPlayerId); //inicia partida
    }

    @GetMapping("/{code}/me")
    public MeResponse me(@PathVariable String code, @RequestParam UUID playerId) {
        return roomService.getMe(code, playerId); //Obtener información sobre el jugado
    }

    @PostMapping("/{code}/votes")
    public VoteResponse vote(@PathVariable String code, @RequestParam UUID voterId, @Valid @RequestBody VoteRequest request) {
        return roomService.registerVote(code, voterId, request); //Registrar un voto
    }

    @PostMapping("/{code}/round/close")
    public CloseRoundResponse closeRound(@PathVariable String code, @RequestParam UUID hostPlayerId) {
        System.out.println("CAMBIO HECHO SOLO EN TEST");
        return roomService.closeRound(code, hostPlayerId);
    }
}
