package stronglab.dto;

public class UserDTO {
    private Long id;
    private String email;
    private String role;
    private String name;

    public UserDTO(Long id, String email, String role, String name) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.name = name;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getName() {return name;}
}
