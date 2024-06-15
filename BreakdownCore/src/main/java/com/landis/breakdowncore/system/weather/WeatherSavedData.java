package com.landis.breakdowncore.system.weather;

import com.landis.breakdowncore.BreakdownCore;
import com.landis.breakdowncore.BREARegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**WeatherSavedData天气数据保存器<br>
 * 天气数据保存器由以下内容构成：<br>
 * {@link ClimateParaHandle}用于存储所有被放入的气候参数。
 * {@link WeatherScheduler}用于在指定时刻决定天气状态。
 * {@link Weather}用于判断当前的天气
 *
 * 天气数据不直接作为世界信息使用，而使用更高级别的世界信息中枢代管。
 * */
public class WeatherSavedData {
    public static final Logger LOGGER = LogManager.getLogger(BreakdownCore.getLogName("world_saved_data.weather"));


    public final ClimateParaHandle climateParaHandle;
    public final HashMap<WeatherScheduler,Weather.ServerState> weatherMap;
    public final ServerLevel level;


    public int climateParaTicker = 0;

    //加载方法
    public WeatherSavedData(CompoundTag nbt, ServerLevel level){
        this.climateParaHandle = new ClimateParaHandle(nbt);
        this.weatherMap = new HashMap<>();
        this.level = level;
        ListTag weathers = nbt.getList("weather_map",10);
        for (Tag tag : weathers){
            if(tag instanceof CompoundTag c){
                weatherMap.put(new WeatherScheduler(c.getCompound("scheduler")),Weather.createState(c.getCompound("weather"),level));
            }else {
                LOGGER.warn("Can't cast tag to CompoundTag:{}",tag);
            }
        }
    }

    //构造方法
    public WeatherSavedData(ClimateParaHandle p,HashMap<WeatherScheduler,Weather.ServerState> w,ServerLevel level){
        this.climateParaHandle = p;
        this.weatherMap = w;
        this.level = level;
        for (Map.Entry<WeatherScheduler, Weather.ServerState> set : weatherMap.entrySet()){
            set.setValue(set.getKey().createNextState(level,climateParaHandle,level.random));
        }
    }

    /**nbt数据结构<br>
     * int climate_tick 存储气候计时器 计时器归零后将对{@link WeatherSavedData#climateParaHandle}执行一次天气刻 <br>
     * listCompound climate_para 存储气候参数 存储与加载由{@link ClimateParaHandle}负责 <br>
     * listCompound weather_map 存储天气提供器与天气状态 包括:<br>
     * -compound scheduler 存储一个计划器的信息 存储与加载由{@link WeatherScheduler}负责 <br>
     * -compound weather 存储一个天气状态的信息 存储与加载由{@link Weather}负责
     * */
    public CompoundTag save(CompoundTag nbt) {
        nbt.putInt("climate_tick",climateParaTicker);
        climateParaHandle.save(nbt);
        ListTag tag = new ListTag();
        for (Map.Entry<WeatherScheduler, Weather.ServerState> set : weatherMap.entrySet()){
            CompoundTag c = new CompoundTag();
            c.put("scheduler",set.getKey().serializeNBT());
            c.put("weather",set.getValue().serializeNBT());
        }
        return nbt;
    }

    //网络发包辅助
    public void synSimpleInfoToClient(CompoundTag compoundTag){
        //添加气候状态：
        ListTag tag = new ListTag();
        for (ClimateParameter.ActivatedState state : climateParaHandle.states){
            CompoundTag c = new CompoundTag();
            c.putString("key", BREARegistries.CLIMATE_PARAMETER.getKey(state.getClimate()).toString());
            c.putFloat("value", state.value);
            c.putFloat("offset", state.offset);
            tag.add(c);
        }
        //添加天气状态：
        ListTag tag2 = new ListTag();
        weatherMap.values().forEach(serverState -> {
            ResourceLocation location = serverState.getId();
            if(location!=null) tag2.add(StringTag.valueOf(location.toString()));
        });
        compoundTag.put("climate",tag);
        compoundTag.put("weather",tag2);
    }

    public void synAllInfoToClient(){

    }

