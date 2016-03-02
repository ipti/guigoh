/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.org.ipti.guigoh.model.jpa.controller;

import br.org.ipti.guigoh.model.jpa.util.PersistenceUnit;
import br.org.ipti.guigoh.model.jpa.controller.exceptions.IllegalOrphanException;
import br.org.ipti.guigoh.model.jpa.controller.exceptions.NonexistentEntityException;
import br.org.ipti.guigoh.model.jpa.controller.exceptions.PreexistingEntityException;
import br.org.ipti.guigoh.model.jpa.controller.exceptions.RollbackFailureException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import br.org.ipti.guigoh.model.entity.SocialProfile;
import br.org.ipti.guigoh.model.entity.UserAuthorization;
import br.org.ipti.guigoh.model.entity.SecretQuestion;
import br.org.ipti.guigoh.model.entity.UserContactInfo;
import br.org.ipti.guigoh.model.entity.Users;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.UserTransaction;

/**
 *
 * @author Joe
 */
public class UsersJpaController implements Serializable {

    private transient EntityManagerFactory emf = PersistenceUnit.getEMF();
    
    public UsersJpaController() {
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Users users) throws PreexistingEntityException, RollbackFailureException, Exception {
        if (users.getUserContactInfoCollection() == null) {
            users.setUserContactInfoCollection(new ArrayList<UserContactInfo>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            SocialProfile socialProfile = users.getSocialProfile();
            if (socialProfile != null) {
                socialProfile = em.getReference(socialProfile.getClass(), socialProfile.getTokenId());
                users.setSocialProfile(socialProfile);
            }
            UserAuthorization userAuthorization = users.getUserAuthorization();
            if (userAuthorization != null) {
                userAuthorization = em.getReference(userAuthorization.getClass(), userAuthorization.getTokenId());
                users.setUserAuthorization(userAuthorization);
            }
            SecretQuestion secretQuestionId = users.getSecretQuestionId();
            if (secretQuestionId != null) {
                secretQuestionId = em.getReference(secretQuestionId.getClass(), secretQuestionId.getId());
                users.setSecretQuestionId(secretQuestionId);
            }
            Collection<UserContactInfo> attachedUserContactInfoCollection = new ArrayList<UserContactInfo>();
            for (UserContactInfo userContactInfoCollectionUserContactInfoToAttach : users.getUserContactInfoCollection()) {
                userContactInfoCollectionUserContactInfoToAttach = em.getReference(userContactInfoCollectionUserContactInfoToAttach.getClass(), userContactInfoCollectionUserContactInfoToAttach.getUserContactInfoPK());
                attachedUserContactInfoCollection.add(userContactInfoCollectionUserContactInfoToAttach);
            }
            users.setUserContactInfoCollection(attachedUserContactInfoCollection);
            em.persist(users);
            if (socialProfile != null) {
                Users oldUsersOfSocialProfile = socialProfile.getUsers();
                if (oldUsersOfSocialProfile != null) {
                    oldUsersOfSocialProfile.setSocialProfile(null);
                    oldUsersOfSocialProfile = em.merge(oldUsersOfSocialProfile);
                }
                socialProfile.setUsers(users);
                socialProfile = em.merge(socialProfile);
            }
            if (userAuthorization != null) {
                Users oldUsersOfUserAuthorization = userAuthorization.getUsers();
                if (oldUsersOfUserAuthorization != null) {
                    oldUsersOfUserAuthorization.setUserAuthorization(null);
                    oldUsersOfUserAuthorization = em.merge(oldUsersOfUserAuthorization);
                }
                userAuthorization.setUsers(users);
                userAuthorization = em.merge(userAuthorization);
            }
            if (secretQuestionId != null) {
                secretQuestionId.getUsersCollection().add(users);
                secretQuestionId = em.merge(secretQuestionId);
            }
            for (UserContactInfo userContactInfoCollectionUserContactInfo : users.getUserContactInfoCollection()) {
                Users oldUsersOfUserContactInfoCollectionUserContactInfo = userContactInfoCollectionUserContactInfo.getUsers();
                userContactInfoCollectionUserContactInfo.setUsers(users);
                userContactInfoCollectionUserContactInfo = em.merge(userContactInfoCollectionUserContactInfo);
                if (oldUsersOfUserContactInfoCollectionUserContactInfo != null) {
                    oldUsersOfUserContactInfoCollectionUserContactInfo.getUserContactInfoCollection().remove(userContactInfoCollectionUserContactInfo);
                    oldUsersOfUserContactInfoCollectionUserContactInfo = em.merge(oldUsersOfUserContactInfoCollectionUserContactInfo);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            if (findUsers(users.getUsername()) != null) {
                throw new PreexistingEntityException("Users " + users + " already exists.", ex);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Users users) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users persistentUsers = em.find(Users.class, users.getUsername());
            SocialProfile socialProfileOld = persistentUsers.getSocialProfile();
            SocialProfile socialProfileNew = users.getSocialProfile();
            UserAuthorization userAuthorizationOld = persistentUsers.getUserAuthorization();
            UserAuthorization userAuthorizationNew = users.getUserAuthorization();
            SecretQuestion secretQuestionIdOld = persistentUsers.getSecretQuestionId();
            SecretQuestion secretQuestionIdNew = users.getSecretQuestionId();
            Collection<UserContactInfo> userContactInfoCollectionOld = persistentUsers.getUserContactInfoCollection();
            Collection<UserContactInfo> userContactInfoCollectionNew = users.getUserContactInfoCollection();
            List<String> illegalOrphanMessages = null;
            if (socialProfileOld != null && !socialProfileOld.equals(socialProfileNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain SocialProfile " + socialProfileOld + " since its users field is not nullable.");
            }
            if (userAuthorizationOld != null && !userAuthorizationOld.equals(userAuthorizationNew)) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("You must retain Authorization " + userAuthorizationOld + " since its users field is not nullable.");
            }
            for (UserContactInfo userContactInfoCollectionOldUserContactInfo : userContactInfoCollectionOld) {
                if (!userContactInfoCollectionNew.contains(userContactInfoCollectionOldUserContactInfo)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain UserContactInfo " + userContactInfoCollectionOldUserContactInfo + " since its users field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (socialProfileNew != null) {
                socialProfileNew = em.getReference(socialProfileNew.getClass(), socialProfileNew.getTokenId());
                users.setSocialProfile(socialProfileNew);
            }
            if (userAuthorizationNew != null) {
                userAuthorizationNew = em.getReference(userAuthorizationNew.getClass(), userAuthorizationNew.getTokenId());
                users.setUserAuthorization(userAuthorizationNew);
            }
            if (secretQuestionIdNew != null) {
                secretQuestionIdNew = em.getReference(secretQuestionIdNew.getClass(), secretQuestionIdNew.getId());
                users.setSecretQuestionId(secretQuestionIdNew);
            }
            Collection<UserContactInfo> attachedUserContactInfoCollectionNew = new ArrayList<UserContactInfo>();
            for (UserContactInfo userContactInfoCollectionNewUserContactInfoToAttach : userContactInfoCollectionNew) {
                userContactInfoCollectionNewUserContactInfoToAttach = em.getReference(userContactInfoCollectionNewUserContactInfoToAttach.getClass(), userContactInfoCollectionNewUserContactInfoToAttach.getUserContactInfoPK());
                attachedUserContactInfoCollectionNew.add(userContactInfoCollectionNewUserContactInfoToAttach);
            }
            userContactInfoCollectionNew = attachedUserContactInfoCollectionNew;
            users.setUserContactInfoCollection(userContactInfoCollectionNew);
            users = em.merge(users);
            if (socialProfileNew != null && !socialProfileNew.equals(socialProfileOld)) {
                Users oldUsersOfSocialProfile = socialProfileNew.getUsers();
                if (oldUsersOfSocialProfile != null) {
                    oldUsersOfSocialProfile.setSocialProfile(null);
                    oldUsersOfSocialProfile = em.merge(oldUsersOfSocialProfile);
                }
                socialProfileNew.setUsers(users);
                socialProfileNew = em.merge(socialProfileNew);
            }
            if (userAuthorizationNew != null && !userAuthorizationNew.equals(userAuthorizationOld)) {
                Users oldUsersOfUserAuthorization = userAuthorizationNew.getUsers();
                if (oldUsersOfUserAuthorization != null) {
                    oldUsersOfUserAuthorization.setUserAuthorization(null);
                    oldUsersOfUserAuthorization = em.merge(oldUsersOfUserAuthorization);
                }
                userAuthorizationNew.setUsers(users);
                userAuthorizationNew = em.merge(userAuthorizationNew);
            }
            if (secretQuestionIdOld != null && !secretQuestionIdOld.equals(secretQuestionIdNew)) {
                secretQuestionIdOld.getUsersCollection().remove(users);
                secretQuestionIdOld = em.merge(secretQuestionIdOld);
            }
            if (secretQuestionIdNew != null && !secretQuestionIdNew.equals(secretQuestionIdOld)) {
                secretQuestionIdNew.getUsersCollection().add(users);
                secretQuestionIdNew = em.merge(secretQuestionIdNew);
            }
            for (UserContactInfo userContactInfoCollectionNewUserContactInfo : userContactInfoCollectionNew) {
                if (!userContactInfoCollectionOld.contains(userContactInfoCollectionNewUserContactInfo)) {
                    Users oldUsersOfUserContactInfoCollectionNewUserContactInfo = userContactInfoCollectionNewUserContactInfo.getUsers();
                    userContactInfoCollectionNewUserContactInfo.setUsers(users);
                    userContactInfoCollectionNewUserContactInfo = em.merge(userContactInfoCollectionNewUserContactInfo);
                    if (oldUsersOfUserContactInfoCollectionNewUserContactInfo != null && !oldUsersOfUserContactInfoCollectionNewUserContactInfo.equals(users)) {
                        oldUsersOfUserContactInfoCollectionNewUserContactInfo.getUserContactInfoCollection().remove(userContactInfoCollectionNewUserContactInfo);
                        oldUsersOfUserContactInfoCollectionNewUserContactInfo = em.merge(oldUsersOfUserContactInfoCollectionNewUserContactInfo);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                String id = users.getUsername();
                if (findUsers(id) == null) {
                    throw new NonexistentEntityException("The users with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(String id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Users users;
            try {
                users = em.getReference(Users.class, id);
                users.getUsername();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The users with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            SocialProfile socialProfileOrphanCheck = users.getSocialProfile();
            if (socialProfileOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the SocialProfile " + socialProfileOrphanCheck + " in its socialProfile field has a non-nullable users field.");
            }
            UserAuthorization userAuthorizationOrphanCheck = users.getUserAuthorization();
            if (userAuthorizationOrphanCheck != null) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the Authorization " + userAuthorizationOrphanCheck + " in its authorization field has a non-nullable users field.");
            }
            Collection<UserContactInfo> userContactInfoCollectionOrphanCheck = users.getUserContactInfoCollection();
            for (UserContactInfo userContactInfoCollectionOrphanCheckUserContactInfo : userContactInfoCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Users (" + users + ") cannot be destroyed since the UserContactInfo " + userContactInfoCollectionOrphanCheckUserContactInfo + " in its userContactInfoCollection field has a non-nullable users field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            SecretQuestion secretQuestionId = users.getSecretQuestionId();
            if (secretQuestionId != null) {
                secretQuestionId.getUsersCollection().remove(users);
                secretQuestionId = em.merge(secretQuestionId);
            }
            em.remove(users);
            em.getTransaction().commit();
        } catch (Exception ex) {
            try {
                em.getTransaction().rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Users> findUsersEntities() {
        return findUsersEntities(true, -1, -1);
    }

    public List<Users> findUsersEntities(int maxResults, int firstResult) {
        return findUsersEntities(false, maxResults, firstResult);
    }

    private List<Users> findUsersEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Users.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Users findUsers(String id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Users.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsersCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Users> rt = cq.from(Users.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
    public int getUsersQuantity() {
        EntityManager em = getEntityManager();
        try {
            Long usersQuantity = (Long) em.createNativeQuery("select count(*) from users u "
                    + "join user_authorization ua on u.token = ua.token_id "
                    + "where u.status = 'CA' and ua.status = 'AC'").getSingleResult();
            return usersQuantity.intValue();
        } finally {
            em.close();
        }
    }
}
