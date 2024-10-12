package com.guidian.searchFriends.model.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -4162304142710323660L;;

    protected int pageSize = 10;

    protected int pageNum;

}
