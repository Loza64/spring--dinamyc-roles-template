package com.server.app.components;

import com.server.app.services.PermissionService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Set;
import java.util.stream.Collectors;

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
        handlerMapping.getHandlerMethods()
                .forEach((info, method) -> getPaths(info).forEach(path -> processEndpoint(path, info)));
    }

    private Set<String> getPaths(RequestMappingInfo info) {
        if (info.getPathPatternsCondition() != null) {
            return info.getPathPatternsCondition()
                    .getPatterns() // Set<PathPattern>
                    .stream()
                    .map(Object::toString) // Convertimos PathPattern a String
                    .collect(Collectors.toSet());
        } else if (info.getPatternsCondition() != null) {
            return info.getPatternsCondition().getPatterns();
        }
        return Set.of();
    }

    private void processEndpoint(String path, RequestMappingInfo info) {
        if (Set.of("/error", "/api/auth/login", "/api/auth/signup").contains(path))
            return;

        var methods = info.getMethodsCondition().getMethods();
        if (methods.isEmpty()) {
            permissionService.createIfNotExists(path, "GET");
        } else {
            methods.forEach(method -> permissionService.createIfNotExists(path, method.name()));
        }
    }
}
