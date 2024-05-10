package com.project.rpsui.GUI;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class InstanceInfo {

    public Integer gamerId;

    public String username;

    public Long createdAt;

    public String sessionCode;

    public Integer sessionId;   

    public Integer opponentId;

    public String opponentUsername;
    
}
