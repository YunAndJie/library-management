package com.pwenjie.dto.request;


import com.pwenjie.common.constant.UserConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {

    @NotBlank(message = UserConstants.MSG_EMAIL_NOT_BLANK)
    private String email;

    @Pattern(regexp = UserConstants.PHONE_PATTERN, message = UserConstants.MSG_PHONE_INVALID)
    private String phone;

    @Size(max = UserConstants.AVATAR_MAX_LENGTH)
    private String avatar;
}
