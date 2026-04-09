package desarrolloempresarial.com.impostorl.services;

import desarrolloempresarial.com.impostorl.domain.enums.PlayerRole;
import desarrolloempresarial.com.impostorl.domain.enums.RoomStatus;
import desarrolloempresarial.com.impostorl.dto.request.CreateRoomRequest;
import desarrolloempresarial.com.impostorl.dto.request.JoinPlayerRequest;
import desarrolloempresarial.com.impostorl.dto.request.VoteRequest;
import desarrolloempresarial.com.impostorl.dto.response.CloseRoundResponse;
import desarrolloempresarial.com.impostorl.dto.response.CreateRoomResponse;
import desarrolloempresarial.com.impostorl.dto.response.ExpelledResponse;
import desarrolloempresarial.com.impostorl.dto.response.JoinPlayerResponse;
import desarrolloempresarial.com.impostorl.dto.response.MeResponse;
import desarrolloempresarial.com.impostorl.dto.response.PlayerStateResponse;
import desarrolloempresarial.com.impostorl.dto.response.RevealPlayerResponse;
import desarrolloempresarial.com.impostorl.dto.response.RoomStateResponse;
import desarrolloempresarial.com.impostorl.dto.response.StartGameResponse;
import desarrolloempresarial.com.impostorl.dto.response.VoteResponse;
import desarrolloempresarial.com.impostorl.entities.PlayerEntity;
import desarrolloempresarial.com.impostorl.entities.RoomEntity;
import desarrolloempresarial.com.impostorl.entities.VoteEntity;
import desarrolloempresarial.com.impostorl.exception.BadRequestException;
import desarrolloempresarial.com.impostorl.exception.ConflictException;
import desarrolloempresarial.com.impostorl.exception.NotFoundException;
import desarrolloempresarial.com.impostorl.repositories.PlayerRepository;
import desarrolloempresarial.com.impostorl.repositories.RoomRepository;
import desarrolloempresarial.com.impostorl.repositories.VoteRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private static final String TEAM_CIVILES = "CIVILES";
    private static final String TEAM_IMPOSTORES = "IMPOSTORES";
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random RANDOM = new Random();

    private final RoomRepository roomRepository;
    private final PlayerRepository playerRepository;
    private final VoteRepository voteRepository;
    private final Map<String, List<String>> wordBank;

    public RoomService(
            RoomRepository roomRepository,
            PlayerRepository playerRepository,
            VoteRepository voteRepository,
            ObjectMapper objectMapper
    ) {
        this.roomRepository = roomRepository;
        this.playerRepository = playerRepository;
        this.voteRepository = voteRepository;
        this.wordBank = loadWordBank(objectMapper);
    }

    @Transactional
    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        String category = normalizeCategory(request.getCategory());
        Integer impostorCountValue = request.getImpostorCount() == null ? 1 : request.getImpostorCount();

        RoomEntity room = new RoomEntity();
        room.setCode(generateCode());
        room.setStatus(RoomStatus.LOBBY);
        room.setCategory(category);
        room.setImpostorCount(impostorCountValue);
        room.setCurrentRound(0);
        room.setWinner(null);
        room.setSecretWord(null);
        room.setHostPlayerId(UUID.randomUUID());
        room = roomRepository.save(room);

        PlayerEntity host = new PlayerEntity();
        host.setRoom(room);
        host.setNickname(request.getHostNickname().trim());
        host.setAlive(true);
        host.setRole(null);
        host.setWord(null);
        host = playerRepository.save(host);

        room.setHostPlayerId(host.getId());
        roomRepository.save(room);
        return new CreateRoomResponse(room.getCode(), host.getId());
    }//develve el código de la sala y el ID del anfitrión.

    @Transactional
    public JoinPlayerResponse joinPlayer(String code, JoinPlayerRequest request) {
        RoomEntity room = findRoom(code);
        if (room.getStatus() != RoomStatus.LOBBY) {
            throw new ConflictException("Solo se pueden unir jugadores en LOBBY");
        }

        PlayerEntity player = new PlayerEntity(); //se crea un jugador y se guarda en la base de datos.
        player.setRoom(room);
        player.setNickname(request.getNickname().trim());
        player.setAlive(true);
        player = playerRepository.save(player);

        return new JoinPlayerResponse(player.getId(), player.getNickname());
    } //se devuelve una respuesta con el ID y el nombre del jugador

    @Transactional(readOnly = true)
    public RoomStateResponse getRoomState(String code) {
        RoomEntity room = findRoom(code);
        List<PlayerStateResponse> players = playerRepository.findByRoom(room).stream()
                .map(p -> new PlayerStateResponse(p.getId(), p.getNickname(), p.isAlive()))
                .toList();
        return new RoomStateResponse(room.getStatus(), room.getCategory(), room.getCurrentRound(), players);
    }

    @Transactional
    public StartGameResponse startGame(String code, UUID hostPlayerId) {
        RoomEntity room = findRoom(code);
        validateHost(room, hostPlayerId);

        if (room.getStatus() != RoomStatus.LOBBY) {
            throw new ConflictException("La partida ya fue iniciada o finalizada");
        }

        List<PlayerEntity> alivePlayers = playerRepository.findByRoomAndAliveTrue(room);
        if (alivePlayers.size() < 3) {
            throw new ConflictException("Se necesitan minimo 3 jugadores para iniciar");
        }

        int impostorCount = room.getImpostorCount();
        if (impostorCount >= alivePlayers.size()) {
            throw new ConflictException("La cantidad de impostores no es valida");
        }

        String secretWord = randomWord(room.getCategory());
        List<PlayerEntity> shuffled = new ArrayList<>(alivePlayers);
        Collections.shuffle(shuffled);

        for (int i = 0; i < shuffled.size(); i++) {
            PlayerEntity player = shuffled.get(i);
            boolean impostor = i < impostorCount;
            player.setRole(impostor ? PlayerRole.IMPOSTOR : PlayerRole.CIVIL);
            player.setWord(impostor ? null : secretWord);
            playerRepository.save(player);
        }

        room.setSecretWord(secretWord);
        room.setStatus(RoomStatus.IN_GAME);
        room.setCurrentRound(1);
        roomRepository.save(room);

        return new StartGameResponse(room.getStatus(), room.getCurrentRound());
    }

    @Transactional(readOnly = true)
    public MeResponse getMe(String code, UUID playerId) {
        RoomEntity room = findRoom(code);
        PlayerEntity player = playerRepository.findByIdAndRoom(playerId, room)
                .orElseThrow(() -> new NotFoundException("Jugador no existe en la sala"));
        return new MeResponse(player.getRole(), player.getWord());
    }

    @Transactional
    public VoteResponse registerVote(String code, UUID voterId, VoteRequest request) {
        RoomEntity room = findRoom(code);
        if (room.getStatus() != RoomStatus.IN_GAME) {
            throw new ConflictException("No se puede votar fuera de IN_GAME");
        }

        PlayerEntity voter = playerRepository.findByIdAndRoom(voterId, room)
                .orElseThrow(() -> new NotFoundException("Votante no existe en la sala"));
        PlayerEntity voted = playerRepository.findByIdAndRoom(request.getVotedId(), room)
                .orElseThrow(() -> new NotFoundException("Jugador votado no existe en la sala"));

        if (!voter.isAlive()) {
            throw new ConflictException("Un jugador muerto no puede votar");
        }
        if (!voted.isAlive()) {
            throw new ConflictException("No se puede votar por un jugador muerto");
        }
        if (voteRepository.existsByRoomAndRoundNumberAndVoter(room, room.getCurrentRound(), voter)) {
            throw new ConflictException("El jugador ya voto en esta ronda");
        }

        VoteEntity vote = new VoteEntity();
        vote.setRoom(room);
        vote.setRoundNumber(room.getCurrentRound());
        vote.setVoter(voter);
        vote.setVoted(voted);
        voteRepository.save(vote);

        return new VoteResponse("Voto registrado", room.getCurrentRound());
    }

    @Transactional
    public CloseRoundResponse closeRound(String code, UUID hostPlayerId) {
        RoomEntity room = findRoom(code);
        validateHost(room, hostPlayerId);

        if (room.getStatus() != RoomStatus.IN_GAME) {
            throw new ConflictException("Solo se puede cerrar ronda cuando la sala esta IN_GAME");
        }

        List<VoteEntity> votes = voteRepository.findByRoomAndRoundNumber(room, room.getCurrentRound());
        if (votes.isEmpty()) {
            throw new ConflictException("No se puede cerrar ronda sin votos");
        }

        Map<UUID, Integer> votesByPlayer = new HashMap<>();
        for (VoteEntity vote : votes) {
            UUID votedId = vote.getVoted().getId();
            votesByPlayer.put(votedId, votesByPlayer.getOrDefault(votedId, 0) + 1);
        }

        int maxVotes = votesByPlayer.values().stream().max(Integer::compareTo).orElse(0);
        List<UUID> top = votesByPlayer.entrySet().stream()
                .filter(e -> e.getValue() == maxVotes)
                .map(Map.Entry::getKey)
                .toList();

        CloseRoundResponse response = new CloseRoundResponse();
        response.setRoundClosed(room.getCurrentRound());

        UUID expelledId = top.get(RANDOM.nextInt(top.size()));
        PlayerEntity expelled = playerRepository.findById(expelledId)
                .orElseThrow(() -> new NotFoundException("Jugador expulsado no encontrado"));
        expelled.setAlive(false);
        playerRepository.save(expelled);
        response.setExpelled(new ExpelledResponse(
                expelled.getId(),
                expelled.getNickname(),
                expelled.getRole() == PlayerRole.IMPOSTOR
        ));

        if (expelled != null && expelled.getRole() == PlayerRole.IMPOSTOR) {
            room.setStatus(RoomStatus.FINISHED);
            room.setWinner(TEAM_CIVILES);
            roomRepository.save(room);
            return finishedResponse(room, response);
        }

        List<PlayerEntity> aliveAfter = playerRepository.findByRoomAndAliveTrue(room);
        boolean impostorAlive = aliveAfter.stream().anyMatch(p -> p.getRole() == PlayerRole.IMPOSTOR);
        if (aliveAfter.size() == 2 && impostorAlive) {
            room.setStatus(RoomStatus.FINISHED);
            room.setWinner(TEAM_IMPOSTORES);
            roomRepository.save(room);
            return finishedResponse(room, response);
        }

        room.setCurrentRound(room.getCurrentRound() + 1);
        roomRepository.save(room);
        response.setStatus(RoomStatus.IN_GAME);
        response.setNextRound(room.getCurrentRound());
        response.setAliveCount(aliveAfter.size());
        return response;
    }

    private CloseRoundResponse finishedResponse(RoomEntity room, CloseRoundResponse response) {
        List<RevealPlayerResponse> reveal = playerRepository.findByRoom(room).stream()
                .map(p -> new RevealPlayerResponse(p.getId(), p.getNickname(), p.getRole()))
                .collect(Collectors.toList());
        response.setStatus(RoomStatus.FINISHED);
        response.setWinner(room.getWinner());
        response.setSecretWord(room.getSecretWord());
        response.setReveal(reveal);
        return response;
    }

    private void validateHost(RoomEntity room, UUID hostPlayerId) {
        if (!room.getHostPlayerId().equals(hostPlayerId)) {
            throw new ConflictException("Solo el host puede ejecutar esta accion");
        }
    }

    private RoomEntity findRoom(String code) {
        return roomRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Sala no encontrada"));
    }

    private String normalizeCategory(String input) {
        if (input == null || input.isBlank()) {
            throw new BadRequestException("Categoria invalida");
        }
        String category = input.trim().toUpperCase(Locale.ROOT);
        if (!wordBank.containsKey(category)) {
            throw new BadRequestException("Categoria no existe");
        }
        return category;
    }

    private String randomWord(String category) {
        List<String> words = wordBank.get(category);
        return words.get(RANDOM.nextInt(words.size()));
    }

    private Map<String, List<String>> loadWordBank(ObjectMapper objectMapper) {
        try (InputStream inputStream = new ClassPathResource("word-bank.json").getInputStream()) {
            Map<String, List<String>> loaded = objectMapper.readValue(
                    inputStream,
                    new TypeReference<Map<String, List<String>>>() {}
            );
            if (loaded == null || loaded.isEmpty()) {
                throw new IllegalStateException("word-bank.json no tiene categorias");
            }

            Map<String, List<String>> normalized = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : loaded.entrySet()) {
                String category = entry.getKey() == null ? "" : entry.getKey().trim().toUpperCase(Locale.ROOT);
                List<String> words = entry.getValue();
                if (category.isEmpty() || words == null || words.isEmpty()) {
                    throw new IllegalStateException("Categoria o palabras invalidas en word-bank.json");
                }
                normalized.put(category, List.copyOf(words));
            }
            return Collections.unmodifiableMap(normalized);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar word-bank.json", e);
        }
    }

    private String generateCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                sb.append(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            }
            code = sb.toString();
        } while (roomRepository.findByCode(code).isPresent());
        return code;
    }
}

