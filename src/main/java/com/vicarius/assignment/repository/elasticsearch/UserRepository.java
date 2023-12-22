package com.vicarius.assignment.repository.elasticsearch;

import com.vicarius.assignment.model.elasticsearch.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository("elasticUserRepository")
public interface UserRepository extends ElasticsearchRepository<User, String> {
}
