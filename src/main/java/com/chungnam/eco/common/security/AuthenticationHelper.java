package com.chungnam.eco.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationHelper {

    /**
     * 현재 인증된 사용자의 ID를 반환
     * 
     * @return 사용자 ID (Long), 인증되지 않은 경우 null
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            
            try {
                return (Long) authentication.getPrincipal();
            } catch (ClassCastException e) {
                return null;
            }
        }
        
        return null;
    }

    /**
     * 현재 사용자가 인증되었는지 확인
     * 
     * @return 인증되었으면 true, 아니면 false
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && 
               authentication.isAuthenticated() && 
               !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * 현재 사용자가 관리자 권한을 가지고 있는지 확인
     * 
     * @return 관리자면 true, 아니면 false
     */
    public static boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
        }
        
        return false;
    }

    /**
     * 현재 사용자의 권한을 반환
     * 
     * @return 권한 문자열 (ADMIN, USER, 또는 null)
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(role -> role.startsWith("ROLE_"))
                    .map(role -> role.substring(5))
                    .findFirst()
                    .orElse(null);
        }
        
        return null;
    }
}
