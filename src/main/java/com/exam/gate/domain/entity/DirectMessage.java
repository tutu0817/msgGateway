package com.exam.gate.domain.entity;

import lombok.Data;

@Data
public class DirectMessage {
    private String qos;
    private String acceptor_tel;
    private TemplateParam template_param;
    private String timestamp;
}