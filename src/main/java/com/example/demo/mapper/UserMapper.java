package com.example.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.demo.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /*
    @Select("select * from user")
    List<User> selectAllUsers();

    @Select("select * from user where id = #{id}")
    User selectUserById(int id);

    @Insert("insert into user (id, username, password) values(#{id}, #{username}, #{password})")
    int insertUser(User user);

    @Delete("delete from user where id = #{id}")
    int deleteUserById(int id);
     */
}
