package com.landis.breakdowncore.system.automatic.update;

public class UpdateManager {
    private final UpdateChecker checker;
    private final UpdateDownloader downloader;
    private final UpdateInstaller installer;

    public UpdateManager(String updateUrl, int version) {
        this.checker = new UpdateChecker(updateUrl,version);
        this.downloader = new UpdateDownloader(updateUrl);
        this.installer = new UpdateInstaller();
    }
/*
    public void checkAndInstallUpdate() {
        if (checker.isUpdateAvailable()) {
            try {
                downloader.downloadUpdate();
                installer.installUpdate();
                System.out.println("Update installed successfully.");
            } catch (Exception e) {
                System.err.println("Failed to update: " + e.getMessage());
                // 这里可以添加错误恢复逻辑
            }
        } else {
            System.out.println("You are using the latest version.");
        }
    }*/
}