package io.spring.core.user;

import io.spring.Util;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
public class User {
    private String id;
    private String username;
    private String password;
    private String email;
    private String bio;
    private String image;

    public User(String username,String password,String email,String bio,String image){
        this.id= UUID.randomUUID().toString();
        this.username=username;
        this.password=password;
        this.email=email;
        this.bio=bio;
        this.image=image;
    }

    public void update(String email,String username,String password,String bio,String image) {
        if(!Util.isEmpty(email)){
            this.email=email;
        }
        if(!Util.isEmpty(username)){
            this.username=username;
        }
        if(!Util.isEmpty(password)){
            this.password=password;
        }
        if(!Util.isEmpty(bio)){
            this.bio=bio;
        }
        if(!Util.isEmpty(image)){
            this.image=image;
        }
    }
}
