package mekanism.common.lib.multiblock;

import java.util.UUID;

public interface IMultiblock<T extends MultiblockData> extends IMultiblockBase {

    T createMultiblock();

    default T getMultiblock() {
        return (T) IMultiblockBase.super.getMultiblockData();
    }

    FormationProtocol<T> getFormationProtocol();

    @Override
    T getDefaultData();

    MultiblockManager<T> getManager();

    UUID getCacheID();

    MultiblockCache<T> getCache();

    void resetCache();

    void setCache(MultiblockCache<T> cache);

    boolean isMaster();

    default boolean hasCache() {
        return getCache() != null;
    }

    default IStructureValidator validateStructure() {
        return new CuboidStructureValidator(getStructure());
    }
}