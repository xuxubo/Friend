package com.guidian.searchFriends.model.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class TeamQuiteRequest implements Serializable {

    private static final long serialVersionUID = 941141758449488001L;
    /**
     * id
     */
    private Long teamId;

}
