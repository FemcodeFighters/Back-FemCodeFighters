package com.code.fighters.controller;

import com.code.fighters.model.PlayerInput;
import com.code.fighters.repository.GameRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CoopGameController {

    private final GameRoomRepository gameRoomRepository;

    @MessageMapping("/game/{roomId}/input")
    public void receiveInput(@DestinationVariable String roomId,
            @Payload PlayerInput input) {

        if (input == null) {
            log.error("Se recibió un payload nulo para la sala: {}", roomId);
            return;
        }

        gameRoomRepository.findById(roomId)
                .ifPresentOrElse(
                        room -> room.receiveInput(input),
                        () -> log.warn("Input recibido para sala inexistente: {}", roomId));
    }
}