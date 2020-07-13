/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup.service.delivery_processes;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.eclipse.microprofile.graphql.Type;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 *
 * @author FOXCONN
 */
@Type
@Schema(
        name = "Phase",
        title = "Phase"
)
@Entity
@Table(name = "PHASE")
public class Phase extends Properties {
    
    @OneToOne
    @JoinColumn(name = "PARENT_ACTIVITIES", referencedColumnName = "NAME")
    private DeliveryProcess parentActivities;

    public DeliveryProcess getParentActivities() {
        return parentActivities;
    }

    public void setParentActivities(DeliveryProcess parentActivities) {
        this.parentActivities = parentActivities;
    }
}
