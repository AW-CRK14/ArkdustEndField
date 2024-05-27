package com.landis.arkdust.helper;

import com.landis.arkdust.Arkdust;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ListAndMapHelper {

    public static <T> void addAll(List<T> list,T... elements){
        Collections.addAll(list, elements);
    }

    public static <T> List<T> toList(T[] l){
        return Arrays.stream(l).collect(ArrayList::new,ArrayList::add,ArrayList::addAll);
    }

    public static <T> List<T> joinList(T[]... lists){
        List<T> cList = new ArrayList<>();
        Arrays.stream(lists).forEach((l)->cList.addAll(List.of(l)));
        return cList;
    }

    public static <T> T multiListGetElement(Random random,T[]... lists){
        List<T> compList = joinList(lists);
        return compList.get(random.nextInt(compList.size()));
    }

    public static <T> List<T> copyList(List<T> list){
        return new ArrayList<>(list);
    }

    /**不必要的设计方法。
     * @see Collections#nCopies
     * */
    @Deprecated
    public static <T> List<T> fill(int length,T obj){
        List<T> list = new ArrayList<>(length);
        for(int i = 0; i < length; i++){
            list.set(i,obj);
        }
        return list;
//        return Stream.generate(()->obj).limit(length).toList();
    }



    public static <S> int getIndexFromMap(Map<S,?> map,S key){
        if (!map.containsKey(key)) return -1;
        Object[] keySet = map.keySet().toArray();
        for (int i = 0; i < map.size(); i++) {
            if(keySet[i].equals(key))
                return i;
        }
        return -1;
    }

    public static <T> String listToString(List<T> list){
        StringBuilder sb = new StringBuilder();
        for (Object obj : list) {
            sb.append(obj).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.insert(0, "[").insert(sb.length(), "]");
        return sb.toString();
    }

    public static <T> List<T> resort(List<T> org,int[] index_reflect){
        if(index_reflect.length != org.size()) return null;
        List<T> list = new ArrayList<>(org);
        for (int i = 0 ; i < org.size() ; i++){
            list.set(index_reflect[i],org.get(i));
        }
        return list;
    }

    public static List<ResourceLocation> string2RLList(String... ss){
        List<ResourceLocation> list = new ArrayList<>();
        for(String s:ss){
            list.add(new ResourceLocation(Arkdust.MODID,s));
        }
        return list;
    }

    public static List<ResourceLocation> string2RLListWithPath(String path, String... ss){
        List<ResourceLocation> list = new ArrayList<>();
        for(String s:ss){
            list.add(new ResourceLocation(Arkdust.MODID,path + s));
        }
        return list;
    }

    public static <T> List<T> disorganizeList(Random random, List<T>... lists){
        List<T> list = new ArrayList<>();
        for (List<T> l : lists){
            if(l == null) continue;
            list.addAll(l);
        }
        Collections.shuffle(list,random);
        return list;
    }

    public static <T,K> void tryAddElementToMapList(Map<K,List<T>> map,K key,List<T> obj){
        if(map.containsKey(key)) map.get(key).addAll(obj);
        else map.put(key,copyList(obj));
    }

    public static <T,K> void tryDeleteElementFromMapList(Map<K,List<T>> map, K key, List<T> obj){
        if(map.containsKey(key)) map.get(key).removeAll(obj);
        else map.put(key,new ArrayList<>());
    }

    public static <T,K,C> void tryTransListToMapList(Map<K,List<T>> map, List<C> list, Function<C,? extends K> createKey,Function<C,T> createValue){
        for (C c:list){
            K key = createKey.apply(c);
            T value = createValue.apply(c);
            if(key!=null && value!=null){
                if(map.containsKey(key)) map.get(key).add(value);
                else {
                    List<T> values = new ArrayList<>();
                    values.add(value);
                    map.put(key,values);
                }
            }
        }
    }


    public static <T,K> List<T> getNonnullListInMap(Map<K,List<T>> map, K key){
        return map.get(key) == null ? Collections.EMPTY_LIST : map.get(key);
    }

    public static <T,K> void tryAddElementToMapList(Map<K,List<T>> map,K key,T... obj){
        tryAddElementToMapList(map,key,Arrays.asList(obj));
    }

}
