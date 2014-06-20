/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guigoh.mandril.bean;

import com.guigoh.mandril.bo.AuthorBO;
import com.guigoh.mandril.bo.EducationalObjectBO;
import com.guigoh.mandril.bo.EducationalObjectMediaBO;
import com.guigoh.mandril.entity.Author;
import com.guigoh.mandril.entity.EducationalObject;
import com.guigoh.mandril.entity.EducationalObjectMedia;
import com.guigoh.primata.bo.InterestsBO;
import com.guigoh.primata.bo.SocialProfileBO;
import com.guigoh.primata.bo.TagsBO;
import com.guigoh.primata.bo.util.CookieService;
import com.guigoh.primata.bo.util.UploadService;
import com.guigoh.primata.entity.Interests;
import com.guigoh.primata.entity.SocialProfile;
import com.guigoh.primata.entity.Tags;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.Part;

/**
 *
 * @author ipti004
 */
@ViewScoped
@ManagedBean(name = "submitFormBean")
public class SubmitFormBean implements Serializable {

    private EducationalObject educationalObject;
    private List<Interests> interestThemesList;
    private List<Author> authorList;
    private Author author;
    private String tags;
    private Part imageFile;
    private Part mediaFile;
    private List<Part> mediaList;
    private boolean submitted;
 
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            submitted = false;
            interestThemesList = new ArrayList<>();
            authorList = new ArrayList<>();
            educationalObject = new EducationalObject();
            author = new Author();
            mediaList = new ArrayList<>();
            loadInterestThemes(); 
        }
    }

    public void addAuthor() { 
        if (authorList.size() < 4) {
                authorList.add(author);
        }
    }

    public void addMedia() throws IOException {
        if (mediaList.size() < 3) {
            if (!mediaFile.getSubmittedFileName().equals("")) {
                mediaList.add(mediaFile);
            }
        }
    }

    public void submitForm() throws IOException {
        EducationalObjectBO educationalObjectBO = new EducationalObjectBO();
        SocialProfileBO socialProfileBO = new SocialProfileBO();
        TagsBO tagBO = new TagsBO();
        AuthorBO authorBO = new AuthorBO();
        EducationalObjectMediaBO educationalObjectMediaBO = new EducationalObjectMediaBO();
        SocialProfile socialProfile = socialProfileBO.findSocialProfile(CookieService.getCookie("token"));
        educationalObject.setSocialProfileId(socialProfile);
        educationalObject.setStatus("PE");
        educationalObject.setDate(educationalObjectBO.getServerTime());
        String imagePath = System.getProperty("user.home") + File.separator + "guigoh" + File.separator + "educationalobjects" + File.separator + educationalObject.getName() + File.separator + "image" + File.separator;
        UploadService.uploadFile(imageFile, imagePath);
        educationalObject.setImage("http://cdn.guigoh.com/educationalobjects/" + educationalObject.getName() + "/image/" + imageFile.getSubmittedFileName());
        educationalObjectBO.create(educationalObject);
        String[] tagArray = tags.replace(" ", "").split(",");
        List<EducationalObject> educationalObjectList = new ArrayList<>();
        educationalObjectList.add(educationalObject);
        for (String tagValue : tagArray) {
            Tags tag = new Tags();
            tag.setEducationalObjectCollection(educationalObjectList);
            tag.setName(tagValue);
            tagBO.create(tag);
        }
        for (Author authorOE : authorList) {
            authorOE.setEducationalObjectCollection(educationalObjectList);
            authorBO.create(authorOE);
        }
        String mediaPath = System.getProperty("user.home") + File.separator + "guigoh" + File.separator + "educationalobjects"+ File.separator + educationalObject.getName() + File.separator +"media" + File.separator;
        for (Part part : mediaList) {
            EducationalObjectMedia educationalObjectMedia = new EducationalObjectMedia();
            educationalObjectMedia.setEducationalObjectId(educationalObject);
            educationalObjectMedia.setSize(BigInteger.valueOf(part.getSize()));
            educationalObjectMedia.setName(part.getSubmittedFileName());
            educationalObjectMedia.setType(part.getContentType().split("/")[1]);
            System.out.println("***** type:"+ part.getContentType().split("/")[1]);
            educationalObjectMedia.setMedia("http://cdn.guigoh.com/educationalobjects/" + educationalObject.getName() + "/media/" + part.getSubmittedFileName());
            UploadService.uploadFile(part, mediaPath);
            educationalObjectMediaBO.create(educationalObjectMedia); 
        }
        submitted = true;
    }

    

    private void loadInterestThemes() {
        InterestsBO interestsBO = new InterestsBO();
        interestThemesList = interestsBO.findInterestsByInterestsTypeName("Themes");
    }

    public EducationalObject getEducationalObject() {
        return educationalObject;
    }

    public void setEducationalObject(EducationalObject educationalObject) {
        this.educationalObject = educationalObject;
    }

    public List<Interests> getInterestThemesList() {
        return interestThemesList;
    }

    public void setInterestThemesList(List<Interests> interestThemesList) {
        this.interestThemesList = interestThemesList;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public Part getImageFile() {
        return imageFile;
    }

    public void setImageFile(Part imageFile) {
        this.imageFile = imageFile;
    }

    public Part getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(Part mediaFile) {
        this.mediaFile = mediaFile;
    }

    public List<Part> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Part> mediaList) {
        this.mediaList = mediaList;
    }

    public boolean isSubmitted() {
        return submitted;
    }

    public void setSubmitted(boolean submitted) {
        this.submitted = submitted;
    }

}
