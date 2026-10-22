package com._idrae.towers_of_the_wild.platform;

import com._idrae.towers_of_the_wild.TowersOfTheWild;
import com._idrae.towers_of_the_wild.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

// Locates the loader-specific implementation of a service declared in META-INF/services of the fabric/neoforge jars.
public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz, Services.class.getClassLoader())
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        TowersOfTheWild.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
