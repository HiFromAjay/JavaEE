/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openup.schema.work_products;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.eclipse.microprofile.graphql.Type;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import openup.schema.OpenUP;
import openup.schema.roles.Role;

/**
 *
 * @author FOXCONN
 */
@Type(OpenUP.ARTIFACT)
@Schema(name = OpenUP.ARTIFACT, title = "Artifact")
@Entity(name = OpenUP.ARTIFACT)
@Table(schema = OpenUP.SCHEMA, name = "OPENUP_ARTIFACT")
public class Artifact implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 
     */
    @ManyToOne
    @JoinColumn(name = "ARTIFACT")
    private epf.schema.work_products.Artifact artifact;
    
    /**
     * 
     */
    @ManyToOne
    @JoinColumn(name = "RESPONSIBLE")
    private Role responsible;
    
    /**
     * 
     */
    @ManyToMany
    @JoinTable(
    		name = "OPENUP_ROLE_MODIFIES",
    		schema = OpenUP.SCHEMA,
    		joinColumns = {@JoinColumn(name = "ID")},
    		inverseJoinColumns = {@JoinColumn(name = "NAME")},
    		indexes = {@Index(columnList = "NAME")}
    		)
    private List<Role> modifiedBy;
    
    /**
     * 
     */
    @Column(name = "NAME", nullable = false)
    private String name;
    
    /**
     * 
     */
    @Column(name = "SUMMARY")
    private String summary;

    public epf.schema.work_products.Artifact getArtifact() {
        return artifact;
    }

    public void setArtifact(final epf.schema.work_products.Artifact artifact) {
        this.artifact = artifact;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public Role getResponsible() {
		return responsible;
	}

	public void setResponsible(final Role responsible) {
		this.responsible = responsible;
	}

	public List<Role> getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(final List<Role> modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}
}
