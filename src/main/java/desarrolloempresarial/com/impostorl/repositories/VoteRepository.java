package desarrolloempresarial.com.impostorl.repositories;

import desarrolloempresarial.com.impostorl.entities.PlayerEntity;
import desarrolloempresarial.com.impostorl.entities.RoomEntity;
import desarrolloempresarial.com.impostorl.entities.VoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<VoteEntity, UUID> {
    List<VoteEntity> findByRoomAndRoundNumber(RoomEntity room, int roundNumber);

    boolean existsByRoomAndRoundNumberAndVoter(
            RoomEntity room,
            int roundNumber,
            PlayerEntity voter
    );
}
