package com.movieticket.dto.request.admin;

import lombok.Data;
import java.util.List;

@Data
public class BatchOperationRequest {
    private List<Long> ids;
    private String operation;
    private Object data;
}