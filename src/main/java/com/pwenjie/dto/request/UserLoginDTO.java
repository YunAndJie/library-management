package com.pwenjie.dto.request;


import com.pwenjie.common.constant.UserConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserLoginDTO {

    @NotBlank(message = UserConstants.MSG_USERNAME_NOT_BLANK)
    @Size(min = UserConstants.USERNAME_MIN_LENGTH, max = UserConstants.USERNAME_MAX_LENGTH, message = UserConstants.MSG_USERNAME_LENGTH)
    @Pattern(regexp = UserConstants.USERNAME_PATTERN, message = UserConstants.MSG_USERNAME_PATTERN)
    private String username;

    @NotBlank(message = UserConstants.MSG_PASSWORD_NOT_BLANK)
    @Size(min = UserConstants.PASSWORD_MIN_LENGTH, max = UserConstants.PASSWORD_MAX_LENGTH, message = UserConstants.MSG_PASSWORD_LENGTH)
    @Pattern(regexp = UserConstants.PASSWORD_PATTERN, message = UserConstants.MSG_PASSWORD_PATTERN)
    private String password;
}
