package com.vicarius.assignment.model.elasticsearch;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Document(indexName = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;

    @Field(type = FieldType.Long)
    private Long lastLoginTimeUtc;

    public LocalDateTime getLastLoginTimeUtc() {
        return lastLoginTimeUtc != null
                ? Instant.ofEpochMilli(lastLoginTimeUtc).atZone(ZoneId.systemDefault()).toLocalDateTime()
                : null;
    }

    public void setLastLoginTimeUtc(LocalDateTime lastLoginTimeUtc) {
        this.lastLoginTimeUtc = lastLoginTimeUtc != null
                ? lastLoginTimeUtc.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                : null;
    }
}
