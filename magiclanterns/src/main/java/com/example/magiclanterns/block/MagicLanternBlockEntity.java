package com.example.magiclanterns.block;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.fabricmc.fabric.api.transfer.v1.energy.EnergyStorage;
import net.fabricmc.fabric.api.transfer.v1.energy.EnergyStorageUtil;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

import java.util.concurrent.atomic.AtomicLong;

public class MagicLanternBlockEntity extends BlockEntity {
    private long energy = 0;
    private static final long MAX_ENERGY = 10000;
    private static final long USAGE_PER_TICK = 5;

    // Участник транзакции для отката изменений
    private final EnergyStorage storage = new InternalStorage();

    public MagicLanternBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAGIC_LANTERN_BE, pos, state);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        this.energy = nbt.getLong("Energy");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        nbt.putLong("Energy", this.energy);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MagicLanternBlockEntity be) {
        if (world == null || world.isClient) return;

        // Потребление энергии и применение эффекта
        if (be.energy >= USAGE_PER_TICK) {
            be.energy -= USAGE_PER_TICK;

            // Применяем эффект ночного зрения игрокам в радиусе 8 блоков
            world.getPlayers().forEach(player -> {
                if (player.squaredDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64) { // 8^2 = 64
                    player.addStatusEffect(new net.minecraft.entity.effect.StatusEffectInstance(
                        net.minecraft.entity.effect.StatusEffects.NIGHT_VISION,
                        20 * 3, // 3 секунды
                        0,
                        false,
                        false,
                        true
                    ));
                }
            });

            be.markDirty();
        } else {
            // Если энергии нет — показываем вспышку
            world.syncWorldEvent(WorldEvents.ENERGY_GONE, pos, 0);
        }
    }

    // Внутреннее хранилище энергии
    private class InternalStorage implements EnergyStorage {
        @Override
        public boolean supportsInsertion() {
            return true;
        }

        @Override
        public long insert(long maxAmount, TransactionContext transaction) {
            long inserted = Math.min(MAX_ENERGY - energy, maxAmount);
            if (inserted > 0) {
                transaction.addOuterCloseCallback(() -> energy += inserted);
            }
            return inserted;
        }

        @Override
        public boolean supportsExtraction() {
            return false;
        }

        @Override
        public long extract(long maxAmount, TransactionContext transaction) {
            return 0;
        }

        @Override
        public long getAmount() {
            return energy;
        }

        @Override
        public long getCapacity() {
            return MAX_ENERGY;
        }
    }

    // Получить хранилище
    public Storage<ItemVariant> getStorage() {
        return InventoryStorage.of(this.getInventory(), null);
    }

    public EnergyStorage getEnergyStorage() {
        return storage;
    }
}

// Попробуем зарядиться от кристалла сверху
BlockPos abovePos = pos.up();
BlockState aboveState = world.getBlockState(abovePos);
if (aboveState.isOf(Blocks.AIR)) {
    // Проверим, есть ли предмет (например, кристалл)
    net.minecraft.util.math.Box box = new net.minecraft.util.math.Box(abovePos);
    java.util.List<net.minecraft.entity.ItemEntity> items = world.getNonSpectatingEntities(net.minecraft.entity.ItemEntity.class, box);
    for (net.minecraft.entity.ItemEntity item : items) {
        if (item.getStack().isOf(ModItems.CRYSTAL_SHARD) && item.getStack().getCount() > 0) {
            try (Transaction tx = Transaction.openOuter()) {
                long inserted = EnergyStorageUtil.insert(item.getStack(), 100, tx); // 100 ME за кристалл
                if (inserted > 0) {
                    energy = Math.min(energy + inserted, MAX_ENERGY);
                    item.getStack().decrement(1);
                    markDirty();
                    tx.commit();
                    break;
                }
            }
        }
    }
}

// Удали импорты Transfer API
// Оставь только наш EnergyStorage

private final EnergyStorage energyStorage = new EnergyStorage(10000, this::markDirty);

// Геттеры
public EnergyStorage getEnergyStorage() {
    return energyStorage;
}

// В tick() замени энергию:
if (energyStorage.getEnergy() >= 5) {
    energyStorage.extract(5);
    // ... применяем эффект
}


// Солнечная зарядка (только днём и если видно небо)
boolean isDay = world.getLevelProperties().isDayTime();
boolean canSeeSky = world.isSkyVisible(pos.up());

if (isDay && canSeeSky && energyStorage.getEnergy() < energyStorage.getMaxEnergy()) {
    energyStorage.insert(50); // +50 ME за тик днём
}


private int mode = 0; // 0: Night Vision, 1: Speed, 2: Resistance

public void cycleMode() {
    mode = (mode + 1) % 3;
}

public StatusEffect getEffect() {
    return switch (mode) {
        case 1 -> StatusEffects.SPEED;
        case 2 -> StatusEffects.RESISTANCE;
        default -> StatusEffects.NIGHT_VISION;
    };
}