package com.code.fighters.repository;

import com.code.fighters.model.GameRoom;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class GameRoomRepository {

    private final Map<String, GameRoom> rooms = new ConcurrentHashMap<>();

    public void save(GameRoom room) {
        rooms.put(room.getRoomId(), room);
    }

    public Optional<GameRoom> findById(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    public Collection<GameRoom> findAll() {
        return rooms.values();
    }

    public void remove(String roomId) {
        rooms.remove(roomId);
    }

    public boolean exists(String roomId) {
        return rooms.containsKey(roomId);
    }
}
