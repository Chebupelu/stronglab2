package stronglab.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stronglab.model.User;
import stronglab.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/athletes")
    public List<User> getAthleteId(){
        return userRepository.findByRole("ATHLETE");
    }
}
