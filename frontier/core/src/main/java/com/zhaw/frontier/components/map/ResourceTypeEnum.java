package com.zhaw.frontier.components.map;

public enum ResourceTypeEnum {
    RESOURCE_TYPE_WOOD("wood"),
    RESOURCE_TYPE_STONE("stone"),
    RESOURCE_TYPE_IRON("iron");

    private final String resourceType;

    /**
     * Constructor for the ResourceTypeEnum.
     * @param resourceType the type of resource
     */
    ResourceTypeEnum(String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Returns the resource type as a string.
     * @return the resource type
     */
    @Override
    public String toString() {
        return resourceType;
    }
}
