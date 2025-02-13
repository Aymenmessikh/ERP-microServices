package com.example.adminservice.Config.Security;
import com.example.adminservice.Entity.Profile;
import com.example.adminservice.Entity.User;
import com.example.adminservice.Services.ExtracteAuthorityService;
import com.example.adminservice.Services.UserService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverte {
    private final UserService userService;
    private final ExtracteAuthorityService extracteAuthorityService;
    private static final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    @Value("${jwt.auth.converter.principle-attribute}")
    private String principleAttribute;
    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    protected Set<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        String userName = getPrincipleClaimName(jwt);
        User user = userService.getUserByUserName(userName);
        if(user.getActif()==true){
            Profile activeProfile = user.getActifProfile();
            if (activeProfile != null) {
                Profile fullyLoadedProfile = extracteAuthorityService.fetchProfileWithAuthorities(activeProfile.getId());
                grantedAuthorities = extracteAuthorityService.extractAuthority(activeProfile.getId());
            }
            grantedAuthorities.addAll(authorities);
            return grantedAuthorities;
        }else throw new RuntimeException("this user is disabled");
    }

    private String getPrincipleClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (principleAttribute != null) {
            claimName = principleAttribute;
        }
        return jwt.getClaim(claimName);
    }
}