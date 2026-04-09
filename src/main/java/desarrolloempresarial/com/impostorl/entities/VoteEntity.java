package desarrolloempresarial.com.impostorl.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "votes")
@Getter
@Setter
public class VoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "roomId", nullable = false)
    private RoomEntity room;

    @ManyToOne
    @JoinColumn(name = "voterId", nullable = false)
    private PlayerEntity voter;

    @ManyToOne
    @JoinColumn(name = "votedId", nullable = false)
    private PlayerEntity voted;

    @Column(nullable = false)
    private int roundNumber;
}