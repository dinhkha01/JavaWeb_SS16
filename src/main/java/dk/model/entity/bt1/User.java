package dk.model.entity.bt1;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String id;

    @NotEmpty(message = "Username không được để trống")
    @Size(min = 3, max = 50, message = "Username phải có từ 3-50 ký tự")
    private String username;

    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email phải đúng định dạng")
    private String email;

    @NotBlank(message = "Role không được để trống")
    private String role = "USER";

    private String status = "ACTIVE";

    public User(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.status = "ACTIVE";
    }
}