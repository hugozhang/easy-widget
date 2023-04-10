package me.about.widget.trace.strategy.asm;

import me.about.widget.trace.util.PkgUtils;
import org.springframework.util.AntPathMatcher;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: hugo.zxh
 * @date: 2022/03/09 14:51
 * @description:
 */

public class PathScanProvider {

    private static AntPathMatcher antPathMatcher = new AntPathMatcher();

    private static String locationSuffix = "/**/*.class";

    public void scan(String locationPattern) {
        String rootDirPath = determineRootDir(toLocation(locationPattern));
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Set<String> clzFromPkg = PkgUtils.getClzFromPkg(rootDirPath.replace('/','.'));
        for ( String clz : clzFromPkg ) {
            if (antPathMatcher.match(toLocation(locationPattern + locationSuffix),toLocation(clz + locationSuffix))) {
                ByteCodeEnhance.inject(clz,loader);
            }
        }
    }

    private String toLocation(String packagePath) {
        String replace = packagePath.replace('.', '/');
        return replace;
    }

    private String determineRootDir(String location) {
        int prefixEnd = location.indexOf(":") + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && antPathMatcher.isPattern(location.substring(prefixEnd, rootDirEnd))) {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2);
        }
        if (rootDirEnd == 0) {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }

    public PathScanProvider() {
//        if (basePackages == null) {
//            throw new IllegalArgumentException("basePackages is required.");
//        }
//        doScan(StringUtils.tokenizeToStringArray(basePackages, CONFIG_LOCATION_DELIMITERS));
    }
}
