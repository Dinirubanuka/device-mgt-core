/*
*  Copyright (c) 2015 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

package io.entgra.device.mgt.core.policy.mgt.core.mgt.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import io.entgra.device.mgt.core.device.mgt.common.DynamicTaskContext;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.Profile;
import io.entgra.device.mgt.core.device.mgt.common.policy.mgt.ProfileFeature;
import io.entgra.device.mgt.core.policy.mgt.common.ProfileManagementException;
import io.entgra.device.mgt.core.policy.mgt.core.dao.FeatureDAO;
import io.entgra.device.mgt.core.policy.mgt.core.dao.FeatureManagerDAOException;
import io.entgra.device.mgt.core.policy.mgt.core.dao.PolicyManagementDAOFactory;
import io.entgra.device.mgt.core.policy.mgt.core.dao.PolicyManagerDAOException;
import io.entgra.device.mgt.core.policy.mgt.core.dao.ProfileDAO;
import io.entgra.device.mgt.core.policy.mgt.core.dao.ProfileManagerDAOException;
import io.entgra.device.mgt.core.policy.mgt.core.mgt.ProfileManager;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProfileManagerImpl implements ProfileManager {

    private static final Log log = LogFactory.getLog(ProfileManagerImpl.class);
    private ProfileDAO profileDAO;
    private FeatureDAO featureDAO;

    public ProfileManagerImpl() {
        profileDAO = PolicyManagementDAOFactory.getProfileDAO();
        featureDAO = PolicyManagementDAOFactory.getFeatureDAO();
    }

    @Override
    public Profile addProfile(Profile profile) throws ProfileManagementException {

        Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        profile.setCreatedDate(currentTimestamp);
        profile.setUpdatedDate(currentTimestamp);

        try {
            PolicyManagementDAOFactory.beginTransaction();
            profile = profileDAO.addProfile(profile);
            featureDAO.addProfileFeatures(profile.getProfileFeaturesList(), profile.getProfileId());
            PolicyManagementDAOFactory.commitTransaction();
        } catch (ProfileManagerDAOException e) {
            PolicyManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while adding the profile (" + profile.getProfileName() + ")";
            log.error(msg, e);
            throw new ProfileManagementException(msg, e);
        } catch (FeatureManagerDAOException e) {
            PolicyManagementDAOFactory.rollbackTransaction();
            String msg = "Error occurred while adding the profile features (" + profile.getProfileName() + ")";
            log.error(msg, e);
            throw new ProfileManagementException(msg, e);
        } catch (PolicyManagerDAOException e) {
            String msg = "Error occurred while adding the profile (" + profile.getProfileName() + ") to the database";
            log.error(msg, e);
            throw new ProfileManagementException(msg, e);
        } finally {
            PolicyManagementDAOFactory.closeConnection();
        }
        return profile;
    }

    @Override
    public Profile updateProfile(Profile profile) throws ProfileManagementException {

        Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());
        profile.setUpdatedDate(currentTimestamp);

        try {
            PolicyManagementDAOFactory.beginTransaction();
            profileDAO.updateProfile(profile);
            featureDAO.updateProfileFeatures(profile.getProfileFeaturesList(), profile.getProfileId());
            PolicyManagementDAOFactory.commitTransaction();
        } catch (ProfileManagerDAOException e) {
            PolicyManagementDAOFactory.rollbackTransaction();
            throw new ProfileManagementException("Error occurred while updating the profile (" +
                    profile.getProfileName() + ")", e);
        } catch (FeatureManagerDAOException e) {
            PolicyManagementDAOFactory.rollbackTransaction();
            throw new ProfileManagementException("Error occurred while updating the profile features (" +
                    profile.getProfileName() + ")", e);
        } catch (PolicyManagerDAOException e) {
            throw new ProfileManagementException("Error occurred while updating the profile (" +
                    profile.getProfileName() + ") to the database", e);
        } finally {
            PolicyManagementDAOFactory.closeConnection();
        }

        return profile;
    }

    @Override
    public boolean deleteProfile(Profile profile) throws ProfileManagementException {
        boolean bool;
        try {
            PolicyManagementDAOFactory.beginTransaction();
            featureDAO.deleteFeaturesOfProfile(profile);
            bool = profileDAO.deleteProfile(profile);
            PolicyManagementDAOFactory.commitTransaction();
        } catch (ProfileManagerDAOException e) {
            PolicyManagementDAOFactory.rollbackTransaction();
            throw new ProfileManagementException("Error occurred while deleting the profile (" +
                    profile.getProfileName() + ")", e);
        } catch (FeatureManagerDAOException e) {
            PolicyManagementDAOFactory.rollbackTransaction();
            throw new ProfileManagementException("Error occurred while deleting the features from profile (" +
                    profile.getProfileName() + ")", e);
        } catch (PolicyManagerDAOException e) {
            throw new ProfileManagementException("Error occurred while deleting the profile (" +
                    profile.getProfileName() + ") from database", e);
        } finally {
            PolicyManagementDAOFactory.closeConnection();
        }
        return bool;
    }

    @Override
    public Profile getProfile(int profileId) throws ProfileManagementException {
        Profile profile;
        List<ProfileFeature> featureList;
        try {
            PolicyManagementDAOFactory.openConnection();
            profile = profileDAO.getProfile(profileId);
            if (profile == null) {
                throw new ProfileManagementException("Profile is not available with profile id (" + profileId + ")");
            }
            featureList = featureDAO.getFeaturesForProfile(profileId);
            profile.setProfileFeaturesList(featureList);
        } catch (ProfileManagerDAOException e) {
            throw new ProfileManagementException("Error occurred while getting profile id (" + profileId + ")", e);
        } catch (FeatureManagerDAOException e) {
            throw new ProfileManagementException("Error occurred while getting features related profile id (" +
                    profileId + ")", e);
        } catch (SQLException e) {
            throw new ProfileManagementException("Error occurred while opening a connection to the data source", e);
        } finally {
            PolicyManagementDAOFactory.closeConnection();
        }
        return profile;
    }

    @Override
    public List<Profile> getAllProfiles() throws ProfileManagementException {
        List<Profile> profileList;
        try {
            PolicyManagementDAOFactory.openConnection();
            profileList = profileDAO.getAllProfiles();
            List<ProfileFeature> featureList = featureDAO.getAllProfileFeatures();

            for (Profile profile : profileList) {

                List<ProfileFeature> list = new ArrayList<>();
                for (ProfileFeature profileFeature : featureList) {
                    if (profile.getProfileId() == profileFeature.getProfileId()) {
                        list.add(profileFeature);
                    }
                }
                profile.setProfileFeaturesList(list);
            }
        } catch (ProfileManagerDAOException e) {
            throw new ProfileManagementException("Error occurred while getting profiles", e);
        } catch (FeatureManagerDAOException e) {
            throw new ProfileManagementException("Error occurred while getting features related to profiles", e);
        } catch (SQLException e) {
            throw new ProfileManagementException("Error occurred while opening a connection to the data source", e);
        } finally {
            PolicyManagementDAOFactory.closeConnection();
        }
        return profileList;
    }

    @Override
    public List<Profile> getProfilesOfDeviceType(String deviceType) throws ProfileManagementException {
        List<Profile> profileList;
        List<ProfileFeature> featureList;
        try {
            PolicyManagementDAOFactory.openConnection();

            profileList = profileDAO.getProfilesOfDeviceType(deviceType);
            featureList = featureDAO.getAllProfileFeatures();

            for (Profile profile : profileList) {
                List<ProfileFeature> profileFeatureList = new ArrayList<>();
                for (ProfileFeature profileFeature : featureList) {
                    if (profile.getProfileId() == profileFeature.getProfileId()) {
                        profileFeatureList.add(profileFeature);
                    }
                }
                profile.setProfileFeaturesList(profileFeatureList);
            }
        } catch (ProfileManagerDAOException e) {
            throw new ProfileManagementException("Error occurred while getting profiles", e);
        } catch (FeatureManagerDAOException e) {
            throw new ProfileManagementException("Error occurred while getting profile features types", e);
        } catch (SQLException e) {
            throw new ProfileManagementException("Error occurred while opening a connection to the data source", e);
        } finally {
            PolicyManagementDAOFactory.closeConnection();

        }
        return profileList;
    }
}
