/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup.ejb.tasks.requirements;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateful;
import javax.ejb.LocalBean;

/**
 *
 * @author FOXCONN
 */
@Stateful
@LocalBean
@RolesAllowed({"Analyst", "Architect", "Developer", "Stakeholder", "Tester"})
public class IdentifyAndOutlineRequirements {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
}
