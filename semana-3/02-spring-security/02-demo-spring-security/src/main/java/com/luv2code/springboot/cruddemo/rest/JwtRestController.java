package com.luv2code.springboot.cruddemo.rest;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.luv2code.springboot.cruddemo.entity.Employee;
import com.luv2code.springboot.cruddemo.service.EmployeeService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class JwtRestController {

    /** Lo que devuelve el login. Un record: clase inmutable en una linea. */
    public record TokenResponse(String accessToken, String tokenType, long expiresIn, String user) {
    }

    private final JwtEncoder jwtEncoder;

    private final long ttlSeconds;

    private final EmployeeService employeeService;

    public JwtRestController(JwtEncoder theJwtEncoder,
            @Value("${jwt.ttl-seconds}") long theTtlSeconds,
            EmployeeService theEmployeeService) {
        jwtEncoder = theJwtEncoder;
        ttlSeconds = theTtlSeconds;
        employeeService = theEmployeeService;
    }

    @PostMapping("/auth/login")
    public TokenResponse login(Authentication authentication) {

        Instant ahora = Instant.now();

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .collect(Collectors.toList());


        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("security-combined")                 // quien lo emitio
                .issuedAt(ahora)                             // cuando  (claim "iat")
                .expiresAt(ahora.plusSeconds(ttlSeconds))    // hasta cuando (claim "exp")
                .subject(authentication.getName())           // de quien es (claim "sub")
                .claim("roles", roles)                       // que puede hacer
                .build();

        // Firmar con RS256 = RSA + SHA-256, usando la llave privada local.
        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();

        return new TokenResponse(token, "Bearer", ttlSeconds, authentication.getName());
    }


    @GetMapping("/jwt/employees")
    public List<Employee> findAll() {
        return employeeService.findAll();
    }

    @GetMapping("/jwt/employees/{employeeId}")
    public Employee getEmployee(@PathVariable int employeeId) {

        Employee theEmployee = employeeService.findById(employeeId);

        if (theEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }

        return theEmployee;
    }

    @PostMapping("/jwt/employees")
    public Employee addEmployee(@RequestBody Employee theEmployee) {

        theEmployee.setId(0);

        return employeeService.save(theEmployee);
    }

    @DeleteMapping("/jwt/employees/{employeeId}")
    public String deleteEmployee(@PathVariable int employeeId) {

        Employee tempEmployee = employeeService.findById(employeeId);

        if (tempEmployee == null) {
            throw new RuntimeException("Employee id not found - " + employeeId);
        }

        employeeService.deleteById(employeeId);

        return "Deleted employee id - " + employeeId;
    }
}
