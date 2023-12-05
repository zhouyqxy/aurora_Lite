package com.aurora.strategy.impl;

import com.aurora.config.properties.LocalProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * TODO 文件本地存储方案
 */
@Slf4j
@Service("localUploadStrategyImpl")
public class LocalUploadStrategyImpl extends AbstractUploadStrategyImpl {


    @Autowired
    private LocalProperties localProperties;

    @Override
    public Boolean exists(String filePath) {
        return false;//直接覆盖 不做判断
    }

    @SneakyThrows
    @Override
    public void upload(String path, String fileName, InputStream inputStream) {
        String newPath = localProperties.getDir()
                + path + fileName;
        File file = new File(newPath);
        File dir = new File(localProperties.getDir()+path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream fos = new FileOutputStream(newPath);
        byte[] b = new byte[1024];
        int length;
        while ((length = inputStream.read(b)) > 0) {
            fos.write(b, 0, length);
        }
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        return localProperties.getUrl() + filePath;
    }
}
