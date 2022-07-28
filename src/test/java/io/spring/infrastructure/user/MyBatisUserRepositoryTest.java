package io.spring.infrastructure.user;

import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.DbTestBase;
import io.spring.infrastructure.repository.MyBatisUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;

import java.util.Optional;

@Import(MyBatisUserRepository.class)//读取并示例化MyBatisUserRepository，执行其中的方法，但是并不会讲这个类的实例存入spring容器中
public class MyBatisUserRepositoryTest extends DbTestBase {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyBatisUserRepository myBatisUserRepository;

    private User user;

    @BeforeEach
    public void setUp(){
        user=new User("Jmon","111","jmon@163.com","nihao","defalut");
    }

    @Test
    @Rollback(value = false)
    void testSave() {

        System.out.println(user.getId());
        myBatisUserRepository.save(user);



    }
}
