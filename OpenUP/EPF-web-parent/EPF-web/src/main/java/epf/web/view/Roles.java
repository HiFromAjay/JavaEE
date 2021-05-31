/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epf.web.view;

import epf.client.persistence.Queries;
import epf.schema.EPF;
import epf.schema.roles.Role;
import epf.schema.roles.RoleSet;
import epf.util.client.Client;
import epf.web.Session;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;

/**
 *
 * @author FOXCONN
 */
@ViewScoped
@Named("roles")
public class Roles implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
    /**
     * 
     */
    @Inject
    private transient Session session;
    
    /**
     * 
     */
    @Inject
    private transient Logger logger;
	
	/**
	 * 
	 */
	private transient List<RoleSet> roleSets;
    /**
     * 
     */
    private transient List<Role> roles;

    /**
     * @return
     */
    public List<RoleSet> getRoleSets() {
        if(roleSets == null){
            try(Client client = session.newClient("persistence")){
            	roleSets = Queries.executeQuery(
            			client, 
            			new GenericType<List<RoleSet>> () {}, 
            			EPF.SCHEMA,
            			target -> target.path(EPF.ROLE_SET), 
            			0, 
            			100);
            } 
            catch (Exception e) {
				logger.severe(e.getMessage());
			}
        }
        return roleSets;
    }

    /**
     * @return
     */
    public List<Role> getRoles() {
        if(roles == null){
            try(Client client = session.newClient("persistence")){
            	roles = Queries.executeQuery(
            			client, 
            			new GenericType<List<Role>> () {}, 
            			EPF.SCHEMA, 
            			target -> target.path(EPF.ROLE), 
            			0, 
            			100);
            } 
            catch (Exception e) {
				logger.severe(e.getMessage());
			}
        }
        return roles;
    }
}
