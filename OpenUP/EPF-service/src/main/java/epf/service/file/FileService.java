/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epf.service.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.jwt.JsonWebToken;
import epf.client.EPFException;
import epf.schema.roles.Role;
import epf.util.client.EntityOutput;

/**
 *
 * @author FOXCONN
 */
@javax.ws.rs.Path("file")
@RolesAllowed(Role.DEFAULT_ROLE)
@ApplicationScoped
public class FileService implements epf.client.file.Files {
	
	/**
	 * 
	 */
	private static final int USER_PATH_SEGMENT_INDEX = 1;
	
	/**
	 * 
	 */
	@ConfigProperty(name = ROOT)
	@Inject
	private transient String rootFolder;

	@Override
	public Response createFile(
			final List<PathSegment> paths,
			final UriInfo uriInfo,
			final InputStream input, 
			final SecurityContext security) {
		validatePaths(paths, security, HttpMethod.POST);
		final PathBuilder builder = new PathBuilder(rootFolder);
		final Path targetFolder = builder
				.paths(paths)
				.build();
		final List<Path> files = new ArrayList<>();
		try {
			targetFolder.toFile().mkdirs();
			final Path targetFile = Files.createTempFile(targetFolder, "", "");
			Files.copy(input, targetFile, StandardCopyOption.REPLACE_EXISTING);
			files.add(targetFile);
		} 
		catch (IOException e) {
			throw new EPFException(e);
		}
		final Path root = Path.of(rootFolder);
		final Link[] links = files
				.stream()
				.map(path -> {
					UriBuilder uriBuilder = uriInfo.getBaseUriBuilder().path(getClass());
					final Iterator<Path> pathIt = root.relativize(path).iterator();
					while(pathIt.hasNext()) {
						uriBuilder = uriBuilder.path(pathIt.next().toString());
					}
					return Link.fromUri(uriBuilder.build()).rel("self").build();
				})
				.collect(Collectors.toList())
				.toArray(new Link[0]);
		return Response.ok().links(links).build();
	}

	@Override
	public StreamingOutput lines(
			final UriInfo uriInfo, 
			final List<PathSegment> paths,
			final SecurityContext security) {
		validatePaths(paths, security, HttpMethod.GET);
		final PathBuilder builder = new PathBuilder(rootFolder);
		final Path targetFile = builder
				.paths(paths)
				.build();
		StreamingOutput response;
		try {
			response = new EntityOutput(Files.newInputStream(targetFile));
		} 
		catch (IOException e) {
			throw new EPFException(e);
		}
		return response;
	}

	@Override
	public Response delete(
			final UriInfo uriInfo, 
			final List<PathSegment> paths, 
			final SecurityContext security) {
		validatePaths(paths, security, HttpMethod.DELETE);
		final PathBuilder builder = new PathBuilder(rootFolder);
		final Path targetFile = builder
				.paths(paths)
				.build();
		try {
			Files.delete(targetFile);
		} 
		catch (IOException e) {
			throw new EPFException(e);
		}
		return Response.ok().build();
	}
    
	/**
	 * @param paths
	 * @param security
	 */
	protected static void validatePaths(final List<PathSegment> paths, final SecurityContext security, final String httpMethod) {
		final Principal principal = security.getUserPrincipal();
		final String principalName = principal.getName();
		final String firstPath = paths.get(0).toString();
		if(!principalName.equals(firstPath)) {
			if(principal instanceof JsonWebToken) {
				final JsonWebToken jwt = (JsonWebToken) principal;
				if(jwt.getGroups().contains(firstPath)) {
					if(paths.size() > USER_PATH_SEGMENT_INDEX) {
						final String secondPath = paths.get(1).toString();
						if(!secondPath.equals(principalName) && !httpMethod.equals(HttpMethod.GET)) {
							throw new ForbiddenException();
						}
					}
					else if(!httpMethod.equals(HttpMethod.GET)) {
						throw new ForbiddenException();
					}
				}
				else {
					throw new ForbiddenException();
				}
			}
			else {
				throw new ForbiddenException();
			}
		}
	}
}
