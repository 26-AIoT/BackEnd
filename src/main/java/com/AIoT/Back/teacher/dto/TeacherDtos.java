package com.AIoT.Back.teacher.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class TeacherDtos {

    @Getter
    @Setter
    public static class RequestJoin {

        @Email
        @NotBlank
        private String email;
        @NotBlank
        @Size(min = 8, message = "비밀번호는 최소 8자 이상")
        private String password;
        @NotBlank
        @Size(min = 2, max = 20, message = "이름은 최소 2~20자")
        private String name;
    }

    @Getter
    @Setter
    public static class RequestLogin {

        @Email
        @NotBlank
        private String email;

        @NotBlank
        private String password;
    }

    @Getter
    @NoArgsConstructor
    public static class CreateRoomRequest {
        private String roomName;
    }
}
