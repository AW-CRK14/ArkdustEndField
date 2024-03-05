# BreakdownCore崩解核心 总览

崩解核心是Arkdust模组的前置，包含了Arkdust模组中主要的玩法系统基础代码。同样，这些功能也可以被用于您的mod以节约您的开发成本，提高内容的多样性与兼容性等。

请注意：由于部分功能实现了自动注册系统，且考虑到大部分情况下没有延迟注册的必要，这一部分内容的注册使用了**不安全**的Neoforge注册，也就是说对象不在注册时被创建，而是**在注册前就已经被创建并加载了**，只是此时还没有被加入注册体系中。因此，请谨慎操作这一部分内容的注册以避免出现更为严重的崩溃。您可以查看[SkippedRegister](src/main/java/com/landis/breakdowncore/unsafe/SkippedRegister.java)获取更多信息。

本mod尝试在尽量不使用mixin的前提下提供高兼容性的体系。以下是可用的功能体系:

## 材料(Material)体系

这一体系用于提供一套便利的(应该是相对比较便利的)，可拓展性较强的材料-物件体系，比如各种材料的板，钢的各种齿轮线棍子等。您可以在[这里](src/main/java/com/landis/breakdowncore/material/introduction.md)了解到更多信息。