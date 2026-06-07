package stronglab.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class LoginRequest {

    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Некорректный формат email")
    private String email;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 4, message = "Пароль должен быть не менее 4 символов")
    private String password;

    // Пустой конструктор (обязателен для работы библиотеки Jackson при десериализации JSON)
    public LoginRequest() {
    }

    // Конструктор со всеми полями (удобен для тестов)
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Геттеры и Сеттеры (Encapsulation)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
