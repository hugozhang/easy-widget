package me.about.widget.spring.mvc.config;

import lombok.Getter;

@Getter
public enum ModuleInfo {
    PORTAL("portal", "com.winning.hmap.portal"),
    TEST("test", "com.winning.hmap.xxx");

    private final String moduleName;
    private final String moduleBasePackage;

    ModuleInfo(String moduleName, String moduleBasePackage) {
        this.moduleName = moduleName;
        this.moduleBasePackage = moduleBasePackage;
    }

}