    public void synNewWeatherState(){

    }

    //TODO change to private
    public void climateParaTick(ServerLevel level){
        climateParaHandle.climateTick();
    }



    public static class Builder{
        List<ClimateParameter.ActivatedState> states = new ArrayList<>();

        List<WeatherScheduler> schedulers = new ArrayList<>();

        public Builder addState(ClimateParameter.ActivatedState... states){
            Collections.addAll(this.states, states);
            return this;
        }

        @SafeVarargs
        public final Builder addState(DeferredHolder<ClimateParameter, ClimateParameter>... parameters){
            this.states.addAll(Arrays.stream(parameters).map(c -> new ClimateParameter.ActivatedState(c.get())).toList());
            return this;
        }

        public Builder addScheduler(WeatherScheduler... schedulers){
            Collections.addAll(this.schedulers, schedulers);
            return this;
        }

        public Builder addScheduler(DeferredHolder<WeatherScheduler.WeatherStateProvider, WeatherScheduler.WeatherStateProvider>... providers){
            this.schedulers.addAll(Arrays.stream(providers).map(r->new WeatherScheduler(r.get())).toList());
            return this;
        }
        public WeatherSavedData build(ServerLevel level){
            HashMap<WeatherScheduler,Weather.ServerState> map = new HashMap<>();
            for (WeatherScheduler s : schedulers){
                map.put(s,null);
            }
            return new WeatherSavedData(new ClimateParaHandle(states),map,level);
        }

    }

    public static class ClimateParaHandle {
        private final List<ClimateParameter.ActivatedState> states = new ArrayList<>();
        private final Map<ClimateParameter,Integer> indexMap = new HashMap<>();

        public ClimateParaHandle(ClimateParameter.ActivatedState... states){
            for(ClimateParameter.ActivatedState s : states){
                put(s);
            }
        }

        public ClimateParaHandle(List<ClimateParameter.ActivatedState> states){
            for(ClimateParameter.ActivatedState s : states){
                put(s);
            }
        }

        //从nbt解析数据
        public ClimateParaHandle(CompoundTag tag){
            ListTag list = tag.getList("climate_para",10);
            for(Tag element : list){
                if(element instanceof CompoundTag c){
                    put(new ClimateParameter.ActivatedState(c));
                }else {
                    LOGGER.warn("Can't cast tag:{} to CompoundTag",element);
                }
            }
        }

        private void put(ClimateParameter.ActivatedState state){
            if(indexMap.containsKey(state.getClimate())){
                LOGGER.warn("Can't put {} as there's another element with the same climate has been put in.",state);
            }
            states.add(state);
            indexMap.put(state.getClimate(),states.size() - 1);
        }

        public float getValue(ClimateParameter parameter){
            if(indexMap.containsKey(parameter)){
                return states.get(indexMap.get(parameter)).value;
            }
            return parameter.defaultValue();
        }

        protected void climateTick(){
            for (ClimateParameter.ActivatedState state : states){
                state.climateTick();
            }
        }

        public void save(CompoundTag tag){
            ListTag list = new ListTag();
            for (ClimateParameter.ActivatedState state : states){
                list.add(state.serializeNBT());
            }
            tag.put("climate_para",list);
        }
    }


//    public static final String NAME = "arkdust_weather";
//    public static final Map<ResourceKey<DimensionType>, Supplier<Builder>> BUILDER_MAP = new HashMap<>();
//
//    @Nullable
//    private static SavedData.Factory<WeatherSavedData> factory(ServerLevel level){
//        if(BUILDER_MAP.containsKey(level.dimensionTypeId())){
//            return new Factory<>(()->BUILDER_MAP.get(level.dimensionTypeId()).get().build(level),tag->new WeatherSavedData(tag,level));
//        }
//        return null;
//    }
//
//    public static @NotNull Optional<WeatherSavedData> getInstance(ServerLevel level){
//        Factory<WeatherSavedData> savedDataFactory = factory(level);
//        if(savedDataFactory!=null){
//            return Optional.of(level.getDataStorage().computeIfAbsent(savedDataFactory, NAME));
//        }
//        return Optional.empty();
//    }
}
