/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.guigoh.mandril.bo;

import com.guigoh.mandril.dao.EducationalObjectMediaDAO;
import com.guigoh.mandril.entity.EducationalObjectMedia;
import java.util.List;

/**
 *
 * @author ipti008
 */
public class EducationalObjectMediaBO {
    private EducationalObjectMediaDAO educationalObjectMediaDAO;

    public EducationalObjectMediaBO() {
        educationalObjectMediaDAO = new EducationalObjectMediaDAO();
    }
    
    public void create(EducationalObjectMedia educationalObjectMedia){
        try{
            educationalObjectMediaDAO.create(educationalObjectMedia);
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void edit(EducationalObjectMedia educationalObjectMedia){
        try{
            educationalObjectMediaDAO.edit(educationalObjectMedia);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public List<EducationalObjectMedia> getMediasByEducationalObject(Integer educationalObjectID){
        try{
            return educationalObjectMediaDAO.getMediasByEducationalObject(educationalObjectID);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
