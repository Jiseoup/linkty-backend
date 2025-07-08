package com.linkty.entities.redis;

import java.io.Serializable;

import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@RedisHash(value = "refreshToken")
public class RefreshToken implements Serializable {

    @Id
    private String email;

    private String token;

    @TimeToLive
    private long expire;
}
