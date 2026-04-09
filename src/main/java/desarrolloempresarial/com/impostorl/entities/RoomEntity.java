package desarrolloempresarial.com.impostorl.entities;

import desarrolloempresarial.com.impostorl.domain.enums.RoomStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
public class RoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @Column(nullable = false)
    private UUID hostPlayerId;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private int impostorCount = 1;

    @Column(nullable = false)
    private int currentRound = 0;

    @Column
    private String secretWord;

    @Column
    private String winner;
}
