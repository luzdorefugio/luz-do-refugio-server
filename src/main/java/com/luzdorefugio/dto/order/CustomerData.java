package com.luzdorefugio.dto.order;

import lombok.Data;

@Data
public class CustomerData {
    private String name;
    private String email;
    private String phone;
    private String nif;
    private String address;
    private String city;
    private String zipCode;
}
