package io.spring.infrastructure.repository;

import io.spring.core.user.FollowRelation;
import io.spring.core.user.User;
import io.spring.core.user.UserRepository;
import io.spring.infrastructure.mybatis.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class MyBatisUserRepository implements UserRepository {
    //定义为不变的，因为字段内容在应用程序的生命周期内永远不变
    private final UserMapper userMapper;

    // 为什么加注解？？？？？？？？？
    /** 使用构造函数来，这样可以在没有spring的情况下可以提供userMapper示例，进行单元测试（在单元测试中可以模拟创建实例）
     *
     * 在创建生产应用程序上下文时，Spring 将自动使用此构造函数来实例化 RegisterUseCase 对象。
     * 注意，在 Spring 5 之前，我们需要在构造函数中添加 @Autowired 注解，以便 Spring 找到构造函数。
     */
    @Autowired
    public MyBatisUserRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public void save(User user) {
        if(userMapper.findById(user.getId())==null){
            userMapper.insert(user);
        }else{
            userMapper.update(user);
        }

    }

    @Override
    public Optional<User> findByEmail(String email) {
        //如果指定的值为非空值，则为一个具有现值的可选项，否则为空的可选项。
        return Optional.ofNullable(userMapper.findByEmail(email));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.findByUsername(username));
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(userMapper.findById(id));
    }

    @Override
    public void saveRelation(FollowRelation followRelation) {
        //当当前登陆用户id和目标用户没有关联时，保存两者的关系？？
        if(!findRelation(followRelation.getUserId(),followRelation.getTargetId()).isPresent()){
            userMapper.saveRelation(followRelation);
        }

    }

    @Override
    public Optional<FollowRelation> findRelation(String userId, String targetId) {
        return Optional.ofNullable(userMapper.findByRelation(userId,targetId));
    }

    @Override
    public void removeRelation(FollowRelation followRelation) {
        //当两者有关系的时候才去解除关系
        if(findRelation(followRelation.getUserId(),followRelation.getTargetId()).isPresent()){
            userMapper.deleteRelation(followRelation);
        }
    }
}
