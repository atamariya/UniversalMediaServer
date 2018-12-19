package net.pms.configuration;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.sun.jna.Platform;

import net.pms.util.PropertiesUtil;

public abstract class BaseProgramPaths {
    private String getBinariesPath() {
        String path = PropertiesUtil.getProjectProperties().get("project.binaries.dir");

        if (isNotBlank(path)) {
            if (!path.endsWith("/")) {
                path = path + "/";
            }
        } else {
            path = "";
        }
        
        return path;
    }
    
    public String getPath(String program) {
        if (StringUtils.isBlank(program))
            return null;
        
        String fullPath = program;
        String platform = "";
        String[] extensions = new String[] {""};
        if (Platform.isWindows()) {
            extensions = new String[] {".exe", ".cmd", ".bat"};
            platform = "win32";
        } else if (Platform.isLinux()) {
            platform = "linux";
        } else if (Platform.isMac()) {
            platform = "osx";
        }
        
        for (int i = 0; i < extensions.length; i++) {
            String path = String.format("%s/%s/%s%s", getBinariesPath(), platform, fullPath, extensions[i]);
            File file = new File(path);
            if (file.exists()) {
                fullPath = path;
                break;
            }
        }

        return fullPath;
    }
}
