package com.AIoT.Back.dto.request;

import com.AIoT.Back.domain.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class RoomDtos {

    // 데이터를 받는 그릇 (이게 없어서 에러)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomPostRequest {
        private String roomTitle; // 프론트엔드의 'roomName'과 철자가 똑같아야 함!
    }

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
