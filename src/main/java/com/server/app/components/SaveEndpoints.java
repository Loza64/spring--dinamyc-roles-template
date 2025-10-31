package com.server.app.components;

import com.server.app.services.PermissionService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.util.Map;
import java.util.Optional;

@Component
public class SaveEndpoints implements ApplicationListener<ApplicationReadyEvent> {

    private final RequestMappingHandlerMapping handlerMapping;
    private final PermissionService permissionService;

    public SaveEndpoints(RequestMappingHandlerMapping handlerMapping, PermissionService permissionService) {
        this.handlerMapping = handlerMapping;
        this.permissionService = permissionService;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        handlerMapping.getHandlerMethods().forEach((info, method) -> {
            Optional.ofNullable(info.getPathPatternsCondition()).ifPresentOrElse(
                    condition -> condition.getPatterns()
                            .forEach(p -> processEndpoint(p.getPatternString(), info)),
                    () -> Optional.ofNullable(info.getPatternsCondition())
                            .ifPresent(condition -> condition.getPatterns()
                                    .forEach(path -> processEndpoint(path, info))));
        });
    }

    private void processEndpoint(String path, RequestMappingInfo info) {
        if (path.equals("/error") || path.equals("/api/auth/login") || path.equals("/api/auth/signup")) {
            return;
        }

        if (info.getMethodsCondition().getMethods().isEmpty()) {
            permissionService.createIfNotExists(path, "GET");
        } else {
            info.getMethodsCondition().getMethods()
                    .forEach(method -> permissionService.createIfNotExists(path, method.name()));
        }
    }
}
