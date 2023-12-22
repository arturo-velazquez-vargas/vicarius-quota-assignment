package com.vicarius.assignment.repository.mysql;


import com.vicarius.assignment.model.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("mysqlUserRepository")
public interface UserRepository extends JpaRepository<User, String> {
}
