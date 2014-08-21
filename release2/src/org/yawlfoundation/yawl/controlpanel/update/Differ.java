package org.yawlfoundation.yawl.controlpanel.update;

import org.yawlfoundation.yawl.controlpanel.util.TomcatUtil;
import org.yawlfoundation.yawl.util.XNode;

import java.io.File;
import java.util.*;

/**
 * @author Michael Adams
 * @date 12/08/2014
 */
public class Differ {

    private ChecksumsReader _latest;
    private ChecksumsReader _current;
    private List<UpdateList> _updates;

    public Differ(File latest, File current) {
        _latest = new ChecksumsReader(latest);
        _current = new ChecksumsReader(current);
    }

    public String getLatestVersion() { return _latest.getVersion(); }

    public String getCurrentVersion() { return _current.getVersion(); }

    public String getLatestTimestamp() { return _latest.getTimestamp(); }

    public String getCurrentTimestamp() { return _current.getTimestamp(); }

    public String getLatestBuild(String appName) {
        return _latest.getBuildNumber(appName);
    }

    public String getCurrentBuild(String appName) {
        return _current.getBuildNumber(appName);
    }


    public boolean isNewVersion() {
        return ! getLatestVersion().equals(getCurrentVersion());
    }


    public boolean hasUpdate(String appName) {
        return isDifferent(getCurrentBuild(appName), getLatestBuild(appName));
    }


    public boolean hasLibChange() {
        return isDifferent(_current.getLibHash(), _latest.getLibHash());
    }


    public boolean hasYawlLibChange() {
        return isDifferent(_current.getYawlLibHash(), _latest.getYawlLibHash());
    }


    public boolean hasUpdates() {
        if (hasLibChange() || hasYawlLibChange()) return true;      // short circuit
        for (String appName : getInstalledWebAppNames()) {
            if (hasUpdate(appName)) return true;
        }
        return false;
    }


    public List<UpdateList> getUpdatesList() throws IllegalStateException {
        if (_updates == null) _updates = diff();
        return _updates;
    }

    public List<String> getWebAppNames() {
        return _latest.getWebAppNames();
    }

    public List<String> getInstalledWebAppNames() {
        List<String> installed = new ArrayList<String>();
        List<String> available = _current.getWebAppNames();
        File webAppsDir = new File(TomcatUtil.getCatalinaHome(), "webapps");
        for (File f : getDirList(webAppsDir)) {
            String name = f.getName();
            if (available.contains(name)) installed.add(name);
        }
        return installed;
    }


    public List<String> getInstalledLibNames() {
        List<String> installed = new ArrayList<String>();
        File libDir = new File(TomcatUtil.getCatalinaHome(), "yawllib");
        for (File f : getFileList(libDir)) {
            installed.add(f.getName());
        }
        return installed;

    }

    public UpdateList getAppFiles(String appName, boolean adding) {
        UpdateList upList = new UpdateList(appName);
        for (XNode node : _latest.getAppFileList(appName)) {
            if (adding) upList.addDownload(node);
            else upList.addDeletion(node);
        }
        return upList;
    }


    // for new installs
    public UpdateList getAppLibs(String appName) {
        List<String> installed = getInstalledLibNames();
        UpdateList upList = new UpdateList(null);
        Map<String, FileNode> libMap  = _latest.getLibMap();
        for (XNode node : _latest.getAppLibList(appName)) {
            String name = node.getAttributeValue("name");
            if (! installed.contains(name)) {
                FileNode fileNode = libMap.get(name);
                if (fileNode != null) upList.addDownload(fileNode);
            }
        }
        return upList;
    }

    public Set<String> getRequiredLibNames() {
        Set<String> libNames = new HashSet<String>();
        for (XNode node : _latest.getRequiredLibs(getInstalledWebAppNames())) {
            libNames.add(node.getAttributeValue("name"));
        }
        return libNames;
    }


    private List<UpdateList> diff() throws IllegalStateException {
        if (_latest == null || _current == null) return Collections.emptyList();
        List<UpdateList> updates = new ArrayList<UpdateList>();
        compareYAWLLib(updates);
        compareWebApps(updates);
        return updates;
    }


    private void compareYAWLLib(List<UpdateList> updates) throws IllegalStateException {
        List<XNode> currentYawl = new ArrayList<XNode>();
        currentYawl.add(_current.getYawlLibNode());
        List<XNode> latestYawl = new ArrayList<XNode>();
        latestYawl.add(_latest.getYawlLibNode());
        UpdateList updateList = compareFileLists(latestYawl, currentYawl, null);
        if (! updateList.isEmpty()) updates.add(updateList);
    }


    private void compareWebApps(List<UpdateList> updates) throws IllegalStateException {
        List<String> installedWebAppNames = getInstalledWebAppNames();
        for (String appName : installedWebAppNames) {
            if (hasUpdate(appName)) {
                UpdateList updateList = compareFileLists(
                        _latest.getAppFileList(appName),
                        _current.getAppFileList(appName), appName);
                if (! updateList.isEmpty()) updates.add(updateList);
            }
        }
        if (hasLibChange()) {
            UpdateList updateList = compareFileLists(
                    _latest.getRequiredLibs(installedWebAppNames),
                    _current.getRequiredLibs(installedWebAppNames), null);
            if (! updateList.isEmpty()) updates.add(updateList);
        }
    }


    private UpdateList compareFileLists(List<XNode> latestList, List<XNode> currentList,
                                  String appName) throws IllegalStateException {
        checkNotNullOrEmpty(latestList);
        checkNotNullOrEmpty(currentList);
        Map<String, FileNode> latestMap = getFileMap(latestList);
        Map<String, FileNode> currentMap = getFileMap(currentList);
        return compareMaps(latestMap, currentMap, appName);
    }


    private Map<String, FileNode> getFileMap(List<XNode> fileList) {
        Map<String, FileNode> fileMap = new HashMap<String, FileNode>();
        for (XNode child : fileList) {
            FileNode fileNode = new FileNode(child);
            fileMap.put(fileNode.getName(), fileNode);
        }
        return fileMap;
    }


    private UpdateList compareMaps(Map<String, FileNode> latestMap,
                             Map<String, FileNode> currentMap,
                             String appName) {

        UpdateList updateList = new UpdateList(appName);
        for (String fileName : latestMap.keySet()) {
            FileNode node = latestMap.get(fileName);

            // if latest in current, compare md5's - if same, don't add download list
            if (currentMap.containsKey(fileName) && node.matches(currentMap.get(fileName))) {
                continue;
            }

            // latest entry is not in current, or different md5's, so add to download list
            updateList.addDownload(node);
        }

        // if current entry not in latest, add to delete list
        for (String fileName : currentMap.keySet()) {
            if (! latestMap.containsKey(fileName)) {
                updateList.addDeletion(currentMap.get(fileName));
            }
        }
        return updateList;
    }


    private List<File> getDirList(File f) {
        List<File> dirList = new ArrayList<File>();
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) dirList.add(file);
            }
        }
        return dirList;
    }


    // non-recursive search
    private List<File> getFileList(File f) {
        List<File> fileList = new ArrayList<File>();
        File[] files = f.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) fileList.add(file);
            }
        }
        return fileList;
    }


    private boolean isDifferent(String one, String two) {
        return ! (one == null || two == null || one.equals(two));
    }


    private void checkNotNullOrEmpty(List<?> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalStateException("Unable to compare versions.");
        }
    }

}