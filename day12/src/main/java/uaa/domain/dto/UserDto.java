package uaa.domain.dto;

import lombok.Data;
import uaa.annotation.ValidEmail;
import uaa.annotation.ValidPassword;
import uaa.annotation.ValidPasswordMatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
@ValidPasswordMatch
public class UserDto implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @NotBlank
    @Size(min = 4,max = 50,message = "用户名长度必须在4到50个字符之间")
    private String username;

    @NotNull
    @ValidPassword
    private String password;

    private String matchPassword;

    @NotNull
    @ValidEmail
    private String email;

    @NotNull
    @NotBlank
    @Size(min = 4,max = 50,message = "姓名长度必须在4到50个字符之间")
    private String name;
}
