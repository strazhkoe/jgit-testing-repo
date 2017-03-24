package com.ystrazhko.git.jgit;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.ConcurrentRefUpdateException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.NoMessageException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.api.errors.UnmergedPathsException;
import org.eclipse.jgit.api.errors.WrongRepositoryStateException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Repository;

import com.google.gson.reflect.TypeToken;
import com.ystrazhko.git.entities.Group;
import com.ystrazhko.git.entities.Project;
import com.ystrazhko.git.services.ProjectService;
import com.ystrazhko.git.services.ServiceProvider;
import com.ystrazhko.git.util.JSONParser;

/**
 * Class for work with Git:
 *
 * [+] clone a group, project or URL of repository;
 * - create repository;
 * - commit;
 * - push.
 *
 * @author Lyska Lyudmila
 */
public class JGit {
    private static final JGit _jgit;

    static {
        _jgit = new JGit();
    }

    /**
     * Gets instance's the class
     *
     * @return instance
     */
    public static JGit getInstance() {
        return _jgit;
    }

    /**
     * Clones all projects from the group
     *
     * @param group for clone
     * @param localPath the path to where will clone all the projects of the group
     */
    public void clone(Group group, String localPath) {
        if (group == null || localPath == null) {
            return;
        }
        for (Project project : getProjects(group)) {
            clone(project, localPath);
        }
    }

    /**
     * Clones the project
     *
     * @param project for clone
     * @param localPath the path to where will clone the project
     */
    public void clone(Project project, String localPath) {
        if (project == null || localPath == null) {
            return;
        }
        clone(project.getHttp_url_to_repo(), localPath + "/" + project.getName());
    }

    /**
     * Clones the URL
     *
     * @param linkClone for clone
     * @param localPath the path to where will clone the project
     */
    public void clone(String linkClone, String localPath) {
        if (linkClone == null || localPath == null) {
            return;
        }
        try {
            Git.cloneRepository().setURI(linkClone).setDirectory(new File(localPath)).call();
        } catch (InvalidRemoteException | TransportException e) {
            System.err.println("!ERROR: " + e.getMessage());
        } catch (GitAPIException e) {
            System.err.println("!ERROR: " + e.getMessage());
        }
    }


    //debug code
    private Collection<Project> getProjects(Group group) {
        Object jsonProjects = ((ProjectService) ServiceProvider.getInstance()
                     .getService(ProjectService.class.getName())).getProjects(String.valueOf(group.getId()));
        return JSONParser.parseToCollectionObjects(jsonProjects,new TypeToken<List<Project>>() {}.getType());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void commit(String groupFolderPath, String message) {
        try {
            Repository rep = new FileRepository(groupFolderPath + "/.git");
            Git git = new Git(rep);
            git.commit().setAll(true).setMessage(message).call();
            git.push().call();






        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoHeadException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoMessageException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnmergedPathsException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ConcurrentRefUpdateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (WrongRepositoryStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (GitAPIException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void commitAndPush() {

    }




//    create new file
//    File myfile = new File(rep.getDirectory().getParent(), "secondfile");
//    myfile.createNewFile();
//    git.add().addFilepattern(".").call();


}
