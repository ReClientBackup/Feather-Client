package net.minecraft.block.state;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.*;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.Cartesian;
import net.minecraft.util.MapPopulator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BlockState
{
    private static final Joiner COMMA_JOINER = Joiner.on(", ");
    private static final Function<IProperty, String> GET_NAME_FUNC = p_apply_1_ -> p_apply_1_ == null ? "<NULL>" : p_apply_1_.getName();
    private final Block block;
    private final ImmutableList<IProperty> properties;
    private final ImmutableList<IBlockState> validStates;

    public BlockState(Block blockIn, IProperty... properties) {
        this.block = blockIn;
        Arrays.sort(properties, Comparator.comparing(IProperty::getName));
        this.properties = ImmutableList.copyOf(properties);
        Map<Map<IProperty, Comparable>, StateImplementation> map = Maps.newLinkedHashMap();
        List<StateImplementation> list = Lists.newArrayList();

        for (List<Comparable> list1 : Cartesian.cartesianProduct(this.getAllowedValues())) {
            Map<IProperty, Comparable> map1 = MapPopulator.createMap(this.properties, list1);
            StateImplementation implementation = new StateImplementation(blockIn, ImmutableMap.copyOf(map1));
            map.put(map1, implementation);
            list.add(implementation);
        }

        for (StateImplementation stateImplementation : list)
        {
            stateImplementation.buildPropertyValueTable(map);
        }

        this.validStates = ImmutableList.copyOf(list);
    }

    public ImmutableList<IBlockState> getValidStates()
    {
        return this.validStates;
    }

    private List<Iterable<Comparable>> getAllowedValues()
    {
        List<Iterable<Comparable>> list = Lists.newArrayList();

        for (IProperty property : properties) {
            list.add((Iterable<Comparable>) property.getAllowedValues());
        }

        return list;
    }

    public IBlockState getBaseState()
    {
        return this.validStates.get(0);
    }

    public Block getBlock()
    {
        return this.block;
    }

    public Collection<IProperty> getProperties()
    {
        return this.properties;
    }

    public String toString()
    {
        return Objects.toStringHelper(this).add("block", Block.blockRegistry.getNameForObject(this.block)).add("properties", this.properties.stream().map(GET_NAME_FUNC).collect(Collectors.toList())).toString();
    }

    static class StateImplementation extends BlockStateBase
    {
        private final Block block;
        private final ImmutableMap<IProperty, Comparable> properties;
        private ImmutableTable<IProperty, Comparable, IBlockState> propertyValueTable;

        private StateImplementation(Block blockIn, ImmutableMap<IProperty, Comparable> propertiesIn)
        {
            this.block = blockIn;
            this.properties = propertiesIn;
        }

        public Collection<IProperty> getPropertyNames()
        {
            return Collections.unmodifiableCollection(this.properties.keySet());
        }

        public <T extends Comparable<T>> T getValue(IProperty<T> property)
        {
            if (!this.properties.containsKey(property))
            {
                throw new IllegalArgumentException("Cannot get property " + property + " as it does not exist in " + this.block.getBlockState());
            }
            else
            {
                return property.getValueClass().cast(this.properties.get(property));
            }
        }

        public <T extends Comparable<T>, V extends T> IBlockState withProperty(IProperty<T> property, V value)
        {
            if (!this.properties.containsKey(property))
            {
                throw new IllegalArgumentException("Cannot set property " + property + " as it does not exist in " + this.block.getBlockState());
            }
            else if (!property.getAllowedValues().contains(value))
            {
                throw new IllegalArgumentException("Cannot set property " + property + " to " + value + " on block " + Block.blockRegistry.getNameForObject(this.block) + ", it is not an allowed value");
            }
            else
            {
                return this.properties.get(property) == value ? this : this.propertyValueTable.get(property, value);
            }
        }

        public ImmutableMap<IProperty, Comparable> getProperties() {
            return this.properties;
        }

        public Block getBlock()
        {
            return this.block;
        }

        public boolean equals(Object p_equals_1_)
        {
            return this == p_equals_1_;
        }

        public int hashCode()
        {
            return this.properties.hashCode();
        }

        public void buildPropertyValueTable(Map<Map<IProperty, Comparable>, StateImplementation> map)
        {
            if (this.propertyValueTable != null)
            {
                throw new IllegalStateException();
            }
            else
            {
                Table<IProperty, Comparable, IBlockState> table = HashBasedTable.create();

                for (IProperty <? extends Comparable > iproperty : this.properties.keySet())
                {
                    for (Comparable comparable : iproperty.getAllowedValues())
                    {
                        if (comparable != this.properties.get(iproperty))
                        {
                            table.put(iproperty, comparable, map.get(this.setPropertyValue(iproperty, comparable)));
                        }
                    }
                }

                this.propertyValueTable = ImmutableTable.copyOf(table);
            }
        }

        private Map<IProperty, Comparable> setPropertyValue(IProperty property, Comparable value)
        {
            Map<IProperty, Comparable> map = Maps.newHashMap(this.properties);
            map.put(property, value);
            return map;
        }
    }
}
