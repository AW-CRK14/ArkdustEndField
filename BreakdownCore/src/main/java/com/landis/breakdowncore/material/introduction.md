# 材料(Material)

材料用于描述一个物品的成分基本特征。

显然，一种材料需要具有在各方面的属性。这些特性通过`MaterialFeature`注册，比如MetalMF提供金属特性。  
一个特征应当继承`IMaterialFeature`接口，这一接口主要要求提供一组`Holder<MaterialItemType>`即特性所包含的类型物品，一组特性依赖。  

具体而言，比如说我们要创造一个“铁”“金属”“块”，我们将需要:
* “块”的`MaterialItemType`，指定其单位为810，也就是一个物质价值为810个铁单位。
* “金属”的`MaterialFeature`，设置依赖为无，类型物品为\[锭,块\]
* “铁”的`Material`，我们为其添加“金属”的MaterialFeature的特性。

# 运行流程
1. 数据收集阶段，这一阶段与mod信息收集阶段平行。在`FMLCommonSetup`事件，优先级为Lowest的时候，所有被添加的[`Handle$Material`](Handler$Material.java)将被统一调用收集信息，同时dataGather标志将被设置为false。
2. 基本注册阶段，这一阶段将注册所有使用`DeferredRegistry`注册的材料，材料物品种类，材料特征。在这一阶段，材料与材料特征被调用时将会拉取 阶段1 提供的，对于本对象的额外附加内容。在这之后，这两个表将会被清空。
3. 额外注册阶段，这一阶段与`RegistryEvent`事件平行。在这一阶段中，所有上述内容已经完成注册，这时将会进行额外注册处理，为对应的材料与物品类型进行额外内容注册。
4. 注册结束阶段，这一阶段与优先级为Low的`RegistryEvent`事件平行。在这一阶段中，将会创建各种额外的映射表，比如`MF_CLASS2MFH`,`M2MI`,`M1MIT2I`等，在阶段1收集的其它信息也将在这一阶段被统一处理。在这之后infoBuild标志将被设置为false。

## [TypedMaterialItem材料类型物品](TypedMaterialItem.java) 与 [ITypedMaterialObj材料类型对象接口](ITypedMaterialObj.java)

材料类型物品是一个独立的物品，其内部包含的nbt数据将使其在不同材料条件下变成不同的样式。也就是说，所有这个形态的材料物品都是同一个物品。   
因此请不要直接通过物品判断来决定它是否为你需要的物品。这有可能导致错误，比如说，你需要下届合金齿轮，但匹配到一个地狱岩齿轮。

`TypedMaterialItem`在`Item`的基础上，主要提供了两个信息：
* 一个实例常量，存储`MaterialItemType`。每种`MaterialItemType`将被分配一个默认`Item`。
* 一个`Material`的nbt数据，存储物品的材料。

一个`TypedMaterialItem`应当实现`ITypedMaterialObj`接口。这一接口需要实现两个方法，分别是提供`MaterialItemType`与`Material`。  
如果您不想为某个物品提供`MaterialItemType`但是想继续使用材料系统，您可以将前者提供一个Unexplained，并覆写`getContext`方法来指定其物质含量。

更多信息请参考[`TypedMaterialItem`](TypedMaterialItem.java)与[`ITypedMaterialObj`](ITypedMaterialObj.java)内的javadoc内容。

## [MaterialItemType材料物品类型](MaterialItemType.java)

在上面的材料类型物品中我们提到了，材料物品类型被以常量形式存储在其中。这二者会相互存储，因此我们可以通过其中任意一个获取另一者。

材料物品类型中存在一个`createItem`方法，这个方法没有添加final修饰，意味着您可以覆写该内容。  
在通过Material与MaterialItemType创建物品时，程序将会先在Material中请求创建物品，若返回值为null，将会继续在MaterialItemType中请求，这时将不能返回null。  
这一设计用于处理一些诸如块之类的特殊物品。

更多信息请参考[`MaterialItemType材料物品类型`](MaterialItemType.java)

## [IMaterialFeature材料特征](IMaterialFeature.java)与[MFHandle材料特征处理器](MaterialFeatureHandle.java)

材料特征接口只要求您返回对应的实例与实现获取类型的方法。为了避免出现不必要的问题，请将其中的泛型I设置为您的实现类。

`MaterialFeatureHandle`则是实际注册用到的类，您可以将其理解为一种类似Type或Capability的设计。在这里您可以声明其包含的`MaterialItemType`，以及实现类的Class。

在`Material`中加入`MaterialFeature`，加载完成后会自动创建对应的表，方便进行属性特征查找。

## [Material材料](Material.java)

材料上面两种设计的最终使用者。在构造时需要传入对应的`MaterialFeature`以及id。其中`MaterialFeature`会自动在`Material`类中创建表，可以使用需要的MFH来获取，物品所可用的物品形态类型同理。

材料中存在一个可能会需要被覆写的方法`createItem`，这一方法与`MaterialItemType`中的功能相似。但是其可以返回null，表示回落给对应的`MaterialItemType`进行处理。
