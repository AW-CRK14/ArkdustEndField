package com.landis.breakdowncore.system.automatic.update;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class UpdateDownloader implements Callable<Boolean> {
    private String updateUrl;
    private final String saveFilePath= "saveFilePath";
    private int threadId;
    private static final int BUFFER_SIZE = 4096; // 定义缓冲区大小

    public UpdateDownloader(String updateUrl) {
        this.updateUrl = updateUrl;
        this.threadId = 1;
    }

    @Override
    public Boolean call() {
        try (InputStream inputStream = new URL(updateUrl).openStream();
             FileOutputStream fos = new FileOutputStream(saveFilePath, true);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            bos.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void downloadUpdate() {
        int numberOfThreads = 1;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        List<Future<Boolean>> results = new ArrayList<>();
        UpdateDownloader downloader = new UpdateDownloader(this.updateUrl);
        results.add(executorService.submit(downloader));
        executorService.shutdown();

        boolean isDownloadSuccessful = true;
        for (Future<Boolean> result : results) {
            try {
                if (!result.get()) {
                    isDownloadSuccessful = false;
                    break;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                isDownloadSuccessful = false;
            }
        }

        if (isDownloadSuccessful) {
            System.out.println("Download completed successfully.");
        } else {
            System.out.println("Download failed.");
        }
    }
}