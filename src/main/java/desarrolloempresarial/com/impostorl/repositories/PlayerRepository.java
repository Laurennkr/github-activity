package desarrolloempresarial.com.impostorl.repositories;

import desarrolloempresarial.com.impostorl.entities.PlayerEntity;
import desarrolloempresarial.com.impostorl.entities.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID> {
    List<PlayerEntity> findByRoom(RoomEntity room);
    List<PlayerEntity> findByRoomAndAliveTrue(RoomEntity room);
    Optional<PlayerEntity> findByIdAndRoom(UUID id, RoomEntity room);
}
