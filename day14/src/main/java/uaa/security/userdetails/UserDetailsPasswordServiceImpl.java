package uaa.security.userdetails;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;
import uaa.repository.UserRepo;

@RequiredArgsConstructor
@Service
public class UserDetailsPasswordServiceImpl implements UserDetailsPasswordService {

    private final UserRepo userRepo;

    public UserDetails updatePassword(UserDetails userDetails,String newPassword){
        return userRepo.findOptionalByUsername(userDetails.getUsername())
                .map(user->
                        (UserDetails)userRepo.save(user.withPassword(newPassword))
                )
                .orElse(userDetails);
    }
}
