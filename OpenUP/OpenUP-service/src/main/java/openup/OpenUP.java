/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import openup.config.Config;
import openup.persistence.Manager;
import openup.persistence.QueryManager;
import openup.security.Security;
import org.eclipse.microprofile.auth.LoginConfig;
import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.links.Link;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.security.SecuritySchemes;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 *
 * @author FOXCONN
 */
@ApplicationScoped
@ApplicationPath("/openup")
@LoginConfig(authMethod="MP-JWT", realmName="OpenUP")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@OpenAPIDefinition(
        info = @Info(
            title = "OpenUP",
            description = "OpenUP",
            contact = @Contact(
                    name = "kimduquan",
                    url = "www.kdq.io",
                    email = "kimduquan@gmail.com"
            ),
            version = "1.0.0"
        ),
        security = {
            @SecurityRequirement(
                    name = "MP-JWT"
            )
        },
        tags = {
            @Tag(
                    name = "OpenUP",
                    description = "OpenUP"
            )
        },
        components = @Components(
                responses = {
                    @APIResponse(
                            description = "UNAUTHORIZED",
                            responseCode = "401",
                            links = {
                                @Link(
                                        name = "login",
                                        operationId = "login"
                                )
                            },
                            name = "UNAUTHORIZED"
                    )
                }
        )
)
@SecuritySchemes({
    @SecurityScheme(
            securitySchemeName = "MP-JWT",
            type = SecuritySchemeType.HTTP,
            description = "MP-JWT",
            scheme = "bearer",
            bearerFormat = "JWT"
    )
})
public class OpenUP extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(Config.class);
        classes.add(Manager.class);
        classes.add(QueryManager.class);
        classes.add(Security.class);
        return classes;
    }
}
