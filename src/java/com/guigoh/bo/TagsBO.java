/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guigoh.bo;

import com.guigoh.dao.TagsDAO;
import com.guigoh.entity.DiscussionTopic;
import com.guigoh.entity.Tags;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joe
 */
public class TagsBO implements Serializable {

    public static void create(Tags tags) {
        try {
            TagsDAO tagsDAO = new TagsDAO();
            tagsDAO.create(tags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createTagsDiscussionTopic(Tags tags, DiscussionTopic discussionTopic) {
        try {
            TagsDAO tagsDAO = new TagsDAO();
            tagsDAO.createTagsDiscussionTopic(tags, discussionTopic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Tags findTagsByName(String tags) {
        try {
            TagsDAO tagsDAO = new TagsDAO();
            Tags tagsT = tagsDAO.findTagsByName(tags);
            if (tagsT == null) {
                return new Tags();
            }
            return tagsT;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Tags();
    }

    public static List<Tags> findTagsByText(String tags) {
        List<Tags> tagList = new ArrayList<Tags>();
        try {
            TagsDAO tagsDAO = new TagsDAO();
            tagList = tagsDAO.findTagsByText(tags);
            if (tagList == null) {
                return new ArrayList<Tags>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagList;
    }

    public static List<Tags> getAllTags() {
        List<Tags> tagList = new ArrayList<Tags>();
        try {
            TagsDAO tagsDAO = new TagsDAO();
            tagList = tagsDAO.findTagsEntities();
            if (tagList == null) {
                return new ArrayList<Tags>();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tagList;
    }
}
