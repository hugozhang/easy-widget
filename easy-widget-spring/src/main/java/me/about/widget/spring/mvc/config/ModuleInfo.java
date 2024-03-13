package me.about.widget.spring.mvc.config;

public enum ModuleInfo {
    PORTAL("portal", "com.winning.hmap.portal"),
    TEST("test", "com.winning.hmap.xxx");

    private String moduleName;
    private String moduleBasePackage;

    ModuleInfo(String moduleName, String moduleBasePackage) {
        this.moduleName = moduleName;
        this.moduleBasePackage = moduleBasePackage;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleBasePackage() {
        return moduleBasePackage;
    }
}
