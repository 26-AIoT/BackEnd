package com.AIoT.Back.dto.request;

import com.AIoT.Back.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RoomDtos {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoomResponse {
        private Long roomId;
        private String roomName;
        private String roomCode;
        private int studentCount;

        public RoomResponse(Room room, int studentCount) {
            this.roomId = room.getId();
            this.roomName = room.getRoomName();
            this.roomCode = room.getRoomCode();
            this.studentCount = studentCount;
        }
    }

}
