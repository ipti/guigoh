/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guigoh.primata.entity;

import com.guigoh.mandril.entity.EducationalObject;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author Joe
 */
@Entity
@Table(name = "primata_tags")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Tags.findAll", query = "SELECT t FROM Tags t"),
    @NamedQuery(name = "Tags.findById", query = "SELECT t FROM Tags t WHERE t.id = :id"),
    @NamedQuery(name = "Tags.findByName", query = "SELECT t FROM Tags t WHERE t.name = :name")})
public class Tags implements Serializable {
    @ManyToMany(mappedBy = "tagsCollection")
    private Collection<EducationalObject> educationalObjectCollection;
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 80)
    @Column(name = "name")
    private String name;
    @ManyToMany(mappedBy = "tagsCollection")
    private Collection<DiscussionTopic> discussionTopicCollection;

    public Tags() {
    }

    public Tags(Integer id) {
        this.id = id;
    }

    public Tags(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    @JsonIgnore
    public Collection<DiscussionTopic> getDiscussionTopicCollection() {
        return discussionTopicCollection;
    }

    public void setDiscussionTopicCollection(Collection<DiscussionTopic> discussionTopicCollection) {
        this.discussionTopicCollection = discussionTopicCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Tags)) {
            return false;
        }
        Tags other = (Tags) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.guigoh.primata.entity.Tags[ id=" + id + " ]";
    }

    @XmlTransient
    @JsonIgnore
    public Collection<EducationalObject> getEducationalObjectCollection() {
        return educationalObjectCollection;
    }

    public void setEducationalObjectCollection(Collection<EducationalObject> educationalObjectCollection) {
        this.educationalObjectCollection = educationalObjectCollection;
    }
    
}
