package com.liferay;

import com.liferay.document.library.kernel.store.Store;
import com.liferay.osgi.util.service.Snapshot;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.model.Image;
import com.liferay.portal.kernel.service.ImageLocalServiceUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.osgi.service.component.annotations.Component;

@Component
public class LiferayImageChecker {

    private static final String ROOT_PATH = "../../data/document_library/20096/0";
    private static int fileCounter = 0;
    private static final Snapshot<Store> _storeSnapshot = new Snapshot<>(
		LiferayImageChecker.class, Store.class, "(default=true)");

    public static void main(String[] args) {
        List<String> missingImages = new ArrayList<>();

        traverseAndCheck(new File(ROOT_PATH), missingImages);

        System.out.println("Missing Images:");
        for (String path : missingImages) {
            System.out.println(path);
        }
    }

    private static void traverseAndCheck(File dir, List<String> missingImages) { 
        if (!dir.isDirectory()) {
            return;
        }
        if (dir.getPath().contains("/0/adaptive/")) {
            return;
        }

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                traverseAndCheck(file, missingImages);
            } else if (file.isFile()) {
                String fileName = file.getName();
                Long imageId = extractImageId(fileName);
                fileCounter++;
                if (fileCounter % 1000 == 0) {
                    System.out.println("Processed " + fileCounter + " files...");
                }
                if (imageId != null && !imageExists(imageId)) {
                    missingImages.add(file.getAbsolutePath());
                    Store store = _storeSnapshot.get();

                    String imageFileName = imageId + ".jpg";
                    System.out.println(imageFileName);
                    store.deleteFile(
                        20096, 0,
                        imageFileName,
                        Store.VERSION_DEFAULT);
                    // if (!file.delete()) {
                    //     System.err.println("Failed to delete: " + file.getAbsolutePath());
                    // }
                }
            }
        }
    }

    private static Long extractImageId(String fileName) {
        // Example: 34833_1.0.jpg or 34926.jpg => get 34833 or 34926
        try {
            if (fileName.contains("_")) {
                return Long.parseLong(fileName.split("_")[0]);
            } else if (fileName.contains(".")) {
                return Long.parseLong(fileName.split("\\.")[0]);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    private static boolean imageExists(long imageId) {
        try {
            Image image = ImageLocalServiceUtil.fetchImage(imageId);
            return image != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
