package com.jwt.JWT_DEMO.service;

import com.jwt.JWT_DEMO.model.User;
import com.jwt.JWT_DEMO.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final EmployeeRepository employeeRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = employeeRepository.findByUsername(username);
        List<GrantedAuthority> li=new ArrayList<>();
        li.add(new SimpleGrantedAuthority(user.get().getRole()));
        return new org.springframework.security.core.userdetails.User(user.get().getUsername(),user.get().getPassword(),li);
    }

    public boolean isAlreadyExistsUser(String userName){
        Optional<User> user =employeeRepository.findByUsername(userName);
       if(user.isPresent()){
           return true;
       }
       return false;
    }

    public User saveUser(User user){
        return employeeRepository.save(user);
    }

}


