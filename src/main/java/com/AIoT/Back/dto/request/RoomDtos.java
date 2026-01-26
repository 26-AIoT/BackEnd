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

        public RoomResponse(Room room) {
            this.roomId = room.getId();
            this.roomName = room.getRoomName();
            this.roomCode = room.getRoomCode();
            // 피그마에는 학생수까지있어서 학생수도 불러와야되나 싶기도 한데 아직 보류
        }
    }

}
