package com.bird.common.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhyyy
 **/
public class KeySeedManager {

    private static final Map<String, CacheKeySeed> CACHE_KEY_SEED_MAP = new HashMap<>(64);

    public synchronized static CacheKeySeed get(String keyPrefix) {
        CacheKeySeed ret;
        if ((ret = CACHE_KEY_SEED_MAP.get(keyPrefix)) == null) {
            ret = new CacheKeySeed(keyPrefix);
            CACHE_KEY_SEED_MAP.put(keyPrefix, ret);
        }
        return ret;
    }
}
