package mekanism.common.capabilities;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.ParametersAreNonnullByDefault;
import mcp.MethodsReturnNonnullByDefault;
import mekanism.api.NBTConstants;
import mekanism.api.annotations.FieldsAreNonnullByDefault;
import mekanism.api.chemical.IChemicalTank;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.infuse.IInfusionTank;
import mekanism.api.chemical.merged.MergedChemicalTank;
import mekanism.api.chemical.pigment.IPigmentTank;
import mekanism.api.fluid.IExtendedFluidTank;
import mekanism.common.capabilities.fluid.FluidTankWrapper;
import mekanism.common.util.NBTUtils;
import net.minecraft.nbt.CompoundNBT;

@FieldsAreNonnullByDefault
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MergedTank extends MergedChemicalTank {

    public static MergedTank create(IExtendedFluidTank fluidTank, IGasTank gasTank, IInfusionTank infusionTank, IPigmentTank pigmentTank) {
        Objects.requireNonNull(fluidTank, "Fluid tank cannot be null");
        Objects.requireNonNull(gasTank, "Gas tank cannot be null");
        Objects.requireNonNull(infusionTank, "Infusion tank cannot be null");
        Objects.requireNonNull(pigmentTank, "Pigment tank cannot be null");
        return new MergedTank(fluidTank, gasTank, infusionTank, pigmentTank);
    }

    private final IExtendedFluidTank fluidTank;

    private MergedTank(IExtendedFluidTank fluidTank, IChemicalTank<?, ?>... chemicalTanks) {
        super(fluidTank::isEmpty, chemicalTanks);
        this.fluidTank = new FluidTankWrapper(fluidTank, () -> Arrays.stream(chemicalTanks).allMatch(IChemicalTank::isEmpty));
    }

    public CurrentType getCurrentType() {
        if (!getFluidTank().isEmpty()) {
            return CurrentType.FLUID;
        } else if (!getGasTank().isEmpty()) {
            return CurrentType.GAS;
        } else if (!getInfusionTank().isEmpty()) {
            return CurrentType.INFUSION;
        } else if (!getPigmentTank().isEmpty()) {
            return CurrentType.PIGMENT;
        }
        return CurrentType.EMPTY;
    }

    public final IExtendedFluidTank getFluidTank() {
        return fluidTank;
    }

    public void addToUpdateTag(CompoundNBT updateTag) {
        updateTag.put(NBTConstants.FLUID_STORED, getFluidTank().getFluid().writeToNBT(new CompoundNBT()));
        updateTag.put(NBTConstants.GAS_STORED, getGasTank().getStack().write(new CompoundNBT()));
        updateTag.put(NBTConstants.INFUSE_TYPE_NAME, getInfusionTank().getStack().write(new CompoundNBT()));
        updateTag.put(NBTConstants.PIGMENT_STORED, getPigmentTank().getStack().write(new CompoundNBT()));
    }

    public void readFromUpdateTag(CompoundNBT tag) {
        NBTUtils.setFluidStackIfPresent(tag, NBTConstants.FLUID_STORED, value -> getFluidTank().setStack(value));
        NBTUtils.setGasStackIfPresent(tag, NBTConstants.GAS_STORED, value -> getGasTank().setStack(value));
        NBTUtils.setInfusionStackIfPresent(tag, NBTConstants.INFUSE_TYPE_NAME, value -> getInfusionTank().setStack(value));
        NBTUtils.setPigmentStackIfPresent(tag, NBTConstants.PIGMENT_STORED, value -> getPigmentTank().setStack(value));
    }

    public enum CurrentType {
        EMPTY,
        FLUID,
        GAS,
        INFUSION,
        PIGMENT
    }
}