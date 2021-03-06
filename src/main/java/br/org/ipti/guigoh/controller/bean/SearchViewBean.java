/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.ipti.guigoh.controller.bean;

import br.org.ipti.guigoh.model.entity.DiscussionTopic;
import br.org.ipti.guigoh.model.entity.EducationalObject;
import br.org.ipti.guigoh.model.entity.Friends;
import br.org.ipti.guigoh.model.entity.SocialProfile;
import br.org.ipti.guigoh.model.jpa.controller.DiscussionTopicJpaController;
import br.org.ipti.guigoh.model.jpa.controller.EducationalObjectJpaController;
import br.org.ipti.guigoh.model.jpa.controller.FriendsJpaController;
import br.org.ipti.guigoh.model.jpa.controller.SocialProfileJpaController;
import br.org.ipti.guigoh.model.jpa.controller.exceptions.RollbackFailureException;
import br.org.ipti.guigoh.util.CookieService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *
 * @author iptipc015
 */
@ViewScoped
@Named
public class SearchViewBean implements Serializable {

    private String generalSearch;
    private Integer userLimit, objectLimit, topicLimit;
    
    private SocialProfile mySocialProfile;

    private List<SocialProfile> socialProfileList;
    private List<EducationalObject> educationalObjectList;
    private List<DiscussionTopic> discussionTopicList;

    private SocialProfileJpaController socialProfileJpaController;
    private EducationalObjectJpaController educationalObjectJpaController;
    private DiscussionTopicJpaController discussionTopicJpaController;
    private FriendsJpaController friendsJpaController;

    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            initGlobalVariables();
            mySocialProfile = socialProfileJpaController.findSocialProfile(CookieService.getCookie("token"));
        }
    }

    public void renderSearchResult() {
        if (generalSearch.length() >= 3) {
            userLimit = objectLimit = topicLimit = 3;
            socialProfileList = socialProfileJpaController.findSocialProfiles(generalSearch, mySocialProfile.getTokenId(), false, false, null);
            educationalObjectList = educationalObjectJpaController.findEducationalObjects(generalSearch, null, null, null, "AC");
            discussionTopicList = discussionTopicJpaController.findDiscussionTopics(generalSearch, null, null, null);
        } else {
            socialProfileList.clear();
            educationalObjectList.clear();
            discussionTopicList.clear();
        }
    }

    public Friends isFriend(String friendTokenId) {
        return friendsJpaController.isFriend(friendTokenId, CookieService.getCookie("token"));
    }
    
    public void addFriend(Integer id) throws RollbackFailureException, Exception{
        friendsJpaController.addFriend(mySocialProfile.getUsers(), id);
    }
    
    public void removeFriend(Integer id) throws RollbackFailureException, Exception{
        friendsJpaController.removeFriend(mySocialProfile.getUsers(), id);
    }

    public void increaseLimit(String type) {
        switch (type) {
            case "EO":
                objectLimit += 6;
                break;
            case "SP":
                userLimit += 6;
                break;
            case "DT":
                topicLimit += 6;
                break;
            
        }
    }

    private void initGlobalVariables() {
        generalSearch = "";
        userLimit = objectLimit = topicLimit = 3;
        
        socialProfileList = new ArrayList<>();
        educationalObjectList = new ArrayList<>();
        discussionTopicList = new ArrayList<>();

        socialProfileJpaController = new SocialProfileJpaController();
        friendsJpaController = new FriendsJpaController();
        educationalObjectJpaController = new EducationalObjectJpaController();
        discussionTopicJpaController = new DiscussionTopicJpaController();
    }

    public String getGeneralSearch() {
        return generalSearch;
    }

    public void setGeneralSearch(String generalSearch) {
        this.generalSearch = generalSearch;
    }

    public List<SocialProfile> getSocialProfileList() {
        return socialProfileList;
    }

    public void setSocialProfileList(List<SocialProfile> socialProfileList) {
        this.socialProfileList = socialProfileList;
    }

    public List<EducationalObject> getEducationalObjectList() {
        return educationalObjectList;
    }

    public void setEducationalObjectList(List<EducationalObject> educationalObjectList) {
        this.educationalObjectList = educationalObjectList;
    }

    public Integer getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(Integer userLimit) {
        this.userLimit = userLimit;
    }

    public Integer getObjectLimit() {
        return objectLimit;
    }

    public void setObjectLimit(Integer objectLimit) {
        this.objectLimit = objectLimit;
    }

    public Integer getTopicLimit() {
        return topicLimit;
    }

    public void setTopicLimit(Integer topicLimit) {
        this.topicLimit = topicLimit;
    }

    public List<DiscussionTopic> getDiscussionTopicList() {
        return discussionTopicList;
    }

    public void setDiscussionTopicList(List<DiscussionTopic> discussionTopicList) {
        this.discussionTopicList = discussionTopicList;
    }

    public SocialProfile getMySocialProfile() {
        return mySocialProfile;
    }

    public void setMySocialProfile(SocialProfile mySocialProfile) {
        this.mySocialProfile = mySocialProfile;
    }
}
