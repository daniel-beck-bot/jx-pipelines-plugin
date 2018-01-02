/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jenkinsci.plugins.jx.pipelines.arguments;

import io.fabric8.utils.Strings;
import io.jenkins.functions.Argument;
import org.jenkinsci.plugins.jx.pipelines.StepExtension;
import org.jenkinsci.plugins.jx.pipelines.helpers.ConfigHelper;
import org.jenkinsci.plugins.jx.pipelines.helpers.GitRepositoryInfo;
import org.jenkinsci.plugins.jx.pipelines.model.ServiceConstants;
import org.jenkinsci.plugins.jx.pipelines.model.StagedProjectInfo;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class MavenFlowArguments implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotEmpty
    @Argument
    private String gitCloneUrl = "";
    @NotEmpty
    @Argument
    private String cdOrganisation = "";
    @Argument
    private List<String> cdBranches = new ArrayList<>();
    @Argument
    private boolean disableGitPush = false;

    @Argument
    private boolean pauseOnFailure = false;
    @Argument
    private boolean pauseOnSuccess = false;

    @Argument
    private boolean useMavenForNextVersion = false;
    @Argument
    private String extraSetVersionArgs = "";
    @Argument
    private List<String> extraImagesToStage = new ArrayList<>();
    @Argument
    private String containerName = "maven";
    @Argument
    private String clientsContainerName = "clients";
    @Argument
    private boolean useStaging;
    @Argument
    private String stageRepositoryUrl = "";
    @Argument
    private String stageServerId = "";
    @Argument
    private boolean skipTests;
    @Argument
    private boolean useSonatype;

    @Argument
    private boolean updateNextDevelopmentVersion;

    @Argument
    private String dockerOrganisation = "";
    @Argument
    private String promoteToDockerRegistry = "";
    @Argument
    private List<String> promoteDockerImages = new ArrayList<>();
    @Argument
    private List<String> extraImagesToTag = new ArrayList<>();
    @Argument
    private String repositoryToWaitFor;
    @Argument
    private String groupId = "";
    @Argument
    private String artifactExtensionToWaitFor = "";
    @Argument
    private String artifactIdToWaitFor = "";
    @Argument
    private List<String> mavenProfiles = new ArrayList<>();

    private StepExtension promoteArtifactsExtension = new StepExtension();
    private StepExtension promoteImagesExtension = new StepExtension();
    private StepExtension tagImagesExtension = new StepExtension();
    private StepExtension waitUntilPullRequestMergedExtension = new StepExtension();
    private StepExtension waitUntilArtifactSyncedExtension = new StepExtension();


    public static MavenFlowArguments newInstance(Map map) {
        MavenFlowArguments arguments = new MavenFlowArguments();
        ConfigHelper.populateBeanFromConfiguration(arguments, map);
        return arguments;
    }

    public String getGitCloneUrl() {
        return gitCloneUrl;
    }

    public void setGitCloneUrl(String gitCloneUrl) {
        this.gitCloneUrl = gitCloneUrl;
    }

    public StageProjectArguments createStageProjectArguments(GitRepositoryInfo repositoryInfo) {
        StageProjectArguments answer = new StageProjectArguments(repositoryInfo.getProject());
        if (useSonatype) {
            if (Strings.isNullOrBlank(stageRepositoryUrl)) {
                stageRepositoryUrl = ServiceConstants.SONATYPE_REPOSITORY_URL;
            }
            if (Strings.isNullOrBlank(stageServerId)) {
                stageServerId = useStaging ? ServiceConstants.SONATYPE_STAGING_SERVER_ID : ServiceConstants.SONATYPE_SERVER_ID;
            }
        }
        if (Strings.isNullOrBlank(repositoryToWaitFor)) {
            if (useSonatype) {
                repositoryToWaitFor = ServiceConstants.MAVEN_CENTRAL;
            } else {
                repositoryToWaitFor = ServiceConstants.ARTIFACT_REPOSITORY_RELEASE_URL;
            }
        }

        answer.setUseMavenForNextVersion(useMavenForNextVersion);
        answer.setExtraSetVersionArgs(extraSetVersionArgs);
        answer.setExtraImagesToStage(extraImagesToStage);
        answer.setUseStaging(useStaging);
        answer.setStageRepositoryUrl(stageRepositoryUrl);
        answer.setStageServerId(stageServerId);
        answer.setSkipTests(skipTests);
        answer.setDisableGitPush(disableGitPush);
        answer.setMavenProfiles(mavenProfiles);
        if (Strings.notEmpty(containerName)) {
            answer.setContainerName(containerName);
        }
        if (Strings.notEmpty(clientsContainerName)) {
            answer.setClientsContainerName(clientsContainerName);
        }
        return answer;
    }

    public ReleaseProjectArguments createReleaseProjectArguments(StagedProjectInfo stagedProject) {
        ReleaseProjectArguments answer = new ReleaseProjectArguments(stagedProject);
        if (Strings.notEmpty(containerName)) {
            answer.setContainerName(containerName);
        }
        answer.setReleaseVersion(stagedProject.getReleaseVersion());
        answer.setProject(stagedProject.getProject());
        answer.setRepoIds(stagedProject.getRepoIds());

        answer.setArtifactExtensionToWaitFor(getArtifactExtensionToWaitFor());
        answer.setArtifactIdToWaitFor(getArtifactIdToWaitFor());
        answer.setDockerOrganisation(getDockerOrganisation());
        answer.setExtraImagesToTag(getExtraImagesToTag());
        answer.setGroupId(getGroupId());
        answer.setPromoteDockerImages(getPromoteDockerImages());
        answer.setPromoteToDockerRegistry(getPromoteToDockerRegistry());
        answer.setRepositoryToWaitFor(getRepositoryToWaitFor());
        answer.setUpdateNextDevelopmentVersion(isUpdateNextDevelopmentVersion());

        answer.setPromoteArtifactsExtension(getPromoteArtifactsExtension());
        answer.setPromoteImagesExtension(getPromoteImagesExtension());
        answer.setTagImagesExtension(getTagImagesExtension());
        answer.setWaitUntilArtifactSyncedExtension(getWaitUntilArtifactSyncedExtension());
        answer.setWaitUntilPullRequestMergedExtension(getWaitUntilPullRequestMergedExtension());
        return answer;
    }

    public String getExtraSetVersionArgs() {
        return extraSetVersionArgs;
    }

    public void setExtraSetVersionArgs(String extraSetVersionArgs) {
        this.extraSetVersionArgs = extraSetVersionArgs;
    }

    public List<String> getExtraImagesToStage() {
        return extraImagesToStage;
    }

    public void setExtraImagesToStage(List<String> extraImagesToStage) {
        this.extraImagesToStage = extraImagesToStage;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getDockerOrganisation() {
        return dockerOrganisation;
    }

    public void setDockerOrganisation(String dockerOrganisation) {
        this.dockerOrganisation = dockerOrganisation;
    }

    public String getPromoteToDockerRegistry() {
        return promoteToDockerRegistry;
    }

    public void setPromoteToDockerRegistry(String promoteToDockerRegistry) {
        this.promoteToDockerRegistry = promoteToDockerRegistry;
    }

    public List<String> getPromoteDockerImages() {
        return promoteDockerImages;
    }

    public void setPromoteDockerImages(List<String> promoteDockerImages) {
        this.promoteDockerImages = promoteDockerImages;
    }

    public List<String> getExtraImagesToTag() {
        return extraImagesToTag;
    }

    public void setExtraImagesToTag(List<String> extraImagesToTag) {
        this.extraImagesToTag = extraImagesToTag;
    }

    public String getRepositoryToWaitFor() {
        return repositoryToWaitFor;
    }

    public void setRepositoryToWaitFor(String repositoryToWaitFor) {
        this.repositoryToWaitFor = repositoryToWaitFor;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactExtensionToWaitFor() {
        return artifactExtensionToWaitFor;
    }

    public void setArtifactExtensionToWaitFor(String artifactExtensionToWaitFor) {
        this.artifactExtensionToWaitFor = artifactExtensionToWaitFor;
    }

    public String getArtifactIdToWaitFor() {
        return artifactIdToWaitFor;
    }

    public void setArtifactIdToWaitFor(String artifactIdToWaitFor) {
        this.artifactIdToWaitFor = artifactIdToWaitFor;
    }

    public boolean isPauseOnFailure() {
        return pauseOnFailure;
    }

    public void setPauseOnFailure(boolean pauseOnFailure) {
        this.pauseOnFailure = pauseOnFailure;
    }

    public boolean isPauseOnSuccess() {
        return pauseOnSuccess;
    }

    public void setPauseOnSuccess(boolean pauseOnSuccess) {
        this.pauseOnSuccess = pauseOnSuccess;
    }

    public String getCdOrganisation() {
        return cdOrganisation;
    }

    public void setCdOrganisation(String cdOrganisation) {
        this.cdOrganisation = cdOrganisation;
    }

    public List<String> getCdBranches() {
        return cdBranches;
    }

    public void setCdBranches(List<String> cdBranches) {
        this.cdBranches = cdBranches;
    }

    public boolean isUseMavenForNextVersion() {
        return useMavenForNextVersion;
    }

    public void setUseMavenForNextVersion(boolean useMavenForNextVersion) {
        this.useMavenForNextVersion = useMavenForNextVersion;
    }

    public String getClientsContainerName() {
        return clientsContainerName;
    }

    public void setClientsContainerName(String clientsContainerName) {
        this.clientsContainerName = clientsContainerName;
    }

    public boolean isUseStaging() {
        return useStaging;
    }

    public void setUseStaging(boolean useStaging) {
        this.useStaging = useStaging;
    }

    public String getStageRepositoryUrl() {
        return stageRepositoryUrl;
    }

    public void setStageRepositoryUrl(String stageRepositoryUrl) {
        this.stageRepositoryUrl = stageRepositoryUrl;
    }

    public String getStageServerId() {
        return stageServerId;
    }

    public void setStageServerId(String stageServerId) {
        this.stageServerId = stageServerId;
    }

    public boolean isSkipTests() {
        return skipTests;
    }

    public void setSkipTests(boolean skipTests) {
        this.skipTests = skipTests;
    }

    public boolean isUseSonatype() {
        return useSonatype;
    }

    public void setUseSonatype(boolean useSonatype) {
        this.useSonatype = useSonatype;
    }

    public boolean isUpdateNextDevelopmentVersion() {
        return updateNextDevelopmentVersion;
    }

    public void setUpdateNextDevelopmentVersion(boolean updateNextDevelopmentVersion) {
        this.updateNextDevelopmentVersion = updateNextDevelopmentVersion;
    }

    public boolean isDisableGitPush() {
        return disableGitPush;
    }

    public void setDisableGitPush(boolean disableGitPush) {
        this.disableGitPush = disableGitPush;
    }

    public List<String> getMavenProfiles() {
        return mavenProfiles;
    }

    public void setMavenProfiles(List<String> mavenProfiles) {
        this.mavenProfiles = mavenProfiles;
    }

    public StepExtension getPromoteArtifactsExtension() {
        return promoteArtifactsExtension;
    }

    public void setPromoteArtifactsExtension(StepExtension promoteArtifactsExtension) {
        this.promoteArtifactsExtension = promoteArtifactsExtension;
    }

    public StepExtension getPromoteImagesExtension() {
        return promoteImagesExtension;
    }

    public void setPromoteImagesExtension(StepExtension promoteImagesExtension) {
        this.promoteImagesExtension = promoteImagesExtension;
    }

    public StepExtension getTagImagesExtension() {
        return tagImagesExtension;
    }

    public void setTagImagesExtension(StepExtension tagImagesExtension) {
        this.tagImagesExtension = tagImagesExtension;
    }

    public StepExtension getWaitUntilPullRequestMergedExtension() {
        return waitUntilPullRequestMergedExtension;
    }

    public void setWaitUntilPullRequestMergedExtension(StepExtension waitUntilPullRequestMergedExtension) {
        this.waitUntilPullRequestMergedExtension = waitUntilPullRequestMergedExtension;
    }

    public StepExtension getWaitUntilArtifactSyncedExtension() {
        return waitUntilArtifactSyncedExtension;
    }

    public void setWaitUntilArtifactSyncedExtension(StepExtension waitUntilArtifactSyncedExtension) {
        this.waitUntilArtifactSyncedExtension = waitUntilArtifactSyncedExtension;
    }
}