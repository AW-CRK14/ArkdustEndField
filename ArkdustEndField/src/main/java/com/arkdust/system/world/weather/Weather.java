package com.arkdust.system.world.weather;

import com.arkdust.registry.regtype.ArkdustRegistry;
import com.arkdust.render.environment.ISkyAndFogRenderer;
import com.arkdust.render.environment.SkyAndFogRenderSub;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.List;
import java.util.OptionalInt;

/**
 * Weather天气
 * 天气用于在发生后决定其对位置的影响，包括在客户端创建天气粒子，在服务端生成内容等<br>
 * 其中proportion占比是由玩家所处的位置决定的，如果玩家周围多个位置的天气判定不一样，将会按照比例推送给各种天气类型<br>
 * 天气类不直接决定天气的生成条件与强度，时间等，对于决定天气是否生成，请参阅{@link WeatherScheduler}<br>
 * 天气发生时在客户端会被推送至{@link SkyAndFogRenderSub}。这里推送的迷雾与天空颜色等数据由Weather提供，因此无法被具体的环境参数影响。
 * */
public abstract class Weather implements ISkyAndFogRenderer {
    //aog表示天气是否为天灾类型。如果是，它将拥有更高的渲染优先级。也可以用于其它用途。
    public final boolean isAog;
    public final OptionalInt skyColor;
    public final OptionalInt fogColor;
    public final IntIntPair distance;
    public Weather(boolean isAog, OptionalInt skyColor, OptionalInt fogColor, IntIntPair distance){
        this.isAog = isAog;
        this.skyColor = skyColor;
        this.fogColor = fogColor;
        this.distance = distance;
    }

    @Override
    public OptionalInt getSkyColor() {
        return skyColor;
    }

    @Override
    public OptionalInt getFogColor() {
        return fogColor;
    }

    @Override
    public @Nullable IntIntPair getFogDistance() {
        return distance;
    }

    @Override
    public float priority() {
        return isAog ? 10 : 2.5F;
    }

    @OnlyIn(Dist.CLIENT)
    public abstract void clientTick(ActivatedStateInClient state, @Range(from = 0L,to = 1L) float proportion);

    @OnlyIn(Dist.DEDICATED_SERVER)
    public abstract void serverTick(ActivatedStateInServer state, @Range(from = 0L,to = 1L) float proportion);

    public abstract void initiate(Level level,int strength,int keepTime);

    public abstract void finish(Level level,int strength,int keepTime);

    public abstract boolean canActiveAt(ActivatedStateInServer state, Player player);

    /**ActivatedState是Weather在具体应用时的实例。包含weather的种类，强度，持续时间等。<br>
     * 在天气未发生时，不会产生对应的ActivatedState。<br>
     * 客户端也会存储ActivatedState。在客户端的tick与天空渲染监测将由客户端方面完成。但客户端方面不会记录天气时长，只有收到服务端发包后才会停止。
     * */
    @OnlyIn(Dist.DEDICATED_SERVER)
    public static class ActivatedStateInServer implements ServerState{
        public Weather getWeather() {
            return weather;
        }

        public int getStrength() {
            return strength;
        }

        public int getTime() {
            return time;
        }

        public ServerLevel getLevel() {
            return level;
        }

        private Weather weather;
        private int strength, time;
        private ServerLevel level;
        private int ticker;
        public ActivatedStateInServer(Weather weather, int strength, int time, ServerLevel level){
            this.weather = weather;
            this.strength = strength;
            this.time = time;
            this.level = level;
            this.ticker = time;
            initiate(false);
        }

        public ActivatedStateInServer(CompoundTag tag,ServerLevel level){
            deserializeNBT(tag);
            this.level = level;
            initiate(true);
        }

        public int getTicker(){
            return ticker;
        }

        //当一个ActivatedState被创建时，进行初始化指令。只应在构造并添加进维度的信息时被调用
        public void initiate(boolean isReload){
            if(!isReload) {
                weather.initiate(level, strength, time);
            }
            noticeClient(level.players(),false,isReload);
        }
        public void noticeClient(List<ServerPlayer> player,boolean finish,boolean isReload){
            //TODO 在玩家进入维度时初始化客户端信息
        }

        //只应在周期结束并释放此state时调用。
        public ActivatedStateInServer finish(){
            weather.finish(level,strength,time);
            return this;
        }

        /**@return 如果在完成此tick后时间归零，将返回false*/
        public boolean tick(float proportion){
            ticker--;
            weather.serverTick(this,proportion);
            return ticker >= 0;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("weather", ArkdustRegistry.WEATHER.getKey(weather).toString());
            tag.putInt("strength", strength);
            tag.putInt("time", time);
            tag.putInt("ticker", ticker);
            tag.putBoolean("clear",false);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.weather = ArkdustRegistry.WEATHER.get(new ResourceLocation(nbt.getString("weather")));
            this.strength = nbt.getInt("strength");
            this.time = nbt.getInt("time");
            this.ticker = nbt.getInt("ticker");
            initiate(true);
        }


        @Override
        public @Nullable ResourceLocation getId() {
            return ArkdustRegistry.WEATHER.getKey(weather);
        }
    }

    @OnlyIn(Dist.DEDICATED_SERVER)
    public static class ClearState implements ServerState {
        public int time;
        private int ticker;
        public ClearState(int time){
            this.time = time;
            this.ticker = time;
        }

        public ClearState(CompoundTag tag){
            deserializeNBT(tag);
        }

        public int getTicker(){
            return ticker;
        }

        @Override
        public boolean tick(float proportion) {
            return ticker-- <= 0;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("time", time);
            tag.putInt("ticker", ticker);
            tag.putBoolean("clear",true);
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            this.ticker = nbt.getInt("ticker");
            this.time = nbt.getInt("time");
        }


        @Override
        public @Nullable ResourceLocation getId() {
            return null;
        }
    }

    public interface ServerState extends INBTSerializable<CompoundTag>{
        int getTicker();
        boolean tick(float proportion);

        @Nullable ResourceLocation getId();
    }

    public static ServerState createState(CompoundTag tag,ServerLevel server){
        return tag.getBoolean("clear") ? new ClearState(tag) : new ActivatedStateInServer(tag,server);
    }

    @OnlyIn(Dist.CLIENT)
    public static class ActivatedStateInClient{
        public final Weather weather;
        public final int strength;
        public ActivatedStateInClient(Weather weather, int strength){
            this.weather = weather;
            this.strength = strength;
        }

        public ActivatedStateInClient(Weather weather, int strength, int time){
            this.weather = weather;
            this.strength = strength;
            initiate(time);
        }

        //当一个ActivatedState被创建时，进行初始化指令。只应在构造并添加进维度的信息时被调用
        public void initiate(int time){
            weather.initiate(Minecraft.getInstance().level, strength,time);
        }

        //只应在周期结束并释放此state时调用。
        public ActivatedStateInClient finish(int time){
            weather.finish(Minecraft.getInstance().level,strength,time);
            return this;
        }

        public void tick(float proportion){
            weather.clientTick(this,proportion);
        }
    }
}
