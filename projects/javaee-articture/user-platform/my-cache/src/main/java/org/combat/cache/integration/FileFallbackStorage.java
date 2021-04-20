package org.combat.cache.integration;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.*;
import java.util.logging.Logger;

/**
 * @author zhangwei
 * @Description FileFallbackStorage
 * @Date: 2021/4/17 09:58
 */
public class FileFallbackStorage extends AbstractFallbackStorage<Object, Object> {

    private static final File CACHE_FALLBACK_DIRECTORY = new File("./cache/fallback/");

    private final Logger logger = Logger.getLogger(getClass().getName());

    public FileFallbackStorage() {
        super(Integer.MAX_VALUE);
        makeCacheFallbackDirectory();
    }

    @Override
    public void destroy() {
        destroyCacheFallbackDirectory();
    }

    @Override
    public Object load(Object key) throws CacheLoaderException {
        File storageFile = toStorageFile(key);
        if (!storageFile.exists() || !storageFile.canRead()) {
            logger.warning(String.format("The storage file[path:%s] does not exist or can't be read, " +
                    "thus the value can't be loaded.", storageFile.getAbsoluteFile()));
            return null;
        }

        Object value = null;
        try (FileInputStream inputStream = new FileInputStream(storageFile)) {
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            value = objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.severe(String.format("The deserialization of value[%s] is failed, caused by :%s",
                    value, e.getMessage()));
        }
        return value;
    }

    @Override
    public void write(Cache.Entry<?, ?> entry) throws CacheWriterException {
        Object key = entry.getKey();
        Object value = entry.getValue();
        File storageFile = toStorageFile(key);
        if (storageFile.exists() && !storageFile.canWrite()) {
            logger.warning(String.format("The storage file[patn:%s] can't be written, " +
                    "thus the entry will not be stored.", storageFile.getAbsoluteFile()));
            return;
        }
        try (FileOutputStream outputStream = new FileOutputStream(storageFile);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            objectOutputStream.writeObject(value);
        } catch (IOException e) {
            logger.severe(String.format("The serialization of value[%s] is failed, caused by :%s",
                    value, e.getMessage()));
        }
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        File storageFile = toStorageFile(key);
        storageFile.delete();
    }

    private void makeCacheFallbackDirectory() {
        if (!CACHE_FALLBACK_DIRECTORY.exists() && !CACHE_FALLBACK_DIRECTORY.mkdirs()) {
            throw new RuntimeException(String.format("The fallback directory[path:%s] can't be created!"));
        }
    }

    private void destroyCacheFallbackDirectory() {
        if (CACHE_FALLBACK_DIRECTORY.exists()) {
            for (File storageFile : CACHE_FALLBACK_DIRECTORY.listFiles()) {
                storageFile.delete();
            }
        }
    }

    File toStorageFile(Object key) {
        return new File(CACHE_FALLBACK_DIRECTORY, key.toString() + ".dat");
    }
}
