package desarrolloempresarial.com.impostorl.entities;

import desarrolloempresarial.com.impostorl.domain.enums.PlayerRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "players")
@Getter
@Setter
public class PlayerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "roomId", nullable = false)
    private RoomEntity room;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private boolean alive = true;

    @Enumerated(EnumType.STRING)
    private PlayerRole role;

    private String word;
}
