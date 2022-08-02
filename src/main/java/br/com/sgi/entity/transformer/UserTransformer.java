package br.com.sgi.entity.transformer;

import br.com.sgi.entity.User;
import br.com.sgi.service.dto.UserDTO;
import org.springframework.stereotype.Component;

@Component
public class UserTransformer extends AbstractTransformer<User, UserDTO>{
    protected UserTransformer() {
        super(User.class, UserDTO.class);
    }
}