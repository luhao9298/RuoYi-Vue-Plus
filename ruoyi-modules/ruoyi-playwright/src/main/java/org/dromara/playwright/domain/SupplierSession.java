package org.dromara.playwright.domain;

import lombok.Data;
import java.util.Date;
import java.util.Map;

@Data
public class SupplierSession {
    private String supplierName;
    private String token;
    private String cookie;
    private Map<String, String> headers;
    private String sessionId;
    private Date loginTime;
    private Date expireTime;
    private String username;
    private String password;
    private Map<String, Object> extra;
} 