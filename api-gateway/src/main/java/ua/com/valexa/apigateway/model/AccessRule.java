package ua.com.valexa.apigateway.model;


import jakarta.persistence.*;
import jakarta.ws.rs.HttpMethod;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Entity
@Table(schema = "sys", name = "access_rule")
@Getter
@Setter
@ToString
public class AccessRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String path;
    private String method;
    private String role;
}
